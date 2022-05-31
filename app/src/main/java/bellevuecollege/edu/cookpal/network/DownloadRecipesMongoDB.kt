package bellevuecollege.edu.cookpal.network

import android.content.Context
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

    suspend fun getRecipes(keyWord: String, context: Context, id:String = ""): List<Recipe>
    {
        val client = OkHttpClient()

        try
        {
            val server:String = Utils.getServerUrl(context)

            val url:String = server + "/api/recipe/title/" + keyWord + "?cid=" + id
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