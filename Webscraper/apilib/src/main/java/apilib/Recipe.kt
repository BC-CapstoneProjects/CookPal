package apilib

class Recipe {
    var label: String = ""
    var title: String = ""
    var steps: Array<String> = arrayOf()
    var imgUrl: String = ""
    var sourceUrl: String = ""
    var ingredients: Array<String> = arrayOf()
    var reviewNumber: Number = 0
    var rating: String = ""
    var totalTime: String = ""
    var cuisineType: ArrayList<String> = arrayListOf()
    var mealType: ArrayList<String>  = arrayListOf()
    var dishType: ArrayList<String>  = arrayListOf()
}