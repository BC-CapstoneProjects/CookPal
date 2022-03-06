package bellevuecollege.edu.cookpal.recipes

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bellevuecollege.edu.cookpal.network.IngredientSearchApi
import bellevuecollege.edu.cookpal.network.Recipe
import kotlinx.coroutines.launch

enum class IngredientSearchApiStatus { LOADING, ERROR, DONE }

class RecipeResultsViewModel : ViewModel() {
    private var ALL_RECIPES_NAME: String = "allrecipes"
    private var _searchTerm: String = "banh mi"

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
            Log.d("RecipeResultsViewModel", "Retrieving recipes for ${_searchTerm}")
            _status.value = IngredientSearchApiStatus.LOADING
            try {
                val searchResponse =
                    IngredientSearchApi.retrofitIngredientSearchGetRecipes.getRecipes(
                        "",
                        _searchTerm,
                        1
                    )
                Log.d("RecipeResultsViewModel", "Successfully get recipes")

                _recipes.value = searchResponse.recipes
                    .filter {
                        it.sourceUrl.contains(ALL_RECIPES_NAME)
                    }
                    .map { recipe ->
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

    fun displayRecipeDetails(recipe: Recipe) {
        _navigateToSelectedRecipe.value = recipe
    }

    fun displayRecipeDetailsComplete() {
        _navigateToSelectedRecipe.value = null
    }
}