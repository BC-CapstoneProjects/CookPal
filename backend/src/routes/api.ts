import { Router } from 'express';
import userRouter from './user-router';
import recipeRouter from './recipe-router';

// Export the base-router
const baseRouter = Router();

// Setup routers
baseRouter.use('/users', userRouter);
baseRouter.use('/recipe', recipeRouter);

// Export default.
export default baseRouter;
