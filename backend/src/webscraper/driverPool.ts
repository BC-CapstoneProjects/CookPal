import * as webdriver from "selenium-webdriver";
import * as Chrome from "selenium-webdriver/chrome";
var https = require("https");

class DriverPool {
  chromeProfileIndex = 0;
  async getOutput(url: string): Promise<string> {
    var indexOfPath: number = url.indexOf("/", 10);

    var path: string = url.substring(indexOfPath);
    var host: string = url.substring(0, indexOfPath);
    host = host.replace("https://", "");

    var options = {
      host: host,
      path: path,
    };

    console.log("start requst");

    return new Promise(function (resolve, reject) {
      https.get(options, function (http_res: any) {
        var source: string = "";
        // initialize the container for our data

        // this event fires many times, each time collecting another piece of the response
        http_res.on("data", function (chunk: any) {
          // append this chunk to our growing `data` var
          source += chunk;
        });

        // this event fires *one* time, after all the `data` events/chunks have been gathered
        http_res.on("end", function () {
          // you can use res.send instead of console.log to output via express
          console.log("request done");
          resolve(source);
        });
      });
    });
  }

  async getOutputOut(url: string): Promise<string> {
    console.log(`${url} started`);
    let options = new Chrome.Options();
    options.addArguments("--disable-dev-shm-usage", "--log-level=3");
    let driver = await new webdriver.Builder()
      .forBrowser("chrome")
      .setChromeOptions(options)
      .build();

    await driver.get(url);
    const source = driver.getPageSource();
    await driver.quit();
    return source;
  }

  async getOutputs(urls: Array<string>): Promise<string[]> {
    const result = urls.map(async (url) => await this.getOutput(url));
    return Promise.all(result).then((result) => {
      return result;
    });
  }
}
export default DriverPool;
