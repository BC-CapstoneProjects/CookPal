var fs = require("fs");

class utils {
  public static logerror(data: string) {
    utils.log(data, "error-log");
  }

  /**
   *
   * @param data the data to be written to the log file
   * @param name the name of the log file
   * return nothings
   */
  public static log(data: string, name: string = "log") {
    var dir = "./logs";

    // since log folder isn't in git we need to create it if it doesn't exist
    if (!fs.existsSync(dir)) {
      fs.mkdirSync(dir);
    }

    const date = new Date();

    var filename =
      name +
      `-${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}.txt`;
    var fullPath = dir + "/" + filename;

    fs.appendFile(fullPath, data + "\n", (err: any) => {
      if (err) {
        console.log(err);
      }
    });
  }

  // use to hide error because seeing full error details could be a security risk
  public static hideError(data: any): any {
    const dt = { ...data };

    if (data.error) {
      dt.error = "un error has occurred";
    }

    return dt;
  }
}

export default utils;
