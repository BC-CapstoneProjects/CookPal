// Recipe schema
export interface IRecipe {
  id: string;
  label: string;
  title: string;
  steps: Array<string>;
  imageUrl: string;
  sourceUrl: string;
  ingredients: Array<string>;
  reviewNumber: number;
  rating: string;
  totalTime: string;
  cuisineType: Array<string>;
  mealType: Array<string>;
  dishType: Array<string>;
}

/**
 * Get a new Recipe object.
 *
 * @returns
 */
function getNew(
  label: string,
  title: string,
  steps: Array<string>,
  imageUrl: string,
  sourceUrl: string,
  ingredients: Array<string>,
  reviewNumber: number,
  rating: string,
  totalTime: string,
  cuisineType: Array<string>,
  mealType: Array<string>,
  dishType: Array<string>
): IRecipe {
  return {
    id: "",
    label,
    title,
    steps,
    imageUrl,
    sourceUrl,
    ingredients,
    reviewNumber,
    rating,
    totalTime,
    cuisineType,
    mealType,
    dishType,
  };
}

/**
 * Copy a recipe object.
 *
 * @param user
 * @returns
 */
function copy(recipe: IRecipe): IRecipe {
  return {
    id: recipe.id,
    label: recipe.label,
    title: recipe.title,
    steps: [...recipe.steps],
    imageUrl: recipe.imageUrl,
    sourceUrl: recipe.id,
    ingredients: [...recipe.ingredients],
    reviewNumber: recipe.reviewNumber,
    rating: recipe.id,
    totalTime: recipe.id,
    cuisineType: [...recipe.cuisineType],
    mealType: [...recipe.mealType],
    dishType: [...recipe.dishType],
  };
}

// Export default
export default {
  new: getNew,
  copy,
};
