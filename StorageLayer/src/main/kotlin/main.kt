fun main() {
    val path = System.getProperty("user.dir")

    println("Working Directory = $path")
    val fetcher = FetchRecipes("src/main/resources/config.yaml")
    fetcher.queryDB("{\n\"collection\":\"Recipes\",\n\"database\":\"CookPal\",\n\"dataSource\":\"CookPal\"}","findOne")
    val x = Recipe()
    x.title = "test"
    x.ingredients = arrayOf("food", "morefood", "evenmorefood")
    fetcher.upload(listOf(x))
}
