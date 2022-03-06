package apilib;

public class main {
  public static void main(String[] args) {
    if (args.length != 3) {
      System.out.println("usage: keyword numPages numDrivers");
      System.exit(1);
    }
    TasteOfHomeAPI api = new TasteOfHomeAPI();
    var keyword = args[0];
    var numPages = Integer.parseInt(args[1]);
    var numDrivers = Integer.parseInt(args[2]);
    api.retrieveRecipes(keyword,numPages,numDrivers);
  }
}
