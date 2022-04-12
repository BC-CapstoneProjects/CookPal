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

/**
 * Get recipes by title.
 * 
 * @returns 
 */
 async function getByTitle(title:string): Promise<Array<any >> {
    var dt = await recipeRepo.getByTitle(title);
 
    return dt;
}


 

// Export default
export default {
    getOne,
    getByTitle
} as const;
