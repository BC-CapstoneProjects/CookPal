import StatusCodes from 'http-status-codes';
import { Request, Response, Router } from 'express';
import recipeService from '@services/recipe-service';

// Constants
const router = Router();
const { OK } = StatusCodes;

// Paths
export const p = {
    get: '/:id', 
    getByTitle: '/title/:title', 
} as const;
 
/**
 * Get one recipe.
 */
router.get(p.get, async (req: Request, res: Response) => {
     
    const data = await recipeService.getOne(req.params.id);

    console.log(JSON.stringify(data));
   
    return res.status(OK).json(data);
});


/**
 * Get recipes by title
 */
 router.get(p.getByTitle, async (req: Request, res: Response) => {
     
    const data = await recipeService.getByTitle(req.params.title);

    console.log(JSON.stringify(data));
   
    return res.status(OK).json(data);
});
 

// Export default
export default router;
