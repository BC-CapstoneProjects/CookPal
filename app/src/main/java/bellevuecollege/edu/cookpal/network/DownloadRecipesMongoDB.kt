package bellevuecollege.edu.cookpal.network

import RecipeAdapter
import SearchParam
import SearchType
import android.content.Context
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

class DownloadRecipesMongoDB {

    suspend fun getRecipesout(keyWord: String, context: Context): List<Recipe> {
        try
        {
            val inputStream = context.assets.open("storageAccess/config.yaml")
            val adapter = RecipeAdapter(inputStream)
            val converter = RecipeClassConverter()

            return adapter.getRecipesFromQuery(listOf(SearchParam(SearchType.TITLE, listOf(keyWord))))
                    .map { converter.convert(it) }
        }
        catch (e:Exception)
        {
            println(e.message);
            return listOf()
        }

    }

    data class Response(val documents: List<Recipe>)

    suspend fun getRecipes(keyWord: String, context: Context): List<Recipe>
    {
        val client = OkHttpClient()
        val mediaType = "application/json".toMediaTypeOrNull()

        val request = Request.Builder()
                .url("http://10.0.0.167:3000/api/recipe/title/" + keyWord)
                .addHeader("Content-Type", "application/json")
                .addHeader("Access-Control-Request-Headers", "*")
                .build()
        val response = withContext(Dispatchers.IO) {
            client.newCall(request).execute()

        }
        //logger.info { "Response code: ${response.code()}" }
        val responseBody = response.body
        println("this runs" )

        try
        {
            val responseString = withContext(Dispatchers.IO) {
                responseBody?.string()
            }

            println ( "Response body: $responseString" )
            println ( "this also runs" )
            println("Response body: $responseString")

            val typeRef = object : com.fasterxml.jackson.core.type.TypeReference<Response>() {}
            return jacksonObjectMapper().readValue(responseString, typeRef).documents
        }
        catch (e:Exception)
        {
            println(e.message);
            return listOf();
        }


    }
}