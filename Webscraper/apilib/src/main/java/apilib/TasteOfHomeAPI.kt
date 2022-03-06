package apilib

import org.jsoup.nodes.Document

class TasteOfHomeAPI : GeneralAPI() {


    override fun retrieveRecipes(keyword: String, numPages: Int, numDrivers: Int) {
        retrieveRecipes(
            keyword,
            numPages,
            numDrivers,
            "./apilib/src/main/java/apilib/json/$keyword-TasteOfHomeAPI.json"
        )
    }


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
            .map { col -> col.attr("href") }

    }

    /**
     * Given the html source code, return a recipe object
     */
    override fun parseRecipeHtml(html: Document, url: String): Recipe {
        val recipe = Recipe()
        // Sometimes videos are used instead of images
        val imageUrl = html.select("div .featured-container img").map { col -> col.attr("src") }
        if (imageUrl.isNotEmpty()) {
            recipe.imgUrl = imageUrl[0].toString()
        }
        recipe.title = html.select("h1").map { col -> col.ownText() }[0].toString()

        recipe.steps = html.select(".recipe-directions__item span")
            .map { col -> col.ownText() }.toTypedArray()


        recipe.ingredients = html.select(".recipe-ingredients li")
            .map { ingredient -> ingredient.ownText() }.toTypedArray()
        recipe.totalTime = html.select(".total-time p")[0].ownText()
        recipe.sourceUrl = url
        var rating = 0.0
        val recipeNumberInput = html.select(".rating a")
        var recipeNumberString = ""
        if (!recipeNumberInput.isEmpty()) {
            recipeNumberString = recipeNumberInput[recipeNumberInput.size - 1].ownText()
        }

        if (recipeNumberString.equals("Add a Review") || recipeNumberInput.isEmpty()) {
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