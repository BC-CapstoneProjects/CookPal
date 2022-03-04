package apilib

import org.json.JSONArray
import org.json.JSONObject
import java.io.FileWriter
import java.io.PrintWriter
import java.nio.charset.Charset

class RecipeJSON {
    private val path: String

    constructor(writePath: String) {
        path = writePath
    }

    fun addRecipes(recipes: List<Recipe>) {
        val fullJSON = JSONArray()
        for (recipe in recipes){
            val recipeJson = JSONObject()
            recipeJson.put("Title", recipe.title)
            recipeJson.put("Steps", recipe.steps)
            recipeJson.put("ImageURL", recipe.imgUrl)
            recipeJson.put("Rating", recipe.rating)
            recipeJson.put("ReviewNumber", recipe.reviewNumber)
            recipeJson.put("Ingredients",recipe.ingredients)
            recipeJson.put("TotalTime", recipe.totalTime)
            recipeJson.put("SourceUrl", recipe.sourceUrl)
            fullJSON.put(recipeJson)
        }

        try {
            PrintWriter(FileWriter(path, Charset.defaultCharset()))
                .use { it.write(fullJSON.toString()) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}