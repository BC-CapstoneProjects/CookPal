import { IRecipeFilter } from "@models/recipe-filter-model";
import { IRecipe } from "@models/recipe-model";
import dataAccess from "./data-access";

/**
 * Get one recipe.
 * @param id the id of the recipe we are looking for
 * @returns a recipe if found otherwise null
 */
async function getOne(id: string): Promise<IRecipe | null> {
  return await dataAccess.findOne(id);
}

/**
 * Gets list of recipes by the title
 * @param title the title of the recipes we are looking for
 * @returns an array of recipes
 */
async function getByTitle(title: string): Promise<Array<IRecipe>> {
  return await dataAccess.findByTitle(title);
}

/**
 * Gets list of recipes by the title and using a filter
 * @param title the title of the recipes we are looking for
 * @returns an array of recipes
 */
async function getByTitleFilter(
  title: string,
  filter: IRecipeFilter
): Promise<Array<IRecipe>> {
  let results = await dataAccess.findByTitle(title);
  let finalResults = [];
  let include = false;

  for (let i: number = 0; i < results.length; i++) {
    let rating = results[i].rating.substring(0, 3);
    let ratingf = parseFloat(rating);

    let totaltime = results[i].totalTime.trim(); // " 5 mins"
    let parts = totaltime.split(" ");
    let minutes = parseInt(parts[0]);

    include =
      filter.rating <= ratingf &&
      filter.minMins <= minutes &&
      minutes <= filter.maxMins;

    if (filter.ingredients.trim() == "") {
      if (include) {
        finalResults.push(results[i]);
      }
    } else {
      for (let j: number = 0; j < results[i].ingredients.length; j++) {
        let filterIngrLower = filter.ingredients.toLowerCase();
        let itemIngrLower = results[i].ingredients[j].toLowerCase();

        if (include && itemIngrLower.indexOf(filterIngrLower) >= 0) {
          finalResults.push(results[i]);
          break;
        }
      }
    }
  }

  return finalResults;
}

// Export default
export default {
  getOne,
  getByTitle,
  getByTitleFilter,
} as const;
