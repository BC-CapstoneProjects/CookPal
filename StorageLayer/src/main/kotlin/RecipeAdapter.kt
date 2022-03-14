import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.squareup.okhttp.*
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Paths

class RecipeAdapter(configPath: String) {

    private val config: Config
    private val logger = KotlinLogging.logger {}

    init {
        val path = Paths.get(configPath)
        val mapper = ObjectMapper(YAMLFactory())
        config = Files.newBufferedReader(path).use {
            mapper.readValue(it, Config::class.java)
        }!!
    }
    
    fun download(keyword: String) {
        val query = completeQuery("\"filter\": {\n\"title\": {\"\$regex\": \"$keyword\", \"\$options\": \"i\"}\n}\n")
        logger.debug { "Download query: $query"}
        queryDB(query, "find")
    }

    fun upload(recipes: List<Recipe>) {
        val documents = "\"documents\":[\n" + recipes.joinToString(separator = ",\n") { recipe ->
            ObjectMapper().writeValueAsString(recipe)
        } + "\n]\n"

        val query = completeQuery(documents)
        logger.debug { "Upload query: $query"}

        queryDB(query, "insertMany")
    }

    private fun completeQuery(subQuery: String): String {
        return "{\n\"collection\":\"${config.collection}\",\n\"database\":\"${config.database}\",\n\"dataSource\":\"${config.dataSource}\",\n$subQuery}"
    }

    fun queryDB(query: String, action: String): ResponseBody? {
        val client = OkHttpClient()
        val mediaType = MediaType.parse("application/json")
        val body = RequestBody.create(mediaType, query)
        val request = Request.Builder()
            .url("https://data.mongodb-api.com/app/${config.apiAppID}/endpoint/data/beta/action/$action")
            .method("POST", body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Access-Control-Request-Headers", "*")
            .addHeader("api-key", config.apiKey)
            .build()
        val response = client.newCall(request).execute()
        logger.info { "Response code: ${response.code()}" }
        var responseBody = response.body()
        logger.debug { "Response body: ${responseBody.string()}" }
        return responseBody
    }
}