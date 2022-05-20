package bellevuecollege.edu.cookpal.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class Utils {
    companion object {
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