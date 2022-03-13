package WebScraper

import java.lang.Exception
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

// for now filter allrecipes.com recipes from RecipeAPI returned json
// allrecipes.com CSS queries
//for Summary box info of recipe
const val ALL_RECIPES_SUMMARY_CSS_QUERY = ".recipe-meta-container"
//for Ingredients box info of recipe
const val ALL_RECIPES_INGREDIENTS_CSS_QUERY = ".ingredients-section:first-of-type li label span span"
//for Instruction box info of recipe
const val ALL_RECIPES_INSTRUCTIONS_CSS_QUERY = ".instructions-section:first-of-type li label span span, .instructions-section li div div p"

fun extractAllRecipesInformation() {
    val recipe = Recipe()
    var doc: Document? = null
    try {
        doc = Jsoup.connect(recipe.sourceUrl).get()
    }
    catch (e: Exception) {
        //Log.e("Parse recipe", "Error: $e")
    }
    // Summary
//    doc?.select(ALL_RECIPES_SUMMARY_CSS_QUERY)
//        ?.map { it -> recipe.summary += it.text() + "\n"}
    //Log.d("Parse recipe", "${recipe.summary}")

    // Ingredients
    recipe.ingredients = doc?.select(ALL_RECIPES_INGREDIENTS_CSS_QUERY)
        ?.map { it -> it.text() }?.toTypedArray()!!
    //Log.d("Parse recipe", "${recipe.ingredients}")

    // Cooking Instructions
    doc?.select(ALL_RECIPES_INSTRUCTIONS_CSS_QUERY)
        ?.map { it -> recipe.steps += it.text() + "\n"}
    //Log.d("Parse recipe", "${recipe.cookingInstructions}")

}


