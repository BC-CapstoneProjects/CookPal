import { IRecipe } from "@models/recipe-model";
abstract class BaseScraper {
  retrieveRecipes(
    keyword: string,
    numPages: Number,
    numDrivers: Number,
    writeLocation: string
  ) {
    const driverPool = new DriverPool();
    const allRecipes = this.returnRecipesFromKeyword(
      keyword,
      driverPool,
      numPages
    );
    //TODO: write recipes to mongoDB
  }
  /**
   * Given a keyword, result an array of recipes
   * @param keyword
   * @param driverPool
   * @param numPages
   * @returns
   */
  returnRecipesFromKeyword(
    keyword: string,
    driverPool: DriverPool,
    numPages: Number
  ): IRecipe[] {
    const pageUrls = [];
    for (let page = 0; page < numPages; page++) {
      pageUrls.push(this.getUrlForPage(page, keyword));
    }
    const pageResults = driverPool.getOutputs(pageUrls);
    const recipeUrls: string[] = [];
    const parser = new DOMParser();
    for (const page of pageResults) {
      recipeUrls.push(
        ...this.getRecipeUrlsFromPage(parser.parseFromString(page, "text/html"))
      );
    }
    const recipeResults = driverPool.getOutputs(recipeUrls);
    return recipeResults.map((recipe, index) =>
      this.parseRecipeHtml(
        parser.parseFromString(recipe, "text/html"),
        recipeUrls[index]
      )
    );
  }

  /**
   * Given html, return urls for recipes stored on the webpage
   * @param html
   */
  protected abstract getRecipeUrlsFromPage(html: Document): Array<string>;

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
  protected abstract parseRecipeHtml(html: Document, url: string): IRecipe;

  /**
   * Given a query, return the textcontent of the first element or an empty string if null
   * @param html
   * @param cssQuery
   * @returns
   */
  protected getFirstOrEmptyText(html: Document, cssQuery: string): string {
    const result = html.querySelector(cssQuery);
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
    html: Document,
    cssQuery: string,
    attr: string
  ): string {
    const result = html.querySelector(cssQuery);
    return result ? this.attribute(result, attr) : "";
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
  protected textContent(html: Document | Element): string {
    return html.textContent ? html.textContent : "";
  }
  protected attribute(element: Element, attribute: string): string {
    const result = element.getAttribute(attribute);
    return result ? result : "";
  }
}

export default BaseScraper;
