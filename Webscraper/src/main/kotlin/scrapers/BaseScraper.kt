package scrapers

import DriverPool
import Recipe
import RecipeJSON
import mu.KotlinLogging
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

abstract class BaseScraper {
    fun retrieveRecipes(
        keyword: String,
        numPages: Int,
        numDrivers: Int,
        writeLocation: String
    ) {
        val logger = KotlinLogging.logger {}
        logger.info { "Retrieving recipes for keyword $keyword with $numPages page(s) and $numDrivers driver(s)" }
        val driverPool = DriverPool(numDrivers)
        val allRecipes = returnRecipesFromKeyword(keyword, driverPool, numPages)
        logger.info { "Found ${allRecipes.size} recipes" }
        val jsonWriter = RecipeJSON(writeLocation)
        jsonWriter.addRecipes(allRecipes)
        logger.info { "Wrote all recipes to JSON" }
        driverPool.closeDrivers()
    }

    protected abstract fun parseRecipeHtml(html: Document, url: String): Recipe
    protected abstract fun getUrlForPage(page: Number, keyWord: String): String
    protected abstract fun getRecipeUrlsFromPage(html: Document): List<String>
    protected fun getFirstOrEmptyText(html: Document, cssQuery: String): String {
        val result = html.select(cssQuery)
        if (result.isEmpty()) {
            return ""
        }
        return result[0].ownText()
    }

    protected fun getFirstOrEmptyAttr(html: Document, cssQuery: String, attr: String): String {
        val result = html.select(cssQuery)
        if (result.isEmpty()) {
            return ""
        }
        return result[0].attr(attr)
    }

    private fun returnRecipesFromKeyword(
        keyWord: String,
        driverPool: DriverPool,
        pageLimit: Int
    ): List<Recipe> {
        val pageUrls = ArrayList<String>()
        for (i in 0 until pageLimit) {
            pageUrls.add(getUrlForPage(i + 1, keyWord))
        }
        val pageResults = driverPool.getOutputs(pageUrls)
        val recipeUrls = ArrayList<String>()
        for (page in pageResults) {
            recipeUrls.addAll(getRecipeUrlsFromPage(Jsoup.parse(page)))
        }

        val recipeResults = driverPool.getOutputs(recipeUrls.toList())
        return recipeResults.mapIndexed { index, recipe ->
            parseRecipeHtml(
                Jsoup.parse(recipe),
                recipeUrls[index]
            )
        }
    }
}