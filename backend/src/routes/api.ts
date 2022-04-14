import { Request, Response, Router } from 'express';
import recipeRouter from './recipe-router';
import StatusCodes from 'http-status-codes';
 
// Export the base-router
const baseRouter = Router();
const { OK } = StatusCodes;
// Setup routers
baseRouter.use('/recipe', recipeRouter);

baseRouter.get('/test', async (req: Request, res: Response) => {
     
    return res.status(OK).json({'data':'some data'});
});

baseRouter.get('/update', async (req: Request, res: Response) => {
     
    // use to update api for aws
    return res.status(OK).json({'data':'some data'});
});

// Export default.
export default baseRouter;
