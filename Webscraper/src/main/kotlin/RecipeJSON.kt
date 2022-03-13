import mu.KotlinLogging
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileWriter
import java.io.PrintWriter
import java.nio.charset.Charset

private val logger = KotlinLogging.logger {}

class RecipeJSON {
    private val path: String

    constructor(writePath: String) {
        path = writePath
    }

    fun addRecipes(recipes: List<Recipe>) {
        val fullJSON = JSONArray()
        for (recipe in recipes) {
            val recipeJson = JSONObject()
            recipeJson.put("title", recipe.title)
            recipeJson.put("steps", recipe.steps)
            recipeJson.put("imgUrl", recipe.imgUrl)
            recipeJson.put("rating", recipe.rating)
            recipeJson.put("reviewNumber", recipe.reviewNumber)
            recipeJson.put("ingredients",recipe.ingredients)
            recipeJson.put("totalTime", recipe.totalTime)
            recipeJson.put("sourceUrl", recipe.sourceUrl)
            fullJSON.put(recipeJson)
        }

        try {
            PrintWriter(FileWriter(path, Charset.defaultCharset()))
                .use { it.write(fullJSON.toString()) }
        } catch (e: Exception) {
            logger.error { e }
        }
    }
}