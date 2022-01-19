package bellevuecollege.edu.cookpal.recipe_details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bellevuecollege.edu.cookpal.network.Recipe

class RecipeDetailsViewModel(ingredientSearchRecipe: Recipe,
                             app: Application
) : AndroidViewModel(app) {

    private val _selectedRecipe = MutableLiveData<Recipe>()
    val selectedRecipe: LiveData<Recipe>
        get() = _selectedRecipe

    init {
        _selectedRecipe.value = ingredientSearchRecipe
    }
}