package bellevuecollege.edu.cookpal.recipes

class RecipeFilter {
    var minMins = 10
    var maxMins = 10
    var rating:Float = 0.0f
    var ingredients = ""

    fun ToQueryString():String {
        var queryString = ""

        queryString = "minMins=" + minMins.toString()
        queryString += "&maxMins=" + maxMins.toString()
        queryString += "&rating=" + rating.toString()
        queryString += "&ingredients=" + ingredients

        return queryString
    }
}