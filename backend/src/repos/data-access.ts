import { IRecipe } from "@models/recipe-model";
import { SearchParam } from "@models/SearchParam";
import { SearchType } from "@models/SearchType";

var axios = require('axios');
const fs = require('fs');
const path = require("path");

const searchType:SearchType = {TITLE:'',INGREDIENT:'',ALL:''};

/**
 * get one object by id.
 * 
 * @returns 
 */
  
async function findOne(id:string): Promise<any> {
        
        const data = {'filter': { "_id": { "$oid": id } } };
   
        return await queryDB(data, 'findOne');
}

async function findByTitle(title:string): Promise<Array<any>> {
          
    var searchParams = [{searchValues:[title], searchType:searchType}];

    return await getRecipesFromQuery(searchParams);
}

function getFieldQuery(field: string, values: Array<String>): String {
    return getRegexQuery(field, getSingleFieldRegex(values), "i");
}

async function getRecipesFromQuery(param: Array<SearchParam>): Promise<any> {

    const filter = getQueryFromEnum(param[0]);
  
    const queryFilter = {filter:  filter }; 

    return await queryDB(queryFilter, "find");
}

function getFieldArrayQuery(field: string, values: Array<String>): String {
    return getRegexQuery(field, values.join(",\n"), "i");
}

function getQueryFromEnum(param: SearchParam): any {
    if (param.searchType) {
        searchType.TITLE = getFieldQuery("title", param.searchValues);
        searchType.INGREDIENT = getFieldArrayQuery("ingredients", param.searchValues);

    }
    return searchType.TITLE;
}

function getSingleFieldRegex(values: Array<String>): String {
    return "^" + "(?=.*" + values.join("") + ")" + ".+";
}

function getRegexQuery(field: string, regex: String, options: String): any {

    var query:any = {};

    query[field] = {'$regex':regex,'$options':options};
 
    return query;
}
 
 async function queryDB(queryParm:any, action:string): Promise<any> {
      
    const fullPath = path.resolve(__dirname, "../creds.txt");
 
    const contents:String = fs.readFileSync(fullPath, {encoding:'utf8', flag:'r'});
    const query = {...queryParm};

    query.collection = 'Recipes';
    query.database = 'CookPal';
    query.dataSource = 'CookPal';

    const queryStr = JSON.stringify(query);
 
    console.log(queryStr); 

    const parts = contents.split(',');

        var config = {
        method: 'post',
        url: 'https://data.mongodb-api.com/app/' + parts[0] + '/endpoint/data/beta/action/' + action,
        headers: {
        'Content-Type': 'application/json',
        'Access-Control-Request-Headers': '*',
        'api-key': parts[1]
        },
        data : queryStr
        };
   
       var resp = await axios(config)
        .then(function (response:any) {

            var rdata:String = JSON.stringify(response.data);
            //console.log(rdata);
        
            return response.data;

        })
        .catch(function (error:any) {
       // console.log(error);
        });

        return resp;
}
 
// Export default
export default {
    findOne,
    findByTitle
} as const;