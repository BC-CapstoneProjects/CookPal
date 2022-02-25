package apilib

class Recipe {
    var title: String? = ""
    var steps: Array<String> = arrayOf()
    var imgUrl: String = ""
    var ingredients: Array<String> = arrayOf()
    var reviewNumber: Number = 0
    var rating: String = ""

    override fun toString(): String {
        var stepsString = ""
        for (step in steps)
            stepsString += "$step\n"
        var ingredientsString = ""
        for (ingredient in ingredients)
            ingredientsString += "$ingredient\n"

        return "\ntitle: $title\n" +
                "rating: $rating\n" +
                "reviewNumber: $reviewNumber\n" +
                "imgUrl: $imgUrl\n" +
                "steps:\n$stepsString\n" +
                "ingredients:\n$ingredientsString\n"

    }

}