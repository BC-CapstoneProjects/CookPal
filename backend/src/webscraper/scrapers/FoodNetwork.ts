import { IRecipe } from "@models/recipe-model";
import BaseScraper from "./BaseScraper";

class FoodNetwork extends BaseScraper {
  parseRecipeHtml(html: Document, url: string): IRecipe {
    const recipe = this.returnEmptyRecipe();

    recipe.title = this.getFirstOrEmptyText(
      html,
      ".m-RecipeSummary .assetTitle .o-AssetTitle__a-HeadlineText:first-of-type"
    );
    if (recipe.title == "") return recipe;

    recipe.steps = [...html.querySelectorAll(".o-Method__m-Body li")].map(
      (col) => this.textContent(col)
    );

    recipe.imageUrl = this.getFirstOrEmptyAttr(
      html,
      ".recipe-lead .m-MediaBlock__m-MediaWrap img",
      "src"
    );
    if (recipe.imageUrl != "") {
      recipe.imageUrl = "https:" + recipe.imageUrl;
    }

    recipe.ingredients = [
      ...html.querySelectorAll(".o-Ingredients__a-Ingredient--CheckboxLabel"),
    ]
      .map((col) => this.textContent(col))
      .filter((val) => val !== "Deselect All");

    recipe.totalTime = this.getFirstOrEmptyText(
      html,
      ".o-RecipeInfo__m-Time .o-RecipeInfo__a-Description"
    );
    recipe.sourceUrl = url;
    const recipeNumberString = this.getFirstOrEmptyText(
      html,
      ".review .review-summary span"
    );

    const numLength = recipeNumberString.indexOf(" ");
    if (1 <= numLength && numLength <= 6) {
      recipe.reviewNumber = parseInt(
        recipeNumberString.substring(0, numLength)
      );
    }
    recipe.rating = this.getFirstOrEmptyAttr(
      html,
      ".review .review-summary .rating-stars",
      "title"
    );

    return recipe;
  }
  getRecipeUrlsFromPage(html: Document): string[] {
    return [
      ...html.querySelectorAll(
        ".o-RecipeResult .l-List .m-MediaBlock__a-Headline a"
      ),
    ]
      .map((col) => col.getAttribute("href"))
      .map((recipe) => `https:${recipe}`);
  }
  getUrlForPage(page: Number, keyword: string): string {
    return `https://www.foodnetwork.com/search/${keyword}-/p/${page}/CUSTOM_FACET:RECIPE_FACET`;
  }
}
