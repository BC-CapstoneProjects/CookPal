import { IRecipe } from "@models/recipe-model";
abstract class BaseScraper {
    retrieveRecipes(keyword: string, numPages: Number, numDrivers: Number, writeLocation: string) {
        const driverPool = new DriverPool()
        const allRecipes = this.returnRecipesFromKeyword(keyword, driverPool, numPages)
    }
    returnRecipesFromKeyword(keyword: string, driverPool: DriverPool, numPages: Number) {
        const pageUrls = []
        for (let page = 0; page < numPages; page++) {
            pageUrls.push(this.getUrlForPage(page, keyword))
        }
        const pageResults = driverPool.getOutputs(pageUrls)
        const recipeUrls: string[] = []
        const parser = new DOMParser()
        for (const page of pageResults){
            recipeUrls.push(...this.getRecipeUrlsFromPage(parser.parseFromString(page, 'text/html')))
        }
        const recipeResults = driverPool.getOutputs(recipeUrls)
        return recipeResults.map((recipe, index) => this.parseRecipeHtml(parser.parseFromString(recipe, 'text/html'), recipeUrls[index]) )

    }

    abstract getRecipeUrlsFromPage(html: Document) : Array<string>
    abstract getUrlForPage(page: Number, keyword: string) : string
    abstract parseRecipeHtml(html: Document, url: string) : IRecipe
}

export default BaseScraper