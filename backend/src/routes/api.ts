import { Router } from 'express';
import recipeRouter from './recipe-router';

// Export the base-router
const baseRouter = Router();

// Setup routers
baseRouter.use('/recipe', recipeRouter);

// Export default.
export default baseRouter;
