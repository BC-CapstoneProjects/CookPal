import StatusCodes from "http-status-codes";
import { Request, Response, Router } from "express";
import recipeService from "@services/recipe-service";
import utils from "../utils/utils";
import { IRecipe } from "@models/recipe-model";
import { IRecipeFilter } from "@models/recipe-filter-model";
var url = require("url");

// Constants
const router = Router();
const { OK } = StatusCodes;

// Paths
export const p = {
  get: "/:id",
  getByTitle: "/title/:title",
} as const;

/**
 * Get one recipe.
 */
router.get(p.get, async (req: Request, res: Response) => {
  try {
    let data: IRecipe | null = await recipeService.getOne(req.params.id);

    if (data) {
      console.log(JSON.stringify(data));
      utils.log(JSON.stringify(data));

      data = utils.hideError(data);

      return res.status(OK).json(data);
    } else {
      console.log("null");
      utils.log("null");

      return res.status(OK).json({});
    }
  } catch (e: any) {
    utils.logerror(JSON.stringify(e));
    return res.status(OK).json({ error: "an error has occurred" });
  }
});

/**
 * Get recipes by title
 */
router.get(p.getByTitle, async (req: Request, res: Response) => {
  try {
    var parts = url.parse(req.url, true);
    var query = parts.query;
    let data: Array<IRecipe>;

    if (query != undefined && query.usefilter != undefined) {
      console.log("filter");

      let filter: IRecipeFilter = {
        ingredients: query.ingredients,
        rating: parseFloat(query.rating),
        minMins: parseInt(query.minMins),
        maxMins: parseInt(query.maxMins),
      };

      data = await recipeService.getByTitleFilter(req.params.title, filter);
    } else {
      console.log("no filter");

      data = await recipeService.getByTitle(req.params.title);
    }

    let finalData: any = { documents: data };

    console.log(data.length);
    //console.log(JSON.stringify(finalData));
    utils.log(JSON.stringify(finalData));

    finalData = utils.hideError(finalData);
    console.log("title");

    return res.status(OK).json(finalData);
  } catch (e: any) {
    utils.logerror(JSON.stringify(e));
    return res.status(OK).json({ error: "an error has occurred" });
  }
});

// Export default
export default router;
