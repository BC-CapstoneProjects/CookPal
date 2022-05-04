import { IRecipe } from "@models/recipe-model";
import webscraping from "src/utils/webscraping";
import dataAccess from "./data-access";

/**
 * Get one recipe.
 * @param id the id of the recipe we are looking for
 * @returns a recipe if found otherwise null
 */
async function getOne(id: string): Promise<IRecipe | null> {
  return await dataAccess.findOne(id);
}

/**
 * Gets list of recipes by the title
 * @param title the title of the recipes we are looking for
 * @returns an array of recipes
 */
async function getByTitle(title: string): Promise<Array<IRecipe>> {
  let results: Array<IRecipe> = await dataAccess.findByTitle(title);

  if (results.length > 0) {
    return results;
  }
  // data scrape
  results = await webscraping.getResults(title);

  await dataAccess.uploadRecipes(results);

  return results;
}

/**
 * uploads a list of recipes
 * @param recipes the list of recipes
 * @returns
 */
async function uploadRecipes(recipes: Array<IRecipe>): Promise<any> {
  return await dataAccess.uploadRecipes(recipes);
}

// Export default
export default {
  getOne,
  getByTitle,
  uploadRecipes,
} as const;
