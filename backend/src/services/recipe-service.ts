import recipeRepo from '@repos/recipe-repo';
import { IRecipe } from '@models/recipe-model';

/**
 * Get one recipe.
 * 
 * @returns 
 */
async function getOne(id:string): Promise<IRecipe | null> {
    const dt = await recipeRepo.getOne(id);
 
    return dt;
}

/**
 * Get recipes by title.
 * 
 * @returns 
 */
 async function getByTitle(title:string): Promise<Array<any >> {
    const dt = await recipeRepo.getByTitle(title);
 
    return dt;
}

// Export default
export default {
    getOne,
    getByTitle
} as const;
