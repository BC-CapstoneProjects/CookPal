package bellevuecollege.edu.cookpal.network

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class IngredientSearchApiService {
    suspend fun getRecipes(
        keyWord: String = "",
    ): List<Recipe> = suspendCoroutine {
        DownloadRecipesFirebase().getRecipes(keyWord) { data -> it.resume(data) }
    }
}






