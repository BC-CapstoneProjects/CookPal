package bellevuecollege.edu.cookpal.recipe_parser

import android.util.Log
import bellevuecollege.edu.cookpal.network.Recipe
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

// for now filter allrecipes.com recipes from RecipeAPI returned json
// allrecipes.com CSS queries
//for Summary box info of recipe
const val ALL_RECIPES_SUMMARY_CSS_QUERY = ".recipe-meta-container"

//for Ingredients box info of recipe
const val ALL_RECIPES_INGREDIENTS_CSS_QUERY =
    ".ingredients-section:first-of-type li label span span"

//for Instruction box info of recipe
const val ALL_RECIPES_INSTRUCTIONS_CSS_QUERY =
    ".instructions-section:first-of-type li label span span, .instructions-section li div div p"

fun extractAllRecipesInformation(recipe: Recipe) {
    var doc: Document? = null
    try {
        doc = Jsoup.connect(recipe.sourceUrl).get()
    } catch (e: Exception) {
        Log.e("Parse recipe", "Error: $e")
    }
    // Summary
    doc?.select(ALL_RECIPES_SUMMARY_CSS_QUERY)
        ?.map { it -> recipe.summary += it.text() + "\n" }
    Log.d("Parse recipe", "${recipe.summary}")

    // Ingredients
    doc?.select(ALL_RECIPES_INGREDIENTS_CSS_QUERY)
        ?.map { it -> recipe.ingredients += it.text() + "\n" }
    Log.d("Parse recipe", "${recipe.ingredients}")

    // Cooking Instructions
    doc?.select(ALL_RECIPES_INSTRUCTIONS_CSS_QUERY)
        ?.map { it -> recipe.cookingInstructions += it.text() + "\n" }
    Log.d("Parse recipe", "${recipe.cookingInstructions}")

}


