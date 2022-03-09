package bellevuecollege.edu.cookpal.network

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize



// An example of recipe details gotten from RecipeAPI
// {"recipe":{
// "ingredients":["6 cups of milk","1/2 cup (1 stick) butter","1/2 cup yellow cornmeal","1/4 cup flour","1 teaspoon salt","1/2 cup molasses","3 eggs, beaten","1/3 cup of granulated sugar","1 teaspoon of cinnamon","1 teaspoon of nutmeg","1 cup golden raisins (optional)","Whipped cream or vanilla ice cream\n"],
// "image_url":"https://res.cloudinary.com/dk4ocuiwa/image/upload/v1575163942/RecipesApi/indianpuddinga300x200bd7240d9.jpg",
// "social_rank":99.95207451433001,
// "_id":"5ddf505525113e0c930aedfc",
// "publisher":"Simply Recipes",
// "source_url":"http://www.simplyrecipes.com/recipes/indian_pudding/",
// "recipe_id":"36498",
// "publisher_url":"http://simplyrecipes.com",
// "title":"Indian Pudding"}}
//data class RecipeDetailsResponse(
//    var ingredients: MutableList<String> = mutableListOf<String>(),
//    val image_url: String,
//    val social_rank: String,
//    val _id: String,
//    val publisher: String,
//    val source_url: String,
//    val recipe_id: String,
//    val publisher_url: String,
//    val title: String
//)

@Parcelize
data class Recipe(
    var isLoadedSuccessful: Boolean = false,
    var rId: String = "",
    var title: String = "",
    @Json(name = "imageUrl") var imgUrl: String = "",
    var sourceUrl: String = "",
    var isFavorite: Boolean = false,
    var summary: String = "",
    var ingredients: ArrayList<String> = arrayListOf(),
    var steps: ArrayList<String> = arrayListOf(),

    var reviewNumber: Int = 0,
    var rating: String = "",
    var totalTime: String = ""
) : Parcelable

/**
 * data class RecipeData (
var title: String = "",
var steps: ArrayList<String> = arrayListOf(),
var imgUrl: String = "",
var sourceUrl: String = "",
var ingredients: ArrayList<String> = arrayListOf(),
var reviewNumber: Int = 0,
var rating: String = "",
var totalTime: String = ""
)
 */
//data class RecipeDetailsResponseMetadata(
//    @SerializedName("recipe")
//    @Expose
//    val recipe: RecipeDetailsResponse
//)