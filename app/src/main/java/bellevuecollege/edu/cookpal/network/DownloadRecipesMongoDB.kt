package bellevuecollege.edu.cookpal.network

import Config
import RecipeAdapter
import SearchParam
import SearchType
import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.nio.file.Files
import java.nio.file.Paths

class DownloadRecipesMongoDB {

    private lateinit var configenv: ConfigEnv
    private lateinit var config: Config

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



        try
        {

            val inputStream = context.assets.open("configenv.yaml")
            val mapper = ObjectMapper(YAMLFactory())
            configenv = mapper.readValue(inputStream, ConfigEnv::class.java)

            var server = "";

            if (configenv.env == "local")
            {
                server = configenv.localserver;
            }
            else if (configenv.env == "aws")
            {
                server = configenv.awsserver
            }
            else
            {
                throw Exception("invalid env value");
            }

            val request = Request.Builder()
                    .url(server + "/api/recipe/title/" + keyWord)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Access-Control-Request-Headers", "*")
                    .build()
            val response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()

            }
            //logger.info { "Response code: ${response.code()}" }
            val responseBody = response.body
            println("this runs" )

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