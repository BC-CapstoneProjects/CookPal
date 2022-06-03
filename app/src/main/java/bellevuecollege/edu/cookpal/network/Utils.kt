package bellevuecollege.edu.cookpal.network

import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class Utils {
    companion object {
        fun getServerUrl(context: Context):String{
            val inputStream = context.assets.open("configenv.yaml")
            val mapper = ObjectMapper(YAMLFactory())
            val configenv:ConfigEnv = mapper.readValue(inputStream, ConfigEnv::class.java)

            var server = "";

            server = when (configenv.env){
                "local"->configenv.localserver
                "aws"->configenv.awsserver
                else -> {
                    throw Exception("invalid env value")
                }
            }

            return server
        }

        suspend fun makeAPIGetRequest(url: String, headers: Map<String, String>): String? {
            var request = Request.Builder()
                    .url(url)

            headers.forEach { entry ->
                request = request.addHeader(entry.key, entry.value)
            }

            return performRequest(request.build());
        }

        suspend fun makeAPIPostRequest(url: String, headers: Map<String, String>, data: RequestBody): String? {
            var request = Request.Builder()
                    .url(url).method("POST", data)

            headers.forEach { entry ->
                request = request.addHeader(entry.key, entry.value)
            }

            return performRequest(request.build());
        }

        private suspend fun performRequest(request: Request): String? {
            val client = OkHttpClient()

            val response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()

            }
            //logger.info { "Response code: ${response.code()}" }
            val responseBody = response.body

            val responseString = withContext(Dispatchers.IO) {
                responseBody?.string()
            }

            return responseString;
        }
    }
}