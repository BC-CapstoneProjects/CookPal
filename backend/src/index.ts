import "./pre-start"; // Must be the first import
import logger from "jet-logger";
import server from "./server";
import FoodNetwork from "./webscraper/scrapers/FoodNetwork";
// Constants
const serverStartMsg = "Express server started on port: ",
  port = process.env.PORT || 3000;

// use https in production when running server on aws for better security
if (process.env.NODE_ENV === "production") {
  const fs = require("fs");
  const https = require("https");

  // Certificate
  const privateKey = fs.readFileSync(
    "/etc/letsencrypt/live/cookpal.cloud/privkey.pem",
    "utf8"
  );
  const certificate = fs.readFileSync(
    "/etc/letsencrypt/live/cookpal.cloud/cert.pem",
    "utf8"
  );
  const ca = fs.readFileSync(
    "/etc/letsencrypt/live/cookpal.cloud/chain.pem",
    "utf8"
  );

  const credentials = {
    key: privateKey,
    cert: certificate,
    ca: ca,
  };

  const httpsServer = https.createServer(credentials, server);

  // Start server
  httpsServer.listen(port, () => {
    console.log("HTTPS Server running on port " + port);
  });
} else {
  // Start server
  server.listen(port, () => {
    logger.info(serverStartMsg + port);
  });
  const a = new FoodNetwork();
  a.retrieveRecipes("fish", 1);
}
