import mu.KotlinLogging

fun main() {
    val path = System.getProperty("user.dir")

    var logger = KotlinLogging.logger {}

    logger.info {"Working Directory = $path" }

    val fetcher = RecipeAdapter("src/main/resources/config.yaml")
    fetcher.queryDB("{\n\"collection\":\"Recipes\",\n\"database\":\"CookPal\",\n\"dataSource\":\"CookPal\"}","findOne")
    val x = Recipe()
    x.title = "test"
    x.ingredients = arrayOf("food", "morefood", "evenmorefood")
    fetcher.upload(listOf(x))
}
