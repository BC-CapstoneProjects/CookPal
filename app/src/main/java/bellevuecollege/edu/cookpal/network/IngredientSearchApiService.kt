package bellevuecollege.edu.cookpal.network

import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Query

private const val BASE_URL = "https://recipesapi.herokuapp.com"

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface IngredientSearchApiService {
    @GET("api/v2/recipes")
    suspend fun getRecipes(
        @Query("key") api_key: String = "",
        @Query("q") q: String,
        @Query("page") page: Int = 1
    ): IngredientSearchRecipesResponse
}

object IngredientSearchApi {
    val retrofitIngredientSearchGetRecipes : IngredientSearchApiService by lazy {
        retrofit.create(IngredientSearchApiService::class.java) }
}