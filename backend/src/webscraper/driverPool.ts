const { Builder, By, Key, until } = require("selenium-webdriver");
const webdriver = require("selenium-webdriver");
require("chromedriver");
const fs = require("fs");
class DriverPool {
  chromeProfileIndex = 0;
  //private const driverPool: Array<C

  async getOutput(url: string): Promise<string> {
    console.log(url);
    await this.chromeProfileIndex++;
    let capabilities = webdriver.Capabilities.chrome();

    const chromeOptions = {
      args: [
        "--no-sandbox",
        "--disable-dev-shm-usage",
        `--user-data-dir=C:/ChromeProfiles/Profile${this.chromeProfileIndex}`,
        // "--headless",
        // "--log-level=3",
      ],
    };

    capabilities.set("chromeOptions", chromeOptions);
    console.log(JSON.stringify(capabilities));
    console.log("this is next");
    let driver = await new Builder()
      .withCapabilities(capabilities)
      .forBrowser("chrome")
      .build();
    await driver.get(url);

    const source = driver.getPageSource();
    await driver.quit();
    return source;
  }
  async getOutputs(urls: Array<string>): Promise<string[]> {
    console.log(urls);
    const result = urls.map(async (url) => await this.getOutput(url));

    return Promise.all(result).then((result) => {
      return result;
    });
  }
}
export default DriverPool;
