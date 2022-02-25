package apilib

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

abstract class GeneralAPI {
    fun retrieveRecipes(
        keyword: String,
        numPages: Int,
        numDrivers: Int,
        writeLocation: String
    ) {
        val driverPool = DriverPool(numDrivers)
        val allRecipes = returnRecipesFromKeyword(keyword, driverPool, numPages)
        val jsonWriter = RecipeJSON(writeLocation)
        jsonWriter.addRecipes(allRecipes)
        driverPool.closeDrivers()
    }

    protected abstract fun retrieveRecipes(keyword: String, numPages: Int, numDrivers: Int)
    protected abstract fun parseRecipeHtml(html: Document): Recipe
    protected abstract fun getUrlForPage(page: Number, keyWord: String): String
    protected abstract fun getRecipeUrlsFromPage(html: Document): List<String>
    private fun returnRecipesFromKeyword(
        keyWord: String,
        driverPool: DriverPool,
        pageLimit: Int
    ): ArrayList<Recipe> {
        val pageUrls = ArrayList<String>()
        for (i in 0 until pageLimit) {
            pageUrls.add(getUrlForPage(i + 1, keyWord))
        }
        val pageResults = driverPool.getOutputs(pageUrls)
        val recipeUrls = HashSet<String>()
        for (page in pageResults) {
            recipeUrls.addAll(getRecipeUrlsFromPage(Jsoup.parse(page)))
        }

        val recipeResults = driverPool.getOutputs(recipeUrls.toList())
        val recipes = ArrayList<Recipe>()
        for (recipe in recipeResults) {
            recipes.add(parseRecipeHtml(Jsoup.parse(recipe)))
        }
        return recipes
    }
}