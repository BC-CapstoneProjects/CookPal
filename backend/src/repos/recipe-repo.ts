import { IRecipe } from '@models/recipe-model';
import  dataAccess from './data-access'



/**
 * Get one recipe.
 * 
 * @param id 
 * @returns 
 */
async function getOne(id: string): Promise<IRecipe | null> {
    const data = await dataAccess.findOne(id);
  
    return data;
}


async function getByTitle(title: string): Promise<Array<any>> {
    const data = await dataAccess.findByTitle(title);
  
    return data;
}
 


// Export default
export default {
    getOne, 
    getByTitle
} as const;
