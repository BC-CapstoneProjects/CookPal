package apilib

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class FoodNetworkAPI : GeneralAPI() {

    override fun retrieveRecipes(
        keyword: String,
        numPages: Int,
        numDrivers: Int
    ) {
        retrieveRecipes(
            keyword,
            numPages,
            numDrivers,
            "./apilib/src/main/java/apilib/json/$keyword-FoodNetwork.json"
        )
    }

    fun main() {
        retrieveRecipes("bacon", 3, 3)
    }

    /**
     * Return the url given a keyword and page number
     */
    override fun getUrlForPage(page: Number, keyWord: String): String {
        return "https://www.foodnetwork.com/search/$keyWord/p/$page/CUSTOM_FACET:RECIPE_FACET"
    }

    /**
     * Return recipe urls from a given search page
     */
    override fun getRecipeUrlsFromPage(html: Document): List<String> {
        return html.select(".o-RecipeResult .l-List .m-MediaBlock__a-Headline a")
            .map { col -> col.attr("href") }.map { recipe -> "https:$recipe" }

    }

    /**
     * Given the html source code, return a recipe object
     */
    override fun parseRecipeHtml(html: Document): Recipe {
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


        val numLength = recipeNumberString.indexOf(" ")
        if (numLength in 1..6) {
            recipe.reviewNumber =
                recipeNumberString.substring(0, numLength).toInt()
        }
        recipe.rating = reviewSummary.select(".rating-stars")
            .map { col -> col.attr("title") }[0].toString()

        return recipe
    }


}