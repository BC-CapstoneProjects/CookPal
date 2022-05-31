package bellevuecollege.edu.cookpal.recipes

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bellevuecollege.edu.cookpal.network.DownloadRecipesMongoDB
import bellevuecollege.edu.cookpal.network.Recipe
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.socket.client.Socket
import kotlinx.coroutines.launch
import org.json.JSONObject


enum class IngredientSearchApiStatus { LOADING, ERROR, DONE }

class RecipeResultsViewModel(application: Application) : AndroidViewModel(application) {
    private var ALL_RECIPES_NAME: String = "allrecipes"
    private var _searchTerm: String = "banh mi"
    private val context = getApplication<Application>().applicationContext

    data class Response(val document: Recipe)

    // The internal MutableLiveData String that stores the most recent response
    private val _status = MutableLiveData<IngredientSearchApiStatus>()

    // The external immutable LiveData for the response String
    val status: LiveData<IngredientSearchApiStatus>
        get() = _status

    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>>
        get() = _recipes

    private val _navigateToSelectedRecipe = MutableLiveData<Recipe?>()
    val navigateToSelectedRecipe: MutableLiveData<Recipe?>
        get() = _navigateToSelectedRecipe

    private val _searchButtonVisible = MutableLiveData<Boolean?>()
    val searchButtonVisible: LiveData<Boolean?>
        get() = _searchButtonVisible

    fun setSearchTerm(searchKeyWord: String) {
        _searchTerm = searchKeyWord
        _searchButtonVisible.value = searchKeyWord.isNotEmpty()

    }

    private lateinit var sk:Socket
    private lateinit var uuid:String
    private var recs:ArrayList<Recipe> = ArrayList()

    fun setSocketObject(psk: Socket, puuid:String) {
        sk = psk
        uuid = puuid

        sk?.on("senddata") { paramters->
            println("mSocket func")
            var ob = paramters[0] as JSONObject

            println(ob.toString())

            val typeRef = object : com.fasterxml.jackson.core.type.TypeReference<bellevuecollege.edu.cookpal.network.Recipe>() {}

            var rep:Recipe = jacksonObjectMapper().readValue(ob.toString(), typeRef)

            recs.add(rep)

            var recs2:ArrayList<Recipe> = ArrayList()

            for (item in recs)
            {
                recs2.add(item.copy())
            }

            viewModelScope.launch {

               try
               {
                   _recipes.value = recs2
               }
               catch (e:Exception)
               {

               }
            }
        }
    }

    /**
     * Call getIngredientSearchRecipes() on init so we can display status immediately.
     */
    init {
        getIngredientSearchRecipes()
    }

    /**
     * Sets the value of the status LiveData to the IngredientSearch API status.
     */
    fun getIngredientSearchRecipes() {
        recs = ArrayList()

        viewModelScope.launch {
            Log.d("RecipeResultsViewModel", "Retrieving recipes for $_searchTerm")
            _status.value = IngredientSearchApiStatus.LOADING
            try {
                val searchResponse =
                    DownloadRecipesMongoDB().getRecipes(

                        _searchTerm,context,uuid

                    )
                Log.d("RecipeResultsViewModel", "Successfully get recipes")

                _recipes.value = searchResponse
                _status.value = IngredientSearchApiStatus.DONE
            } catch (e: Exception) {
                _status.value = IngredientSearchApiStatus.ERROR
                _recipes.value = ArrayList()
            }
        }
    }

    fun displayRecipeDetails(recipe: Recipe) {
        _navigateToSelectedRecipe.value = recipe
    }

    fun displayRecipeDetailsComplete() {
        _navigateToSelectedRecipe.value = null
    }
}