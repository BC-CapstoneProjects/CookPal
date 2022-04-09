import recipeRepo from '@repos/recipe-repo';
import { IRecipe } from '@models/recipe-model';
import { UserNotFoundError } from '@shared/errors';



/**
 * Get one recipe.
 * 
 * @returns 
 */
async function getOne(id:string): Promise<IRecipe | null> {
    var dt = await recipeRepo.getOne(id);
 
    return dt;
}

 

// Export default
export default {
    getOne 
} as const;
