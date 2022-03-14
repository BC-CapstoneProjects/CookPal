data class Recipe (
    var label: String = "",
    var title: String = "",
    var steps: ArrayList<String> = arrayListOf(),
    var imgUrl: String = "",
    var sourceUrl: String = "",
    var ingredients: ArrayList<String> = arrayListOf(),
    var reviewNumber: Number = 0,
    var rating: String = "",
    var totalTime: String = "",
    var cuisineType: ArrayList<String> = arrayListOf(),
    var mealType: ArrayList<String> = arrayListOf(),
    var dishType: ArrayList<String> = arrayListOf(),
)
