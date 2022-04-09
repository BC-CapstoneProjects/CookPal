import StatusCodes from 'http-status-codes';
import { Request, Response, Router } from 'express';
import recipeService from '@services/recipe-service';
import { IRecipe } from '@models/recipe-model';
 

// Constants
const router = Router();
const { CREATED, OK } = StatusCodes;

// Paths
export const p = {
    get: '/:id', 
} as const;
 
/**
 * Get one recipe.
 */
router.get(p.get, async (req: Request, res: Response) => {
     
    var data = await recipeService.getOne(req.params.id);

    console.log(JSON.stringify(data));
   
    return res.status(OK).json(data);
});

 

// Export default
export default router;
