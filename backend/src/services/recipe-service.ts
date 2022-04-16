import recipeRepo from '@repos/recipe-repo';
import { IRecipe } from '@models/recipe-model';

/**
 * Get one recipe.
 * 
 * @returns 
 */
async function getOne(id:string): Promise<IRecipe | null> {
    return await recipeRepo.getOne(id); 
}

/**
 * Get recipes by title.
 * 
 * @returns 
 */
 async function getByTitle(title:string): Promise<Array<IRecipe >> {
    return await recipeRepo.getByTitle(title);
 }

// Export default
export default {
    getOne,
    getByTitle
} as const;
