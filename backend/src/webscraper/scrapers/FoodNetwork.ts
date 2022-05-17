import { IRecipe } from "@models/recipe-model";
import BaseScraper from "./BaseScraper";
import * as cheerio from "cheerio";

class FoodNetwork extends BaseScraper {
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
