import { IRecipe } from '@models/recipe-model';
import  dataAccess from './data-access'



/**
 * Get one recipe.
 * 
 * @param email 
 * @returns 
 */
async function getOne(id: string): Promise<IRecipe | null> {
    const data = await dataAccess.findOne(id);
  
    return data;
}

 


// Export default
export default {
    getOne, 
} as const;
