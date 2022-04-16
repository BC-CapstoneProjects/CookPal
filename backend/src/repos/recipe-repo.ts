import { IRecipe } from '@models/recipe-model';
import  dataAccess from './data-access'

/**
 * Get one recipe.
 * 
 * @param id 
 * @returns 
 */
async function getOne(id: string): Promise<IRecipe | null> {
    return await dataAccess.findOne(id);
}

/**
 * Get recipes by title.
 * 
 * @param title 
 * @returns 
 */
async function getByTitle(title: string): Promise<Array<IRecipe>> {
    return await dataAccess.findByTitle(title);
}
 
// Export default
export default {
    getOne, 
    getByTitle
} as const;
