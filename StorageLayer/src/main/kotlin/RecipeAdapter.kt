import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths


class RecipeAdapter {

    private val config: Config
//    private val logger = KotlinLogging.logger {}

    constructor(inputStream: InputStream) {
        val mapper = ObjectMapper(YAMLFactory())
        config = mapper.readValue(inputStream, Config::class.java)
    }

    constructor(configPath: String = "src/main/resources/config.yaml") {
        val path = Paths.get(configPath)
        val mapper = ObjectMapper(YAMLFactory())
        config = Files.newBufferedReader(path).use {
            mapper.readValue(it, Config::class.java)
        }!!
    }

    data class Response(val documents: List<Recipe>)

    //TODO: Sanitize user input
    suspend fun getRecipesFromQuery(param: List<SearchParam>): List<Recipe> {
        val queryFilter =
            "\"filter\":{\n" + param.joinToString(",\n") { getQueryFromEnum(it) } + "\n}\n"
        return queryDB(completeQuery(queryFilter), "find")
    }

    private fun getQueryFromEnum(param: SearchParam): String {
        when (param.searchType) {
            SearchType.TITLE -> return getFieldQuery("title", param.searchValues)
            SearchType.INGREDIENT -> return getFieldArrayQuery("ingredients", param.searchValues)

        }
        return ""
    }

    private fun getFieldQuery(field: String, values: List<String>): String {
        return getRegexQuery(field, getSingleFieldRegex(values), "i")
    }

    private fun getSingleFieldRegex(values: List<String>): String {
        return "^" + values.joinToString(separator = "") { "(?=.*$it)" } + ".+"
    }

    private fun getFieldArrayQuery(field: String, values: List<String>): String {
        return values.joinToString(separator = ",\n") { getRegexQuery(field, it, "i") }
    }

    private fun getRegexQuery(field: String, regex: String, options: String): String {
        return "\"$field\": {\"\$regex\": \"$regex\", \"\$options\": \"$options\"}"
    }

    suspend fun upload(recipes: List<Recipe>): List<Recipe> {
        val documents = "\"documents\":[\n" + recipes.joinToString(separator = ",\n") { recipe ->
            ObjectMapper().writeValueAsString(convertUploadRecipes(recipe))
        } + "\n]\n"
        println(documents)
        return queryDB(completeQuery(documents), "insertMany")
    }

    private fun convertUploadRecipes(recipe: Recipe): UploadRecipe {

        return UploadRecipe(
            title = recipe.title,
            steps = recipe.steps,
            imgUrl = recipe.imgUrl,
            sourceUrl = recipe.sourceUrl,
            ingredients = recipe.ingredients,
            reviewNumber = recipe.reviewNumber,
            rating = recipe.rating,
            totalTime = recipe.totalTime
        )

    }

    data class UploadRecipe(
        var label: String = "",
        var title: String = "",
        var steps: ArrayList<String> = arrayListOf(),
        var imgUrl: String = "",
        var sourceUrl: String = "",
        var ingredients: ArrayList<String> = arrayListOf(),
        var reviewNumber: Number = 0,
        var rating: String = "",
        var totalTime: String = "",
        var cuisineType: ArrayList<String> = arrayListOf(),
        var mealType: ArrayList<String> = arrayListOf(),
        var dishType: ArrayList<String> = arrayListOf(),
    )

    private fun completeQuery(subQuery: String): String {
        return "{\n\"collection\":\"${config.collection}\",\n\"database\":\"${config.database}\",\n\"dataSource\":\"${config.dataSource}\",\n$subQuery}"
    }

    private suspend fun queryDB(query: String, action: String): List<Recipe> {
        println("Query: $query")
       println("Query: $config")
        val client = OkHttpClient()
        val mediaType = "application/json".toMediaTypeOrNull()
        val body = RequestBody.create(mediaType, query)
        val request = Request.Builder()
            .url("https://data.mongodb-api.com/app/${config.apiAppID}/endpoint/data/beta/action/$action")
            .method("POST", body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Access-Control-Request-Headers", "*")
            .addHeader("api-key", config.apiKey)
            .build()
        val response = withContext(Dispatchers.IO) {
            client.newCall(request).execute()

        }
        //logger.info { "Response code: ${response.code()}" }
        val responseBody = response.body
       println("this runs" )
        val responseString = responseBody?.string()
        println ( "Response body: $responseString" )
       println ( "this also runs" )
        println("Response body: $responseString")
        if (action == "find"){
            val typeRef = object : com.fasterxml.jackson.core.type.TypeReference<Response>() {}
            return jacksonObjectMapper().readValue(responseString, typeRef).documents
        }
        return listOf()
    }
}