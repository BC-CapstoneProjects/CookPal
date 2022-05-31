import { IRecipe } from "@models/recipe-model";
import driverPool from "../driverPool";
import * as cheerio from "cheerio";
import dataAccess from "@repos/data-access";
import recipeService from "@services/recipe-service";

abstract class BaseScraper {
  protected io: any;
  protected id: string = "";

  public setWb(pio: any, pid: string): void {
    this.io = pio;
    this.id = pid;
  }

  public retrieveRecipes(keyword: string, numPages: Number) {
    const drivers = new driverPool();
    const allRecipes = this.returnRecipesFromKeyword(
      keyword,
      drivers,
      numPages
    );
    return allRecipes.then((result) => result);
    //TODO: write recipes to mongoDB
  }
  /**
   * Given a keyword, result an array of recipes
   * @param keyword
   * @param drivers
   * @param numPages
   * @returns
   */
  async returnRecipesFromKeywordOut(
    keyword: string,
    drivers: driverPool,
    numPages: Number
  ): Promise<IRecipe[]> {
    const pageUrls = [];
    for (let page = 0; page < numPages; page++) {
      pageUrls.push(this.getUrlForPage(page, keyword));
    }
    const pageResults = await drivers.getOutputs(pageUrls);
    var recipeUrls: string[] = [];
    for (const page of pageResults) {
      recipeUrls.push(...this.getRecipeUrlsFromPage(page));
    }

    console.log(recipeUrls);
    const recipeResults = await Promise.all(
      recipeUrls.map((url) => {
        var dt1: number = new Date().getTime();

        return drivers.getOutput(url).then((result) => {
          var dt2: number = new Date().getTime();

          var diff: number = dt2 - dt1;

          console.log(`${url} resolved time pre: ${diff}`);

          const recipe = this.parseRecipeHtml(result, url);

          console.log(`${url} resolved time pre next: ${diff}`);

          return drivers
            .getOutput(
              "https://api.sni.foodnetwork.com/moderation-chitter-proxy/v1/ratings/brand/FOOD/type/recipe/id/" +
                recipe.id
            )
            .then((result) => {
              var ob: any = JSON.parse(result);

              var rating: number = ob.ratingsSummaries[0].averageValue;

              rating = Math.round(rating * 100) / 100;

              recipe.rating = rating.toString() + " of 5 stars";

              console.log(result);

              console.log(`${recipe.sourceUrl} resolved`);
              var dt3: number = new Date().getTime();

              var diff2: number = dt3 - dt1;
              console.log(`${recipe.sourceUrl} resolved time: ${diff2}`);

              // Send to user here.
              this.io.to(this.id).emit("senddata", recipe);

              return recipe;
            });
        });
      })
    );
    console.log(`web scraping completed`);
    dataAccess.upload(recipeResults);
    return recipeResults;
  }

  async returnRecipesFromKeyword(
    keyword: string,
    drivers: driverPool,
    numPages: Number
  ): Promise<IRecipe[]> {
    const pageUrls = [];
    for (let page = 0; page < numPages; page++) {
      pageUrls.push(this.getUrlForPage(page, keyword));
    }
    const pageResults = await drivers.getOutputs(pageUrls);
    var recipeUrls: string[] = [];
    for (const page of pageResults) {
      recipeUrls.push(...this.getRecipeUrlsFromPage(page));
    }

    recipeUrls = recipeUrls.slice(0, 4);

    return this.getRecipes(drivers, recipeUrls);
  }

  async getRecipes(drivers: driverPool, urls: string[]): Promise<IRecipe[]> {
    var lthis = this;
    var reps: IRecipe[] = [];

    return new Promise(function (resolve, reject) {
      lthis.getRecipesEx(drivers, urls, 0, reps, resolve, lthis);
    });
  }

