package bellevuecollege.edu.cookpal.recipe_details

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import bellevuecollege.edu.cookpal.network.Recipe

class RecipeDetailsViewModelProvider(
    private val ingredientSearchRecipe: Recipe,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeDetailsViewModel::class.java)) {
            return RecipeDetailsViewModel(ingredientSearchRecipe, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}