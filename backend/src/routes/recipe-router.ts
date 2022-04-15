import StatusCodes from 'http-status-codes';
import { Request, Response, Router } from 'express';
import recipeService from '@services/recipe-service';
import utils from '../utils/utils';

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
    try
    {
        let data = await recipeService.getOne(req.params.id);

        console.log(JSON.stringify(data));
        utils.log(JSON.stringify(data));

        data = utils.hideError(data);
    
        return res.status(OK).json(data);
    }
    catch (e:any)
    {
        utils.logerror(JSON.stringify(e));
        return res.status(OK).json({'error':'an error has occurred'});
    }
});


/**
 * Get recipes by title
 */
 router.get(p.getByTitle, async (req: Request, res: Response) => {
     
    try
    {
        let data = await recipeService.getByTitle(req.params.title);

        console.log(JSON.stringify(data));
        utils.log(JSON.stringify(data));
    
        data = utils.hideError(data);
        console.log('title');
       
        return res.status(OK).json(data);
    }
    catch (e:any)
    {
        utils.logerror(JSON.stringify(e));
        return res.status(OK).json({'error':'an error has occurred'});
    }
    
    
});
 

// Export default
export default router;
