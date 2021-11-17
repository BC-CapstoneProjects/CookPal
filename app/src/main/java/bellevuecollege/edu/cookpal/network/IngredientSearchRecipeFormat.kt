package bellevuecollege.edu.cookpal.network

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class IngredientSearchRecipesResponse(
    @SerializedName("count")
    @Expose
    val count: Int,

    @SerializedName("recipes")
    @Expose
    val recipes: List<RecipeResponse>
)

data class RecipeResponse (
    val imageUrl: String,
    val socialUrl: String,
    val publisher: String,
    val sourceUrl: String,
    val id: String,
    val publisherId: String,
    val title: String
)

@Parcelize
data class Recipe (
    var isLoadedSuccessful: Boolean = false,
    var rId: String,
    val title: String,
    @Json(name = "imageUrl") val imgSrcUrl: String,
    val sourceUrl: String,
    var isFavorite: Boolean = false,
    var ingredients: MutableList<String> = mutableListOf<String>(),
    var cookingInstructions: MutableList<String> = mutableListOf<String>(),
    var response: String
): Parcelable
