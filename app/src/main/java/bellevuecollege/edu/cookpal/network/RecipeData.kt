package bellevuecollege.edu.cookpal.network

data class RecipeData (
    var title: String = "",
    var steps: ArrayList<String> = arrayListOf(),
    var imgUrl: String = "",
    var sourceUrl: String = "",
    var ingredients: ArrayList<String> = arrayListOf(),
    var reviewNumber: Int = 0,
    var rating: String = "",
    var totalTime: String = ""
)