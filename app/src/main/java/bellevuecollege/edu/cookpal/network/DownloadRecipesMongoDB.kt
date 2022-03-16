package bellevuecollege.edu.cookpal.network

import RecipeAdapter
import SearchParam
import SearchType
import android.content.Context

class DownloadRecipesMongoDB {

    suspend fun getRecipes(keyWord: String, context: Context): List<Recipe> {
        val inputStream = context.assets.open("storageAccess/config.yaml")
        val adapter = RecipeAdapter(inputStream)
        val converter = RecipeClassConverter()

        return adapter.getRecipesFromQuery(listOf(SearchParam(SearchType.TITLE, listOf(keyWord))))
            .map { converter.convert(it) }
    }
}