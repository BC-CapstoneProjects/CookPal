 
 

var axios = require('axios');
const fs = require('fs');
const path = require("path");

/**
 * get one object by id.
 * 
 * @returns 
 */

 async function findOne(id:string): Promise<any> {
        
        const data = {'filter': { "_id": { "$oid": id } } };
   
        return await queryDB(data, 'findOne');
}
 
 async function queryDB(queryParm:any, action:string): Promise<any> {
      
    const fullPath = path.resolve(__dirname, "../creds.txt");
 
    const contents:String = fs.readFileSync(fullPath, {encoding:'utf8', flag:'r'});
    const query = {...queryParm};

    query.collection = 'Recipes';
    query.database = 'CookPal';
    query.dataSource = 'CookPal';

    const queryStr = JSON.stringify(query);

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
        console.log(error);
        });

        return resp;
}
 
// Export default
export default {
    findOne
} as const;
