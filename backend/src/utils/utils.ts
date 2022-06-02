import { IRecipeFilter } from "@models/recipe-filter-model";
import { IRecipe } from "@models/recipe-model";

var fs = require("fs");

class utils {
  public static logerror(data: string) {
    utils.log(data, "error-log");
  }

  public static includeItem(recipe: IRecipe, filter: IRecipeFilter): boolean {
    let rating: string = recipe.rating.substring(0, 3);
    let ratingf: number = parseFloat(rating);

    let totaltime: string = recipe.totalTime.trim();
    let parts: Array<string> = totaltime.split(" ");
    let minutes: number = parseInt(parts[0]);

    var include =
      filter.rating <= ratingf &&
      filter.minMins <= minutes &&
      minutes <= filter.maxMins;

    if (filter.ingredients.trim() == "") {
      if (include) {
        return true;
      }
    } else {
      for (let j: number = 0; j < recipe.ingredients.length; j++) {
        let filterIngrLower: string = filter.ingredients.toLowerCase();
        let itemIngrLower: string = recipe.ingredients[j].toLowerCase();

        if (include && itemIngrLower.indexOf(filterIngrLower) >= 0) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   *
   * @param data the data to be written to the log file
   * @param name the name of the log file
   * return nothings
   */
  public static log(data: string, name: string = "log") {
    var dir = "./logs";

    // since log folder isn't in git we need to create it if it doesn't exist
    if (!fs.existsSync(dir)) {
      fs.mkdirSync(dir);
    }

    const date = new Date();

    var filename =
      name +
      `-${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}.txt`;
    var fullPath = dir + "/" + filename;

    fs.appendFile(fullPath, data + "\n", (err: any) => {
      if (err) {
        console.log(err);
      }
    });
  }

  // use to hide error because seeing full error details could be a security risk
  public static hideError(data: any): any {
    const dt = { ...data };

    if (data.error) {
      dt.error = "un error has occurred";
    }

    return dt;
  }
}

export default utils;
