package bellevuecollege.edu.cookpal.network

import android.content.Context
import bellevuecollege.edu.cookpal.recipes.RecipeFilter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class DownloadRecipesMongoDB {

    private lateinit var configenv: ConfigEnv

    data class Response(val documents: List<Recipe>)

    suspend fun getRecipesByTitle(keyWord: String, context:Context, id:String = "", filter:RecipeFilter? = null): List<Recipe>
    {
        return getRecipesFn(keyWord, context, "title", id, filter)
    }

    suspend fun getRecipesByRating(keyWord: String, context: Context): List<Recipe>
    {
        return getRecipesFn(keyWord, context, "rating")
    }

    suspend fun getRecipesFn(keyWord: String, context: Context, field:String, id:String = "", filter:RecipeFilter? = null): List<Recipe>
    {
        val client = OkHttpClient()

        try
        {
            val server:String = Utils.getServerUrl(context)

            var url:String = server + "/api/recipe/" + field + "/" + keyWord
          
            if (filter != null) {
                url += "?cid=" + id + "&usefilter&" + filter.ToQueryString()
            }
            else {
                url += "?cid=" + id
            }
            
            val responseString = Utils.makeAPIGetRequest(url,

        mapOf("Content-Type" to "application/json", "Access-Control-Request-Headers" to "*"))

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