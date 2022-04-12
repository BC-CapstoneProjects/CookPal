package bellevuecollege.edu.cookpal.home_screen

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bellevuecollege.edu.cookpal.network.DownloadRecipesMongoDB
import bellevuecollege.edu.cookpal.network.Recipe
import bellevuecollege.edu.cookpal.recipes.IngredientSearchApiStatus
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.launch

enum class IngredientSearchApiStatus { LOADING, ERROR, DONE }

class HomeScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext
    // The internal MutableLiveData String that stores the most recent response
    private val _status = MutableLiveData<IngredientSearchApiStatus>()

    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>>
        get() = _recipes

    /**
     * Call getIngredientSearchRecipes() on init so we can display status immediately.
     */
    init {

        /**
         * Translate Test
         */
        val words = "I am cool"
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.SPANISH)
            .build()
        val engJapTranslator = Translation.getClient(options)
        var conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        //download language model
        //Language models are around 30MB, don't have too many.
        engJapTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                // Model downloaded successfully. Okay to start translating
                // Set a flag, unhide the translation UI, etc.
                Log.d("Translator", "Model download successful")

                //Translate text if download successful
                engJapTranslator.translate(words)
                    .addOnSuccessListener {
                        Log.d("Translated text", it)
                    }
                    .addOnFailureListener {
                        Log.e("Translated text", "Failed to translate")
                    }
            }
            .addOnFailureListener {
                //Model couldn't be downloaded or other internal error
                Log.e("Translator", "Model download failed")
            }
        getIngredientSearchRecipes4()
    }


    /**
     * Sets the value of the status LiveData to the IngredientSearch API status.
     * Searches rice and is used in the toggle buttons on the home screen function
     * Look into button settings, home_screen_fragment.xml for more.
     */
    fun getIngredientSearchRecipes() {
        viewModelScope.launch {
            Log.d("HomeScreenViewModel", "Retrieving recipes for rice")
            _status.value = IngredientSearchApiStatus.LOADING
            try {
                val searchResponse =
                    DownloadRecipesMongoDB().getRecipes(
                        "rice",context
                    )
                Log.d("HomeScreenViewModel", "Successfully get recipes")
                Log.d("recipe", searchResponse.toString())
                _recipes.value = searchResponse.map { recipe ->
                    Recipe(
                        title = recipe.title,
                        imgUrl = recipe.imgUrl,
                        sourceUrl = recipe.sourceUrl
                    )
                }
                _status.value = IngredientSearchApiStatus.DONE
            } catch (e: Exception) {
                _status.value = IngredientSearchApiStatus.ERROR
                _recipes.value = ArrayList()
            }
        }
    }

    /**
     * Searches burger for the toggle button
     */
    fun getIngredientSearchRecipes2() {
        viewModelScope.launch {
            Log.d("HomeScreenViewModel", "Retrieving recipes for burger")
            _status.value = IngredientSearchApiStatus.LOADING
            try {
                val searchResponse =
                    DownloadRecipesMongoDB().getRecipes(
                        "burger", context

                    )
                Log.d("HomeScreenViewModel", "Successfully get recipes")
                Log.d("recipe", searchResponse.toString())
                _recipes.value = searchResponse.map { recipe ->
                    Recipe(

                        title = recipe.title,
                        imgUrl = recipe.imgUrl,
                        sourceUrl = recipe.sourceUrl
                    )
                }
                _status.value = IngredientSearchApiStatus.DONE
            } catch (e: Exception) {
                _status.value = IngredientSearchApiStatus.ERROR
                _recipes.value = ArrayList()
            }
        }
    }

    /**
     * Searches drink for the toggle button
     */
    fun getIngredientSearchRecipes3() {
        viewModelScope.launch {
            Log.d("HomeScreenViewModel", "Retrieving recipes for drink")
            _status.value = IngredientSearchApiStatus.LOADING
            try {
                val searchResponse =
                    DownloadRecipesMongoDB().getRecipes(

                        "drink", context

                    )
                Log.d("HomeScreenViewModel", "Successfully get recipes")
                Log.d("recipe", searchResponse.toString())
                _recipes.value = searchResponse.map { recipe ->
                    Recipe(

                        title = recipe.title,
                        imgUrl = recipe.imgUrl,
                        sourceUrl = recipe.sourceUrl
                    )
                }
                _status.value = IngredientSearchApiStatus.DONE
            } catch (e: Exception) {
                _status.value = IngredientSearchApiStatus.ERROR
                _recipes.value = ArrayList()
            }
        }
    }

    /**
     *  Searches rice, burger, drink for toggle button
     */
    fun getIngredientSearchRecipes4() {
        viewModelScope.launch {
            Log.d("HomeScreenViewModel", "Retrieving recipes for drink")
            _status.value = IngredientSearchApiStatus.LOADING
            try {
                val searchResponse =
                    DownloadRecipesMongoDB().getRecipes("rice", context) +
                            DownloadRecipesMongoDB().getRecipes("bacon", context) +
                            DownloadRecipesMongoDB().getRecipes("drink", context)
                Log.d("HomeScreenViewModel", "Successfully get recipes")
                Log.d("recipe", searchResponse.toString())
                _recipes.value = searchResponse.map { recipe ->
                    Recipe(
                        title = recipe.title,
                        imgUrl = recipe.imgUrl,
                        sourceUrl = recipe.sourceUrl
                    )
                }
                _status.value = IngredientSearchApiStatus.DONE
            } catch (e: Exception) {
                _status.value = IngredientSearchApiStatus.ERROR
                _recipes.value = ArrayList()
            }
        }
    }
}