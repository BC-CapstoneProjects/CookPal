import { IRecipe } from "@models/recipe-model";
import DriverPool from "../driverPool";
import BaseScraper from "./BaseScraper";

class AllRecipes extends BaseScraper {
  getRecipe(drivers: DriverPool, url: string): Promise<IRecipe> {
    throw new Error("Method not implemented.");
  }
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
