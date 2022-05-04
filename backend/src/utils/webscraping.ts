import { IRecipe } from "@models/recipe-model";

class webscraping {
  public static async getResults(
    title: string,
    maxResults: number = -1
  ): Promise<Array<IRecipe>> {
    return new Promise(function (resolve, reject) {
      resolve([]);
    });
  }
}

export default webscraping;
