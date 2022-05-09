import { IRecipe } from "@models/recipe-model";
import driverPool from "../driverPool";
var DOMParser = require("dom-parser");
const cheerio = require("cheerio");
abstract class BaseScraper {
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
    const recipeUrls: string[] = [];
    for (const page of pageResults) {
      recipeUrls.push(...this.getRecipeUrlsFromPage(page));
    }
    console.log(recipeUrls);
    const recipeResults = await drivers.getOutputs(recipeUrls);
    return recipeResults.map((recipe, index) =>
      this.parseRecipeHtml(
        recipe,
        recipeUrls[index]
      )
    );
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
    const result = html(cssQuery).map((_: any, element: cheerio.Cheerio) =>
      html(element)
    )[0];
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
    const result = html(cssQuery).map((_: any, element: cheerio.Cheerio) =>
      html(element).attr(attr)
    )[0];
    return result ? this.attribute(result, attr) : "";
  }

  protected query(doc: string, cssQuery: string): cheerio.Cheerio[] {
    const html = cheerio.load(doc);
    return html(cssQuery).map((_: any, element: cheerio.Cheerio) =>
      html(element).html()
    );
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
