package apilib

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class MainKotlin {
    fun main() {

        val keyWord = "fried rice"
        val driverPool = DriverPool(3)
        val allRecipes = returnRecipesFromKeyword(convertKeyWord(keyWord), driverPool, 6)

        val ans = allRecipes.sortedBy { it.title }
        for (recipe in ans)
            println(recipe.toString())
        println(ans.size)

        driverPool.closeDrivers()
    }

    private fun convertKeyWord(keyWord: String): String {
        return "${keyWord.replace(' ', '-')}-"
    }

    /**
     * Return recipe objects given search keyword, driver, and page limit
     */
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
            recipeUrls.addAll(parseRecipeUrls(Jsoup.parse(page)))
        }

        val recipeResults = driverPool.getOutputs(recipeUrls.toList())
        val recipes = ArrayList<Recipe>()
        for (recipe in recipeResults) {
            recipes.add(parseRecipeHtml(Jsoup.parse(recipe)))
        }
        return recipes
    }


    /**
     * Return the url given a keyword and page number
     */
    private fun getUrlForPage(page: Number, keyWord: String): String {
        return "https://www.foodnetwork.com/search/$keyWord/p/$page/CUSTOM_FACET:RECIPE_FACET"
    }

    /**
     * Return recipe urls from a given search page
     */
    private fun parseRecipeUrls(html: Document): List<String> {
        return html.select(".o-RecipeResult .l-List .m-MediaBlock__a-Headline a")
            .map { col -> col.attr("href") }.map { recipe -> "https:$recipe" }

    }

    /**
     * Given the html source code, return a recipe object
     */
    private fun parseRecipeHtml(html: Document): Recipe {
        val recipe = Recipe()
        val titleArr =
            html.select(".m-RecipeSummary .assetTitle .o-AssetTitle__a-HeadlineText:first-of-type")
                .map { col -> col.ownText() }
        if (titleArr.isEmpty())
            return recipe


        recipe.title = titleArr[0].toString()

        recipe.steps = html.select(".o-Method__m-Body li")
            .map { col -> col.ownText() }.toTypedArray()

        recipe.imgUrl = html.select(".recipe-lead .m-MediaBlock__m-MediaWrap img")
            .map { col -> col.attr("src") }[0].toString()

        recipe.ingredients = html.select(".o-Ingredients__a-Ingredient--CheckboxLabel")
            .map { col -> col.ownText() }
            .filter { !it.equals("Deselect All") }.toTypedArray()

        val reviewSummary = html.select(".review .review-summary")

        val recipeNumberString = reviewSummary.select("span")
            .map { col -> col.ownText() }[0]
        
        recipe.reviewNumber =
            recipeNumberString.substring(0, recipeNumberString.indexOf(" ")).toInt()


        recipe.rating = reviewSummary.select(".rating-stars")
            .map { col -> col.attr("title") }[0].toString()

        return recipe
    }
}