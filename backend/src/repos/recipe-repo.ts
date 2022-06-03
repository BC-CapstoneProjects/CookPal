import { IRecipeFilter } from "@models/recipe-filter-model";
import { IRecipe } from "@models/recipe-model";
import utils from "../utils/utils";
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
  return await dataAccess.findByTitle(title);
}

/**
 * Gets list of recipes by the title and using a filter
 * @param title the title of the recipes we are looking for
 * @param filter the filter to use
 * @returns an array of recipes
 */
async function getByTitleFilter(
  title: string,
  filter: IRecipeFilter
): Promise<Array<IRecipe>> {
  let results: Array<IRecipe> = await dataAccess.findByTitle(title);
  let finalResults: Array<IRecipe> = [];

  for (let i: number = 0; i < results.length; i++) {
    if (utils.includeItem(results[i], filter)) {
      finalResults.push(results[i]);
    }
  }

  return finalResults;
}

/**
 * Gets list of recipes by the rating
 * @param rating the rating of the recipes we are looking for
 * @returns an array of recipes
 */
async function getByRating(rating: string): Promise<Array<IRecipe>> {
  return await dataAccess.findByRating(rating);
}

/**
 * upload one recipe
 * @param title the recipe to upload
 * @returns an array of recipes
 */
async function uploadRecipe(recipe: IRecipe): Promise<IRecipe> {
  return await dataAccess.uploadRecipe(recipe);
}

// Export default
export default {
  getOne,
  getByTitle,
  getByTitleFilter,
  getByRating,
  uploadRecipe,
} as const;
