import StatusCodes from "http-status-codes";
import { Request, Response, Router } from "express";
import recipeService from "@services/recipe-service";
import utils from "../utils/utils";
import { IRecipe } from "@models/recipe-model";

import ws from "../websocket";
import FoodNetwork from "../webscraper/scrapers/FoodNetwork";
var url = require("url");

// Constants
const router = Router();
const { OK } = StatusCodes;

// Paths
export const p = {
  getByRating: "/rating/:rating",
  get: "/:id",
  getByTitle: "/title/:title",
} as const;

router.get(p.getByRating, async (req: Request, res: Response) => {
  try {
    let data: Array<IRecipe> = await recipeService.getByRating(
      req.params.rating
    );

    let finalData: any = { documents: data };

    console.log(JSON.stringify(finalData));
    utils.log(JSON.stringify(finalData));

    finalData = utils.hideError(finalData);
    console.log("title");

    return res.status(OK).json(finalData);
  } catch (e: any) {
    utils.logerror(JSON.stringify(e));
    return res.status(OK).json({ error: "an error has occurred" });
  }
});

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
    let data: Array<IRecipe> = await recipeService.getByTitle(req.params.title);

    let finalData: any = { documents: data };

    console.log(JSON.stringify(finalData));
    utils.log(JSON.stringify(finalData));

    finalData = utils.hideError(finalData);

    var parts: any = url.parse(req.url, true);
    var query: any = parts.query;
    var id: string = query.cid; // id used to identify client for web socket

    if (id != "" && data.length < 4) {
      console.log("web scraping");

      var io: any = ws.getWS(); // pointer to io object used to send data to client for the web socket

      var fn: FoodNetwork = new FoodNetwork();
      fn.setWb(io, id);
      fn.retrieveRecipes(req.params.title, 1);
    }

    return res.status(OK).json(finalData);
  } catch (e: any) {
    utils.logerror(JSON.stringify(e));
    return res.status(OK).json({ error: "an error has occurred" });
  }
});

// Export default
export default router;
