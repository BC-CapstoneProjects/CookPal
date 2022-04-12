package bellevuecollege.edu.cookpal.network

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

// TODO rename file
@Parcelize
data class Recipe (
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
    var totalTime: String = "",
    val _id: String = "",
    var label: String = "",
    var cuisineType: ArrayList<String> = arrayListOf(),
    var mealType: ArrayList<String> = arrayListOf(),
    var dishType: ArrayList<String> = arrayListOf(),
) : Parcelable