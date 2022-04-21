import { IRecipe } from "@models/recipe-model";
import BaseScraper from "./BaseScraper";

class TasteOfHome extends BaseScraper {
  getRecipeUrlsFromPage(html: Document): string[] {
    return [...html.querySelectorAll(".recipe .entry-title a")]
      .map((col) => this.attribute(col, "href"))
      .filter((url) => url.includes("recipes"));
  }
  getUrlForPage(page: Number, keyword: string): string {
    return `https://www.tasteofhome.com/recipes/dishes-beverages/page/${page}/?s=${keyword}`;
  }
  parseRecipeHtml(html: Document, url: string): IRecipe {
    const recipe = this.returnEmptyRecipe();

    recipe.title = this.getFirstOrEmptyText(html, "h1");
    if (recipe.title == "") {
      return recipe;
    }

    recipe.sourceUrl = url;

    // Sometimes videos are used instead of images. In that case no image is returned
    recipe.imageUrl = this.getFirstOrEmptyAttr(
      html,
      "div .featured-container img",
      "src"
    );

    recipe.steps = [
      ...html.querySelectorAll(".recipe-directions__item span"),
    ].map((col) => this.textContent(col));

    recipe.ingredients = [
      ...html.querySelectorAll(".recipe-ingredients li"),
    ].map((ingredient) => this.textContent(ingredient));

    recipe.totalTime = this.getFirstOrEmptyText(html, ".total-time p");

    var rating = 0.0;
    const recipeNumberInput = html.querySelectorAll(".rating a");
    var recipeNumberString = "";
    if (recipeNumberInput.length) {
      recipeNumberString = this.textContent(
        recipeNumberInput[recipeNumberInput.length - 1]
      );
    }

    if (
      recipeNumberString == "Add a Review" ||
      recipeNumberInput.length === 0
    ) {
      recipe.rating = "Not reviewed";
      recipe.reviewNumber = 0;
    } else {
      html.querySelectorAll(".rating a i").forEach((it) => {
        var starStatus = it.classList;
        if (starStatus.contains("dashicons-star-half")) {
          rating += 0.5;
        } else if (starStatus.contains("dashicons-star-filled")) {
          rating++;
        }
      });
      recipe.rating = `${rating} out of 5 stars`;
      const numLength = recipeNumberString.indexOf(" ");
      if (1 <= numLength && numLength <= 6) {
        recipe.reviewNumber = parseInt(
          recipeNumberString.substring(0, numLength)
        );
      }
    }

    return recipe;
  }
}
