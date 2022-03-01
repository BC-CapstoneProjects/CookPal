package bellevuecollege.edu.cookpal.network

import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Query

//recipeAPI
private const val BASE_URL = "https://recipesapi.herokuapp.com"
//use Retrofit to comply with json returned from RecipeAPI
private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

//interface extending from IngredientSearchRecipesResponse
interface IngredientSearchApiService {
    @GET("api/v2/recipes")
    suspend fun getRecipes(
        @Query("key") api_key: String = "",
        @Query("q") q: String,
        @Query("page") page: Int = 1
    ): IngredientSearchRecipesResponse
}

interface RecipeDetailsApiService {
    @GET("api/get")
    suspend fun getRecipeDetails(
        @Query("rId") recipeId: String
    ): RecipeDetailsResponseMetadata
}

object IngredientSearchApi {
    val retrofitIngredientSearchGetRecipes : IngredientSearchApiService by lazy {
        retrofit.create(IngredientSearchApiService::class.java) }

    val retrofitGetRecipeDetails : RecipeDetailsApiService by lazy {
        retrofit.create(RecipeDetailsApiService::class.java)
    }
}