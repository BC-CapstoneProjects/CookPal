import * as webdriver from "selenium-webdriver";
import * as Chrome from "selenium-webdriver/chrome";

class DriverPool {
  chromeProfileIndex = 0;

  async getOutput(url: string): Promise<string> {
    console.log(`${url} started`);
    let options = new Chrome.Options();
    options.addArguments(
      "--no-sandbox",
      "--disable-dev-shm-usage",
      "--headless",
      "--log-level=3",
      `--user-data-dir=C:/ChromeProfiles/Profile${this.chromeProfileIndex++}`
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
