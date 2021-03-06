import { IRecipe } from "@models/recipe-model";
import { SearchParam } from "@models/SearchParam";
import { SearchType } from "@models/SearchType";
import utils from "../utils/utils";
import axios, { AxiosRequestConfig } from "axios";
import fs from "fs";
import path from "path";
import mysql from "mysql";

var connection: mysql.Connection;

const searchType: SearchType = {
  TITLE: "",
  INGREDIENTS: "",
  ALL: "",
  RATING: "",
};

function connectToMysql() {
  if (connection) {
    return;
  }

  const fullPath = path.resolve(__dirname, "../dbcreds.txt");

  const pwd: string = fs.readFileSync(fullPath, {
    encoding: "utf8",
    flag: "r",
  });

  connection = mysql.createConnection({
    host: "localhost",
    user: "nduser",
    password: pwd,
    database: "nddb",
  });

  connection.connect();
}

// put request in to update the server code
// returns results of query exection
function putInPendingCodeUpdate(): Promise<any> {
  return new Promise(function (resolve, reject) {
    connectToMysql();

    connection.query(
      "INSERT INTO tblcodeupd (val) VALUES (0)",
      function (error: any, results: any, fields: any) {
        if (error) {
          return reject(error);
        }
        console.log("The solution is: ", results);
        resolve(results);
      }
    );
  });
}

// check if there are pending rquests to update the sever code
// returns an array of rending code updates, the array could be 0 size
function getPendingCodeUpdates(): Promise<Array<any>> {
  return new Promise(function (resolve, reject) {
    connectToMysql();

    connection.query(
      "SELECT * FROM tblcodeupd WHERE val = 0",
      function (error: any, results: any, fields: any) {
        if (error) {
          return reject(error);
        }
        console.log("The solution is: ", results);
        resolve(results);
      }
    );
  });
}

/**
 * get one recipe by id.
 *
 * @id the id of the recipe
 * @returns a recipe object
 */
async function findOne(id: string): Promise<IRecipe | null> {
  const data = { filter: { _id: { $oid: id } } };

  // two statements are needed because typescript error occurs with one statement, says
  /*
  Element implicitly has an 'any' type because expression of type '0' can't be used to index type 'Promise<IRecipe[]>'.
  Property '0' does not exist on type 'Promise<IRecipe[]>'
  */
  // reutrn await queryDB(data, "findOne")[0];

  const res: Array<IRecipe> = await queryDB(data, "findOne");

  console.log(res);

  return res[0];
}

/**
 * get recipes by title.
 *
 * @title the title of the recipe
 * @returns an array of recipe objects
 */

async function findByTitle(title: string): Promise<Array<IRecipe>> {
  const searchParams = [{ searchValues: [title], searchType: searchType }];

  return await getRecipesFromQuery(searchParams, "title");
}

async function findByRating(rating: string): Promise<Array<IRecipe>> {
  var ratingStr: string = rating + " of 5 stars";

  const searchParams = [{ searchValues: [ratingStr], searchType: searchType }];

  return await getRecipesFromQuery(searchParams, "rating");
}

function getFieldQuery(field: string, values: Array<string>): string {
  return getRegexQuery(field, getSingleFieldRegex(values), "i");
}

async function getRecipesFromQuery(
  param: Array<SearchParam>,
  searchBy: string
): Promise<Array<IRecipe>> {
  const filter = getQueryFromEnum(param[0], searchBy);

  const queryFilter = { filter: filter };

  return await queryDB(queryFilter, "find");
}

function getFieldArrayQuery(field: string, values: Array<string>): string {
  return getRegexQuery(field, values.join(",\n"), "i");
}

function getQueryFromEnum(param: SearchParam, searchBy: string): any {
  if (searchBy == "rating") {
    searchType.RATING = getFieldQuery("rating", param.searchValues);
    return searchType.RATING;
  } else if (searchBy == "title") {
    searchType.TITLE = getFieldQuery("title", param.searchValues);
    return searchType.TITLE;
  } else {
    searchType.INGREDIENTS = getFieldArrayQuery(
      "ingredients",
      param.searchValues
    );
    return searchType.INGREDIENTS;
  }
}

function getSingleFieldRegex(values: Array<string>): string {
  return "^" + "(?=.*" + values.join("") + ")" + ".+";
}

function getRegexQuery(field: string, regex: string, options: string): any {
  const query: any = {};

  query[field] = { $regex: regex, $options: options };

  return query;
}

async function uploadRecipe(recipe: IRecipe): Promise<IRecipe> {
  const operation = {
    upsert: true,
    update: { $set: recipe },
    filter: { id: recipe.id },
  };

  var res: IRecipe[] = await queryDB(operation, "updateOne");

  return res[0];
}

function upload(recipes: Array<IRecipe>): Promise<Array<IRecipe>> {
  const documents = { documents: recipes };
  console.log(documents);
  return queryDB(documents, "insertMany");
}

/**
 * make a https call to the mongo db server with a query to get some data
 * @param queryParm the db query
 * @param action which action we are requesting from the database
 * @returns an array of recipes
 */
async function queryDB(
  queryParm: any,
  action: string
): Promise<Array<IRecipe>> {
  const fullPath = path.resolve(__dirname, "../creds.txt");

  const contents: string = fs.readFileSync(fullPath, {
    encoding: "utf8",
    flag: "r",
  });
  const query = { ...queryParm };

  query.collection = "Recipes";
  query.database = "CookPal";
  query.dataSource = "CookPal";

  const queryStr = JSON.stringify(query);

  console.log(queryStr);
  utils.log(queryStr);

  const parts = contents.split(",");

  const config: AxiosRequestConfig = {
    method: "post",
    url:
      "https://data.mongodb-api.com/app/" +
      parts[0] +
      "/endpoint/data/beta/action/" +
      action,
    headers: {
      "Content-Type": "application/json",
      "Access-Control-Request-Headers": "*",
      "api-key": parts[1],
    },
    data: queryStr,
  };

  const resp = await axios(config)
    .then(function (response: any) {
      const rdata: string = JSON.stringify(response.data);
      //console.log(rdata);

      // access the recipes off the document or documents, document is used with the action findOne, documents is used with the action find
      if (response.data.document) {
        return response.data.document;
      } else if (response.data.documents) {
        return response.data.documents;
      } else {
        return []; // return empty array otherwise if the response data has no document or documents
      }
    })
    .catch(function (error: any) {
      // console.log(error);
    });

  // findOne returns single object but this method excepts an array to be returned
  if (action == "findOne") {
    return [resp];
  } else {
    return resp;
  }
}

// Export default
export default {
  findOne,
  findByTitle,
  putInPendingCodeUpdate,
  getPendingCodeUpdates,
  upload,
  findByRating,
  uploadRecipe,
} as const;
