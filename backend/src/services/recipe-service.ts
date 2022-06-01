import recipeRepo from "@repos/recipe-repo";
import { IRecipe } from "@models/recipe-model";

/**
 * Get one recipe.
 * @param id the id of the recipe we are looking for
 * @returns a recipe if found otherwise null
 */
async function getOne(id: string): Promise<IRecipe | null> {
  return await recipeRepo.getOne(id);
}

/**
 * Gets list of recipes by the title
 * @param title the title of the recipes we are looking for
 * @returns an array of recipes
 */
async function getByTitle(title: string): Promise<Array<IRecipe>> {
  return await recipeRepo.getByTitle(title);
}

/**
 * upload one recipe
 * @param title the recipe to upload
 * @returns an array of recipes
 */
async function uploadRecipe(recipe: IRecipe): Promise<IRecipe> {
  return await recipeRepo.uploadRecipe(recipe);
}

// Export default
export default {
  getOne,
  getByTitle,
  uploadRecipe,
} as const;
