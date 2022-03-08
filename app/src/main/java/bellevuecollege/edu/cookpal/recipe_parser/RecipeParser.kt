package bellevuecollege.edu.cookpal.recipe_parser

import android.util.Log
import bellevuecollege.edu.cookpal.network.Recipe
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
//for dish image
const val ALL_RECIPES_DISH_IMAGE_CSS_QUERY = ".recipe-content-container div div div div noscript"

const val TAG = "Parse allrecipes recipe"

fun extractAllRecipesInformation(recipe: Recipe) {
    var doc: Document? = null
    try {
        doc = Jsoup.connect(recipe.sourceUrl).get()
    }
    catch (e: Exception) {
        Log.e(TAG, "Error: $e")
    }
    // Summary
    doc?.select(ALL_RECIPES_SUMMARY_CSS_QUERY)
        ?.map { it -> recipe.summary += it.text() + "\n"}
    Log.d(TAG, "${recipe.summary}")

    // Ingredients
    doc?.select(ALL_RECIPES_INGREDIENTS_CSS_QUERY)
        ?.map { it -> recipe.ingredients += it.text() + "\n" }
    Log.d(TAG, "${recipe.ingredients}")

    // Cooking Instructions
    doc?.select(ALL_RECIPES_INSTRUCTIONS_CSS_QUERY)
        ?.map { it -> recipe.cookingInstructions += it.text() + "\n"}
    Log.d(TAG, "${recipe.cookingInstructions}")

    // Dish image
//    recipe.imgSrcUrl = ""
//    doc?.select(ALL_RECIPES_DISH_IMAGE_CSS_QUERY)
//        ?.map { it -> recipe.imgSrcUrl += it.text() + "\n"}
//    Log.d(TAG, "Image source url: ${recipe.imgSrcUrl}")
}

