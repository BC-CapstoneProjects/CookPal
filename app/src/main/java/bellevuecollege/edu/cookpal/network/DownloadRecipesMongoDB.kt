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

    suspend fun getRecipes(keyWord: String, context: Context): List<Recipe>
    {
        return getRecipesByTitle(keyWord, context)
    }

    suspend fun getRecipesByTitle(keyWord: String, context: Context): List<Recipe>
    {
        return getRecipesFn(keyWord, context, "title")
    }

    suspend fun getRecipesByRating(keyWord: String, context: Context): List<Recipe>
    {
        return getRecipesFn(keyWord, context, "rating")
    }

    suspend fun getRecipesFn(keyWord: String, context: Context, field:String): List<Recipe>
    {
        val client = OkHttpClient()

        try
        {

            val inputStream = context.assets.open("configenv.yaml")
            val mapper = ObjectMapper(YAMLFactory())
            configenv = mapper.readValue(inputStream, ConfigEnv::class.java)

            var server = "";

            server = when (configenv.env){
                "local"->configenv.localserver
                "aws"->configenv.awsserver
                else -> {
                    throw Exception("invalid env value")
                }
            }

            val responseString = Utils.makeAPIGetRequest(server + "/api/recipe/" + field + "/" + keyWord,
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