  getRecipesEx(
    drivers: driverPool,
    urls: string[],
    index: number,
    recipes: IRecipe[],
    resolve: any,
    lthis: any
  ) {
    if (index == urls.length) {
      resolve(recipes);
      return;
    }

    lthis.getRecipe(drivers, urls[index], lthis).then(function (result: any) {
      recipes.push(result);
      lthis.getRecipesEx(drivers, urls, index + 1, recipes, resolve, lthis);
    });
  }

  async getRecipe(
    drivers: driverPool,
    url: string,
    lthis: any
  ): Promise<IRecipe> {
    return new Promise(function (resolve, reject) {
      var dt1: number = new Date().getTime();

      drivers.getOutput(url).then((result) => {
        var dt2: number = new Date().getTime();

        var diff: number = dt2 - dt1;

        console.log(`${url} resolved time pre: ${diff}`);

        const recipe = lthis.parseRecipeHtml(result, url);

        console.log(`${url} resolved time pre next: ${diff}`);

        return drivers
          .getOutput(
            "https://api.sni.foodnetwork.com/moderation-chitter-proxy/v1/ratings/brand/FOOD/type/recipe/id/" +
              recipe.id
          )
          .then((result) => {
            var ob: any = JSON.parse(result);

            if (ob.ratingsSummaries.length > 0) {
              var rating: number = ob.ratingsSummaries[0].averageValue;

              rating = Math.round(rating * 100) / 100;

              recipe.rating = rating.toString() + " of 5 stars";
            } else {
              recipe.rating = "";
            }

            console.log(result);

            // Send to user here.
            console.log(`${recipe.sourceUrl} resolved`);
            var dt3: number = new Date().getTime();

            var diff2: number = dt3 - dt1;
            console.log(`${recipe.sourceUrl} resolved time: ${diff2}`);

            lthis.io.to(lthis.id).emit("senddata", recipe);
            recipeService.uploadRecipe(recipe);

            resolve(recipe);
          });
      });
    });
  }

  /**
   * Given html, return urls for recipes stored on the webpage
   * @param html
   */
  protected abstract getRecipeUrlsFromPage(html: string): Array<string>;

  /**
   * Given a page and keyword, return a url for a page with search results
   * @param page
   * @param keyword
   */
  protected abstract getUrlForPage(page: Number, keyword: string): string;

  /**
   * Given html of a webpage with a recipe, return a recipe object
   * @param html
   * @param url
   */
  protected abstract parseRecipeHtml(html: string, url: string): IRecipe;

  /**
   * Given a query, return the textcontent of the first element or an empty string if null
   * @param html
   * @param cssQuery
   * @returns
   */
  protected getFirstOrEmptyText(doc: string, cssQuery: string): string {
    const html = cheerio.load(doc);
    const result = html(cssQuery)
      .toArray()
      .map((element) => html(element))[0];
    return result === null ? "" : this.textContent(result);
  }

  /**
   * Given a query, return the attribute of the first element or an empty string if null
   * @param html
   * @param cssQuery
   * @param attr
   * @returns
   */
  protected getFirstOrEmptyAttr(
    doc: string,
    cssQuery: string,
    attr: string
  ): string {
    const html = cheerio.load(doc);
    const result = html(cssQuery)
      .toArray()
      .map((element) => html(element).attr(attr))[0];
    return result ? result : "";
  }

  protected query(doc: string, cssQuery: string): cheerio.Cheerio[] {
    const html = cheerio.load(doc);
    const result = html(cssQuery)
      .toArray()
      .map((element) => html(element));
    return result;
  }

  protected returnEmptyRecipe(): IRecipe {
    return {
      id: "",
      label: "",
      title: "",
      steps: [],
      imageUrl: "",
      sourceUrl: "",
      ingredients: [],
      reviewNumber: 0,
      rating: "",
      totalTime: "",
      cuisineType: [],
      mealType: [],
      dishType: [],
    };
  }
  protected textContent(html: cheerio.Cheerio): string {
    return html == null ? "" : html.text() ? html.text() : "";
  }
  protected attribute(element: cheerio.Cheerio, attribute: string): string {
    const result = element.attr(attribute);
    return result ? result : "";
  }
}

export default BaseScraper;
