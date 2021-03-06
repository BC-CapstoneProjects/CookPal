package scrapers

import Recipe
import org.jsoup.nodes.Document

class TasteOfHome : BaseScraper() {
    /**
     * Return the url given a keyword and page number
     */
    override fun getUrlForPage(page: Number, keyWord: String): String {
        return "https://www.tasteofhome.com/recipes/dishes-beverages/page/$page/?s=$keyWord"
    }

    /**
     * Return recipe urls from a given search page
     */
    override fun getRecipeUrlsFromPage(html: Document): List<String> {
        return html.select(".recipe .entry-title a")
            .map { col -> col.attr("href") }.filter { url -> url.contains("recipes") }

    }

    /**
     * Given the html source code, return a recipe object
     */
    override fun parseRecipeHtml(html: Document, url: String): Recipe {
        val recipe = Recipe()

        recipe.title = getFirstOrEmptyText(html, "h1")
        if (recipe.title == "") {
            println(recipe.title)
            return recipe
        }

        recipe.sourceUrl = url

        // Sometimes videos are used instead of images. In that case no image is returned
        recipe.imgUrl = getFirstOrEmptyAttr(html, "div .featured-container img", "src")

        recipe.steps = arrayListOf(*html.select(".recipe-directions__item span")
            .map { col -> col.ownText() }.toTypedArray())

        recipe.ingredients = arrayListOf(*html.select(".recipe-ingredients li")
            .map { ingredient -> ingredient.ownText() }.toTypedArray())

        recipe.totalTime = getFirstOrEmptyText(html, ".total-time p")

        var rating = 0.0
        val recipeNumberInput = html.select(".rating a")
        var recipeNumberString = ""
        if (recipeNumberInput.isNotEmpty()) {
            recipeNumberString = recipeNumberInput[recipeNumberInput.size - 1].ownText()
        }

        if (recipeNumberString == "Add a Review" || recipeNumberInput.isEmpty()) {
            recipe.rating = "Not reviewed"
            recipe.reviewNumber = 0
        } else {
            html.select(".rating a i").forEach {
                val starStatus = it.classNames()
                if (starStatus.contains("dashicons-star-half")) {
                    rating += 0.5
                } else if (starStatus.contains("dashicons-star-filled")) {
                    rating++
                }
            }
            recipe.rating = "$rating out of 5 stars"
            val numLength = recipeNumberString.indexOf(" ")
            if (numLength in 1..6) {
                recipe.reviewNumber =
                    recipeNumberString.substring(0, numLength).toInt()
            }
        }

        return recipe
    }
}