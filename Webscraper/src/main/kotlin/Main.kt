import WebScraper.scrapers.BaseScraper
import WebScraper.scrapers.FoodNetwork
import WebScraper.scrapers.TasteOfHome
import mu.KotlinLogging
import java.io.File
import kotlin.system.exitProcess


fun main(args: Array<String>) {


    val logger = KotlinLogging.logger {}
    if (args.size !== 5) {
        println("usage: keyword numPages numDrivers FoodNetwork|TasteOfHome path_to_json_dir")
        exitProcess(1)
    }
    val keyword: String = args[0]
    val numPages = args[1].toInt()
    val numDrivers = args[2].toInt()
    val scraperType: String = args[3].toLowerCase()
    val path: String = args[4]
    var scraper: BaseScraper? = null
    logger.debug { "Running with keyword = $keyword, scraperType = $scraperType" }
    when (scraperType) {
        "foodnetwork" -> scraper = FoodNetwork()
        "tasteofhome" -> scraper = TasteOfHome()
        else -> exitProcess(2)
    }
    val fullPath: String = File(path, "$keyword-$scraperType.json").getPath()
    scraper!!.retrieveRecipes(keyword, numPages, numDrivers, fullPath)
}