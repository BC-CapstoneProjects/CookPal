import { Request, Response, Router } from 'express';
import recipeRouter from './recipe-router';
import StatusCodes from 'http-status-codes';
import dataAccess from '@repos/data-access';

// Export the base-router
const baseRouter = Router();
const { OK } = StatusCodes;
// Setup routers
baseRouter.use('/recipe', recipeRouter);

baseRouter.get('/test', async (req: Request, res: Response) => {
     
    return res.status(OK).json({'data':'some data'});
});
 
baseRouter.get('/update', async (req: Request, res: Response) => {
       
    const dt:any = await dataAccess.getPendingCodeUpdates();

    if (dt.length > 0)
    {
        return res.status(OK).json({'info':'code update already in progress'});
    }

    const udt:any = await dataAccess.putinPendingCodeUpdate();
 
    // use to update api for aws
    return res.status(OK).json({'info':'code will be updated shortly'});
});

// Export default.
export default baseRouter;
