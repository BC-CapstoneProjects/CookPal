var fs = require('fs');

class utils
{
    public static log(data: string) {
        
        var dir = './logs';

        if (!fs.existsSync(dir)){
            fs.mkdirSync(dir);
        }

        const date = new Date();
 
        var filename = `log-${date.getFullYear()}-${date.getMonth()}-${date.getDay()}.txt`;
        var fullPath = dir + '/' + filename;
 
        fs.appendFile(fullPath, data + "\n", (err:any) => {
            if (err) {
              console.log(err);
            }
          });
    }

    // use to hide error because seeing full error details could be a security risk
    public static hideError(data:any):any {
        const dt = {...data};

        if (data.error) {
            dt.error = 'un error has occurred';
        }

        return dt;
    }
}

export default utils;