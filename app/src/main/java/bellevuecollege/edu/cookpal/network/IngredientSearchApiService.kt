package bellevuecollege.edu.cookpal.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

//recipeAPI
private const val BASE_URL = "https://recipesapi.herokuapp.com"

//use Retrofit to comply with json returned from RecipeAPI
private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

//interface extending from IngredientSearchRecipesResponse
//interface IngredientSearchApiService {
//    @GET("api/v2/recipes")
//    suspend fun getRecipes(
//        @Query("key") api_key: String = "",
//        @Query("q") q: String,
//        @Query("page") page: Int = 1
//    ): IngredientSearchRecipesResponse
//}
class IngredientSearchApiService {

    suspend fun getRecipes(
       keyWord: String = "",
    ): List<Recipe> = suspendCoroutine {
        DownloadRecipesFirebase().getRecipes(keyWord) { data -> it.resume(data)}
    }
}






