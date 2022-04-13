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
        val client = OkHttpClient()

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