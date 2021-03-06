package scrapers

import Recipe
import org.jsoup.nodes.Document

class FoodNetwork : BaseScraper() {
    /**
     * Return the url given a keyword and page number
     */
    override fun getUrlForPage(page: Number, keyWord: String): String {
        return "https://www.foodnetwork.com/search/$keyWord-/p/$page/CUSTOM_FACET:RECIPE_FACET"
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
    override fun parseRecipeHtml(html: Document, url: String): Recipe {
        val recipe = Recipe()
        recipe.title = getFirstOrEmptyText(
            html,
            ".m-RecipeSummary .assetTitle .o-AssetTitle__a-HeadlineText:first-of-type"
        )
        if (recipe.title == "")
            return recipe

        recipe.steps = arrayListOf(*html.select(".o-Method__m-Body li")
            .map { col -> col.ownText() }.toTypedArray())

        recipe.imgUrl = getFirstOrEmptyAttr(
            html,
            ".recipe-lead .m-MediaBlock__m-MediaWrap img",
            "src"
        )
        if (recipe.imgUrl != "") {
            recipe.imgUrl = "https:" + recipe.imgUrl
        }

        recipe.ingredients = arrayListOf(*html.select(".o-Ingredients__a-Ingredient--CheckboxLabel")
            .map { col -> col.ownText() }
            .filter { !it.equals("Deselect All") }
            .toTypedArray())
        recipe.totalTime =
            getFirstOrEmptyText(html, ".o-RecipeInfo__m-Time .o-RecipeInfo__a-Description")
        recipe.sourceUrl = url
        val recipeNumberString = getFirstOrEmptyText(html, ".review .review-summary span")

        val numLength = recipeNumberString.indexOf(" ")
        if (numLength in 1..6) {
            recipe.reviewNumber =
                recipeNumberString.substring(0, numLength).toInt()
        }
        recipe.rating = getFirstOrEmptyAttr(html, ".review .review-summary .rating-stars", "title")

        return recipe
    }


}