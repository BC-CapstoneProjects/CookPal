import * as webdriver from "selenium-webdriver";
import * as Chrome from "selenium-webdriver/chrome";
import * as https from "https";
import { IncomingMessage } from "http";

class DriverPool {
  chromeProfileIndex = 0;
  async getOutput(url: string): Promise<string> {
    console.log(url);
    return new Promise(function (resolve, _reject) {
      https.get(url, function (http_res: IncomingMessage) {
        var source: string = "";
        http_res.on("data", (data: string) => (source += data));
        http_res.on("end", () => resolve(source));
      });
    });
  }

  async getOutputOut(url: string): Promise<string> {
    console.log(`${url} started`);
    let options = new Chrome.Options();
    options.addArguments(
      "--disable-dev-shm-usage",
      "--headless",
      "--log-level=3"
    );
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
