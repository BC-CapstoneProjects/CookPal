package bellevuecollege.edu.cookpal.recipes

class RecipeFilter {
    var minMins:Int? = 10
    var maxMins:Int? = 10
    var rating:Float? = 0.0f
    var ingredients:String? = ""

    fun ToQueryString():String {
        var queryString = ""

        queryString = "minMins=" + minMins.toString()
        queryString += "&maxMins=" + maxMins.toString()
        queryString += "&rating=" + rating.toString()
        queryString += "&ingredients=" + ingredients

        return queryString
    }
}