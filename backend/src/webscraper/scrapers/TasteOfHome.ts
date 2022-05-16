import { IRecipe } from "@models/recipe-model";
import BaseScraper from "./BaseScraper";
import * as cheerio from "cheerio";
class TasteOfHome extends BaseScraper {
  
  getRecipeUrlsFromPage(doc: string): string[] {
    const html = cheerio.load(doc);

    return html(".recipe .entry-title a")
      .toArray()
      .map((col) => this.attribute(html(col), "href"))
      .filter((url) => url.includes("recipes"));
  }

  getUrlForPage(page: Number, keyword: string): string {
    return `https://www.tasteofhome.com/recipes/dishes-beverages/page/${page}/?s=${keyword}`;
  }

  parseRecipeHtml(doc: string, url: string): IRecipe {
    const recipe = this.returnEmptyRecipe();
    const html = cheerio.load(doc);
    recipe.title = this.getFirstOrEmptyText(doc, "h1");
    if (recipe.title == "") {
      return recipe;
    }
    recipe.sourceUrl = url;

    // Sometimes videos are used instead of images. In that case no image is returned
    recipe.imageUrl = this.getFirstOrEmptyAttr(
      doc,
      "div .featured-container img",
      "src"
    );

    recipe.steps = html(".recipe-directions__item span")
      .toArray()
      .map((col) => this.textContent(html(col)));

    recipe.ingredients = html(".recipe-ingredients li")
      .toArray()
      .map((ingredient) => this.textContent(html(ingredient)));

    recipe.totalTime = this.getFirstOrEmptyText(doc, ".total-time p");

    const recipeNumberInput = html(".rating a").toArray();
    var recipeNumberString = "";
    if (recipeNumberInput.length) {
      recipeNumberString = this.textContent(
        html(recipeNumberInput[recipeNumberInput.length - 1])
      );
    }

    if (
      recipeNumberString == "Add a Review" ||
      recipeNumberInput.length === 0
    ) {
      recipe.rating = "Not reviewed";
      recipe.reviewNumber = 0;
      return recipe;
    }
    var rating = 0.0;
    html(".rating a i")
      .toArray()
      .forEach((it) => {
        var starStatus = html(it);
        if (starStatus.hasClass("dashicons-star-half")) {
          rating += 0.5;
        } else if (starStatus.hasClass("dashicons-star-filled")) {
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
    return recipe;
  }
}

export default TasteOfHome;
