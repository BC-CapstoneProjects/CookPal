 
 

var axios = require('axios');


/**
 * get one object by id.
 * 
 * @returns 
 */

 async function findOne(id:string): Promise<any> {
      
    var data = JSON.stringify({
        "collection": "Recipes",
        "database": "CookPal",
        "dataSource": "CookPal",
        "filter": { "_id": { "$oid": id } }
        });
  
        console.log(data);
 
        return await queryDB(data, 'findOne');
}

 async function queryDB(query:any, action:string): Promise<any> {
      
        var config = {
        method: 'post',
        url: 'https://data.mongodb-api.com/app/data-dgwfl/endpoint/data/beta/action/' + action,
        headers: {
        'Content-Type': 'application/json',
        'Access-Control-Request-Headers': '*',
        'api-key': 'L2jOucju0Ifu2EQYOQisTKvef9UTfP4Y6b63sQFS4ImyT17cb4odHk0GP9bZWUhh'
        },
        data : query
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
