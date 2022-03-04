package bellevuecollege.edu.cookpal.home_screen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bellevuecollege.edu.cookpal.network.IngredientSearchApi
import bellevuecollege.edu.cookpal.network.Recipe
import bellevuecollege.edu.cookpal.recipes.IngredientSearchApiStatus
import kotlinx.coroutines.launch

enum class IngredientSearchApiStatus { LOADING, ERROR, DONE }

class HomeScreenViewModel : ViewModel() {

    private var _searchTerm: String = "rice"
    private var _searchTerm2: String = "banh mi"
    private var _searchTerm3: String = "tea"

    // The internal MutableLiveData String that stores the most recent response
    private val _status = MutableLiveData<IngredientSearchApiStatus>()

    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>>
        get() = _recipes

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
        viewModelScope.launch {
            Log.d("HomeScreenViewModel", "Retrieving recipes for ${_searchTerm}")
            _status.value = IngredientSearchApiStatus.LOADING
            try {
                val searchResponse =
                    IngredientSearchApi.retrofitIngredientSearchGetRecipes.getRecipes(
                        "",
                        _searchTerm,
                        1
                    )
                Log.d("HomeScreenViewModel", "Successfully get recipes")
                Log.d("recipe", searchResponse.toString())
                _recipes.value = searchResponse.recipes.map { recipe ->
                    Recipe(
                        rId = recipe.id,
                        title = recipe.title,
                        imgSrcUrl = recipe.imageUrl,
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

    fun getIngredientSearchRecipes2() {
        viewModelScope.launch {
            Log.d("HomeScreenViewModel", "Retrieving recipes for ${_searchTerm2}")
            _status.value = IngredientSearchApiStatus.LOADING
            try {
                val searchResponse =
                    IngredientSearchApi.retrofitIngredientSearchGetRecipes.getRecipes(
                        "",
                        _searchTerm2,
                        1
                    )
                Log.d("HomeScreenViewModel", "Successfully get recipes")
                Log.d("recipe", searchResponse.toString())
                _recipes.value = searchResponse.recipes.map { recipe ->
                    Recipe(
                        rId = recipe.id,
                        title = recipe.title,
                        imgSrcUrl = recipe.imageUrl,
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

    fun getIngredientSearchRecipes3() {
        viewModelScope.launch {
            Log.d("HomeScreenViewModel", "Retrieving recipes for ${_searchTerm3}")
            _status.value = IngredientSearchApiStatus.LOADING
            try {
                val searchResponse =
                    IngredientSearchApi.retrofitIngredientSearchGetRecipes.getRecipes(
                        "",
                        _searchTerm3,
                        1
                    )
                Log.d("HomeScreenViewModel", "Successfully get recipes")
                Log.d("recipe", searchResponse.toString())
                _recipes.value = searchResponse.recipes.map { recipe ->
                    Recipe(
                        rId = recipe.id,
                        title = recipe.title,
                        imgSrcUrl = recipe.imageUrl,
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