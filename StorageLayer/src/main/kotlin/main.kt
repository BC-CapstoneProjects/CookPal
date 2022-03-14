import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import mu.KLogger
import mu.KotlinLogging
import java.io.File
import kotlin.system.exitProcess

fun search(fetcher: RecipeAdapter, args: Array<String>) {
    if (args.size < 3) {
        throw Exception("Missing cmd line args.")
    }
    val searchType = SearchType.valueOf(args[1].toUpperCase())
    val searchValues= mutableListOf<String>()
    for ( i in 2 until args.size)
    {
        searchValues.add(args[i])
    }

    val ret = fetcher.getRecipesFromQuery(listOf(SearchParam(searchType, searchValues)))
    println(ret.string())
}

fun upload(logger: KLogger, fetcher: RecipeAdapter, args: Array<String>) {
    if (args.size != 2) {
        throw Exception("Too many cmd line args.")
    }
    val typeRef = object : TypeReference<List<Recipe>>() {}
    var fileName = args[1]
    logger.info { "Loading $fileName"}
    var file = File(fileName).readText(Charsets.UTF_8)
    val recipes = jacksonObjectMapper().readValue(file, typeRef)
    logger.debug { "Uploading $recipes"}
    fetcher.upload(recipes)
}

fun main(args: Array<String>) {
    val path = System.getProperty("user.dir")

    val usage = "Usage:\n" +
            "Search {title|ingredient|all} search terms\n" +
            "Upload path_to_json_recipes_file\n"+
            "Example1: search ingredient rice\n" +
            "Example2: upload ../app/src/main/assets/recipes/bacon-foodnetwork.json"
    val logger = KotlinLogging.logger {}

    logger.info {"Working Directory = $path" }

    try {
        val fetcher = RecipeAdapter("src/main/resources/config.yaml")
        if (args.size < 2) {
            throw Exception("Missing cmd line args.")
        }
        when (args[0].toLowerCase())
        {
            "search"-> search(fetcher, args)
            "upload" -> upload(logger, fetcher, args)
            else -> throw Exception("Invalid action ${args[0]}")
        }
    } catch (e: Exception)
    {
        println(e)
        println(usage)
        exitProcess(1)
    }
}
