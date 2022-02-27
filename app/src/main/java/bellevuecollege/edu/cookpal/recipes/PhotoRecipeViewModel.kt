package bellevuecollege.edu.cookpal.recipes

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bellevuecollege.edu.cookpal.favorites.PhotoRecipe
import bellevuecollege.edu.cookpal.network.IngredientSearchApi
import bellevuecollege.edu.cookpal.network.Recipe
import com.google.android.gms.common.util.MapUtils
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.lang.Exception

class PhotoRecipeViewModel : ViewModel() {
    private val TAG = "PhotoRecipeViewModel"
    private val PHOTO_RECIPES_NAME = "photo_recipes"

    private lateinit var databaseRef: DatabaseReference

    // The internal MutableLiveData String that stores the most recent response
    private val _status = MutableLiveData<IngredientSearchApiStatus>()

    // The external immutable LiveData for the response String
    val status: LiveData<IngredientSearchApiStatus>
        get() = _status

    private val _navigateToSelectedRecipe = MutableLiveData<Recipe>()
    val navigateToSelectedRecipe: LiveData<Recipe>
        get() = _navigateToSelectedRecipe

    private val _photo_recipes = MutableLiveData<List<Recipe?>>()
    val photo_recipes: LiveData<List<Recipe?>>
        get() = _photo_recipes

    init {
        databaseRef = FirebaseDatabase.getInstance().getReference(PHOTO_RECIPES_NAME)
        getPhotoRecipesFromFirebaseStorage()
    }

    private fun getPhotoRecipesFromFirebaseStorage() {
        viewModelScope.launch {
            _status.value = IngredientSearchApiStatus.LOADING
            try {
                val getDataListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot!!.exists()) {
                            _photo_recipes.value = dataSnapshot.children
                                .map {
                                    e ->
                                    e.getValue(PhotoRecipe::class.java)?.let {
                                        Recipe(
                                            rId = "FIREBASE_DATA",
                                            title = it.name,
                                            imgSrcUrl = it.filePath,
                                            sourceUrl = "",
                                            cookingInstructions = it.steps,
                                            ingredients = it.ingredients,
                                            summary = it.summary
                                        )
                                    }
                                }
                            Log.d(TAG, _photo_recipes.value.toString())
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Getting Post failed, log a message
                        Log.w(
                            TAG,
                            "Failed to read data from Firebase Realtime Database",
                            databaseError.toException()
                        )
                    }
                }

                databaseRef.addValueEventListener(getDataListener)
                _status.value = IngredientSearchApiStatus.DONE
            } catch (e: Exception) {
                _status.value = IngredientSearchApiStatus.ERROR
                _photo_recipes.value = ArrayList()
            }
        }
    }

    fun displayPhotoRecipeDetails(recipe: Recipe) {
        _navigateToSelectedRecipe.value = recipe
    }

    fun displayPhotoRecipeDetailsComplete() {
        _navigateToSelectedRecipe.value = null
    }
}