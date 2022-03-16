package bellevuecollege.edu.cookpal.network

import Recipe as RecipeStorage

class RecipeClassConverter {
    fun convert(recipe: RecipeStorage): Recipe {
        return Recipe(
            title = recipe.title,
            sourceUrl = recipe.sourceUrl,
            ingredients = recipe.ingredients,
            steps = recipe.steps,
            imgUrl = recipe.imgUrl,
            rating = recipe.rating,
            reviewNumber = recipe.reviewNumber as Int,
            totalTime = recipe.totalTime
        )
    }
}