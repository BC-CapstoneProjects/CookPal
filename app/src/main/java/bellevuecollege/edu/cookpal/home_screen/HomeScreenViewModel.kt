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
import kotlinx.coroutines.launch

enum class IngredientSearchApiStatus { LOADING, ERROR, DONE }

class HomeScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext
    // The internal MutableLiveData String that stores the most recent response
    private val _status = MutableLiveData<IngredientSearchApiStatus>()

    private val _popularNow = MutableLiveData<Recipe>()
    val popularNow: LiveData<Recipe>
        get() = _popularNow

    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>>
        get() = _recipes

    /**
     * Call getIngredientSearchRecipes() on init so we can display status immediately.
     */
    init {
        getIngredientSearchRecipes4()
        getPopularNow()
    }

    fun getPopularNow(){
        viewModelScope.launch {
            Log.d("HomeScreenViewModel", "Retrieving recipes for 4.6 ratings")
            _status.value = IngredientSearchApiStatus.LOADING
            try {
                val searchResponse =
                        DownloadRecipesMongoDB().getRecipesByRating(
                                "4.6",context
                        )
                Log.d("HomeScreenViewModel", "Successfully get recipes")
                Log.d("recipe", searchResponse.toString())
                _popularNow.value = searchResponse[0]
            } catch (e: Exception) {

            }
        }
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
                        imageUrl = recipe.imageUrl,
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
                        imageUrl = recipe.imageUrl,
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
                        imageUrl = recipe.imageUrl,
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
                        imageUrl = recipe.imageUrl,
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