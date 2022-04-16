import { IRecipe } from '@models/recipe-model';
import  dataAccess from './data-access'

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
 
// Export default
export default {
    getOne, 
    getByTitle
} as const;
