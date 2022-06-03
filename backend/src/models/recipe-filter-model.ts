// Recipe filter schema
export interface IRecipeFilter {
  ingredients: string;
  rating: number;
  minMins: number;
  maxMins: number;
}

/**
 * Get a new Recipe filter object.
 *
 * @returns
 */
function getNew(
  ingredients: string,
  rating: number,
  minMins: number,
  maxMins: number
): IRecipeFilter {
  return {
    ingredients,
    rating,
    minMins,
    maxMins,
  };
}

/**
 * Copy a recipe filter object.
 *
 * @param user
 * @returns
 */
function copy(recipe: IRecipeFilter): IRecipeFilter {
  return {
    ingredients: recipe.ingredients,
    rating: recipe.rating,
    minMins: recipe.minMins,
    maxMins: recipe.maxMins,
  };
}

// Export default
export default {
  new: getNew,
  copy,
};
