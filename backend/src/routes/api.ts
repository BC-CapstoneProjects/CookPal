import { Request, Response, Router } from "express";
import recipeRouter from "./recipe-router";
import StatusCodes from "http-status-codes";
import dataAccess from "@repos/data-access";
import utils from "../utils/utils";

// Export the base-router
const baseRouter = Router();
const { OK } = StatusCodes;
// Setup routers
baseRouter.use("/recipe", recipeRouter);

baseRouter.get("/test", async (req: Request, res: Response) => {
  return res.status(OK).json({ data: "some data" });
});

baseRouter.get("/version", async (req: Request, res: Response) => {
  return res.status(OK).json({ version: "04-16-2022c" });
});

// url to update server code
baseRouter.get("/update", async (req: Request, res: Response) => {
  try {
    const dt: any = await dataAccess.getPendingCodeUpdates();

    if (dt.length > 0) {
      return res.status(OK).json({ info: "code update already in progress" });
    }

    const udt: any = await dataAccess.putInPendingCodeUpdate();

    // use to update api for aws
    return res.status(OK).json({ info: "code will be updated shortly" });
  } catch (e: any) {
    utils.logerror(JSON.stringify(e));
    return res.status(OK).json({ error: "an error has occurred" });
  }
});

// Export default.
export default baseRouter;
