import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.squareup.okhttp.*
import java.nio.file.Files
import java.nio.file.Paths

class RecipeAdapter(configPath: String) {

    private val config: Config

    init {
        val path = Paths.get(configPath)
        val mapper = ObjectMapper(YAMLFactory())
        config = Files.newBufferedReader(path).use {
            mapper.readValue(it, Config::class.java)
        }!!
    }
    //TODO: Sanitize user input
    fun getRecipesFromQuery(param: List<SearchParam>) {
        val queryFilter = "\"filter\":{\n" + param.joinToString(",\n") { getQueryFromEnum(it) } + "\n}\n"
        println(completeQuery(queryFilter))
        queryDB(completeQuery(queryFilter), "find")
    }

    private fun getQueryFromEnum(param: SearchParam): String {
        when (param.searchType) {
            SearchType.KEYWORD -> return getFieldQuery("title", param.searchValues)
            SearchType.INGREDIENT -> return getFieldArrayQuery("ingredients", param.searchValues)

        }
        return ""
    }

    private fun getFieldQuery(field: String, values: List<String>): String {
        return getRegexQuery(field, getSingleFieldRegex(field, values), "i")
    }

    private fun getSingleFieldRegex(field: String, values: List<String>): String {
        return "^" + values.joinToString(separator = "") { "(?=.*$it)" } + ".+"
    }

    private fun getFieldArrayQuery(field: String, values: List<String>): String {
        return values.joinToString(separator = ",\n") { getRegexQuery(field, it, "i") }
    }

    fun getRecipesByKeyword(keyword: String) {
        val query = "\"filter\": {\n\"title\": {\"\$regex\": \"$keyword\", \"\$options\": \"i\"}\n}\n"
        println(completeQuery(query))
        queryDB(completeQuery(query), "find")
    }


    fun getRegexQuery(field: String, regex: String, options: String): String {
        return "\"$field\": {\"\$regex\": \"$regex\", \"\$options\": \"$options\"}"
    }

    fun joinSearchQueries(queries: List<String>): String {
        return "\"filter\":{\n" + queries.joinToString(separator = ",\n") + "\n}\n"
    }


    fun upload(recipes: List<Recipe>) {
        val documents = "\"documents\":[\n" + recipes.joinToString(separator = ",\n") { recipe ->
            ObjectMapper().writeValueAsString(recipe)
        } + "\n]\n"

        println(completeQuery(documents))
        queryDB(completeQuery(documents), "insertMany")
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
        println(response.body().string())
        return response.body()
    }
}