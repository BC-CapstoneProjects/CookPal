package apilib;

import java.io.File;

import apilib.scrapers.FoodNetwork;
import apilib.scrapers.BaseScraper;
import apilib.scrapers.TasteOfHome;

public class main {
  public static void main(String[] args) {
    if (args.length != 5) {
      System.out.println("usage: keyword numPages numDrivers FoodNetwork|TasteOfHome path_to_json_dir");
      System.exit(1);
    }
    TasteOfHome api = new TasteOfHome();
    var keyword = args[0];
    var numPages = Integer.parseInt(args[1]);
    var numDrivers = Integer.parseInt(args[2]);
    var scraperType = args[3].toLowerCase();
    var path = args[4];
    BaseScraper scraper = null;
    switch (scraperType)
    {
      case "foodnetwork":
          scraper = new FoodNetwork();
          break;
      case "tasteofhome":
        scraper = new TasteOfHome();
        break;
      default:
        System.exit(2);
    }
    var fullPath = new File(path, keyword+"-"+scraperType+".json").getPath();
    api.retrieveRecipes(keyword,numPages,numDrivers, fullPath);
  }
}
