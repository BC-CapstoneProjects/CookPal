package bellevuecollege.edu.cookpal.recipe_details

import android.app.Application
import androidx.lifecycle.*
import bellevuecollege.edu.cookpal.network.Recipe
import bellevuecollege.edu.cookpal.recipe_parser.extractAllRecipesInformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeDetailsViewModel(
    ingredientSearchRecipe: Recipe,
    app: Application
) : AndroidViewModel(app) {

    private val _selectedRecipe = MutableLiveData<Recipe>()
    val selectedRecipe: LiveData<Recipe>
        get() = _selectedRecipe

    init {
        viewModelScope.launch {
            // Parse recipe info from Recipe source_url. Details include but not limited to: summary,
            // ingredients and cooking instructions
            // Get Recipe details. We need to wrap jsoup call in a coroutine as Jsoup get() takes time
            withContext(Dispatchers.IO) {
                extractAllRecipesInformation(ingredientSearchRecipe)
            }
            _selectedRecipe.value = ingredientSearchRecipe
        }
    }
}