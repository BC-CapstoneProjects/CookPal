import { IRecipe } from "@models/recipe-model";
import BaseScraper from "./BaseScraper";
import * as cheerio from "cheerio";
import DriverPool from "../driverPool";
import recipeService from "@services/recipe-service";
import utils from "../../utils/utils";

class FoodNetwork extends BaseScraper {
  async getRecipe(drivers: DriverPool, url: string): Promise<IRecipe> {
    var recipe = await this.getRecipeBase(drivers, url);
    return new Promise(async (resolve, _reject) => {
      const result = await drivers.getOutput(
        "https://api.sni.foodnetwork.com/moderation-chitter-proxy/v1/ratings/brand/FOOD/type/recipe/id/" +
          recipe.id
      );
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

      // Send to user here.
      if (this.filter == undefined || utils.includeItem(recipe, this.filter)) {
        this.io.to(this.id).emit("senddata", recipe);
      }

      recipeService.uploadRecipe(recipe);
      resolve(recipe);
    });
  }
  getID(doc: string): string {
    var str: string = '"DetailID", "';
    var index: number = doc.indexOf(str);
    var newDoc: string = doc.substring(index + str.length);
    index = newDoc.indexOf('"');
    newDoc = newDoc.substring(0, index);
    return newDoc;
  }

  parseRecipeHtml(doc: string, url: string): IRecipe {
    const recipe = this.returnEmptyRecipe();
    const html = cheerio.load(doc);

    recipe.title = this.getFirstOrEmptyText(
      doc,
      ".m-RecipeSummary .assetTitle .o-AssetTitle__a-HeadlineText:first-of-type"
    );
    if (recipe.title == "") return recipe;

    recipe.steps = html(".o-Method__m-Body li")
      .toArray()
      .map((element) => this.textContent(html(element)).trim());

    recipe.imageUrl = this.getFirstOrEmptyAttr(
      doc,
      ".recipe-lead .m-MediaBlock__m-MediaWrap img",
      "src"
    );
    if (recipe.imageUrl != "") {
      recipe.imageUrl = "https:" + recipe.imageUrl;
    }

    recipe.ingredients = html(".o-Ingredients__a-Ingredient--CheckboxLabel")
      .toArray()
      .map((element) => this.textContent(html(element)))
      .filter((val) => val !== "Deselect All");

    recipe.totalTime = this.getFirstOrEmptyText(
      doc,
      ".o-RecipeInfo__m-Time .o-RecipeInfo__a-Description"
    );
    recipe.sourceUrl = url;
    const recipeNumberString = this.getFirstOrEmptyText(
      doc,
      ".review .review-summary span"
    );

    const numLength = recipeNumberString.indexOf(" ");
    if (1 <= numLength && numLength <= 6) {
      recipe.reviewNumber = parseInt(
        recipeNumberString.substring(0, numLength)
      );
    }
    recipe.rating = this.getFirstOrEmptyAttr(
      doc,
      ".review .review-summary .rating-stars",
      "title"
    );

    recipe.id = this.getID(doc);

    return recipe;
  }
  // add to existing element
  getRecipeUrlsFromPage(doc: string): string[] {
    const html = cheerio.load(doc);
    return html(".o-RecipeResult .l-List .m-MediaBlock__a-Headline a")
      .toArray()
      .map((element) => "https:" + html(element).attr("href"));
  }
  getUrlForPage(page: Number, keyword: string): string {
    return `https://www.foodnetwork.com/search/${keyword}-/p/${page}/CUSTOM_FACET:RECIPE_FACET`;
  }
}

export default FoodNetwork;
