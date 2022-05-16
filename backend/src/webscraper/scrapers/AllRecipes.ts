import { IRecipe } from "@models/recipe-model";
import BaseScraper from "./BaseScraper";

class AllRecipes extends BaseScraper {
    getRecipeUrlsFromPage(doc: string): string[] {
        throw new Error("Method not implemented.");
    }
    getUrlForPage(page: Number, keyword: string): string {
        throw new Error("Method not implemented.");
    }
    parseRecipeHtml(doc: string, url: string): IRecipe {
        throw new Error("Method not implemented.");
    }
    
}