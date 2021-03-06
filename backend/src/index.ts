import "./pre-start"; // Must be the first import
import logger from "jet-logger";
import server from "./server";
import wb from "./websocket";

const socketio = require("socket.io");
var url = require("url");

// Constants
const serverStartMsg = "Express server started on port: ",
  port = process.env.PORT || 3000;
var io: any;
var serverWithSockets: any;

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

  serverWithSockets = https.createServer(credentials, server);

  // Start server
  serverWithSockets.listen(port, () => {
    console.log("HTTPS Server running on port " + port);
  });
} else {
  const http = require("http");
  serverWithSockets = http.createServer(server);

  // Start server
  serverWithSockets.listen(port, () => {
    logger.info(serverStartMsg + port);
  });
}

io = socketio(serverWithSockets);

wb.setWS(io);

io.on("connection", (socket: any) => {
  console.log("on connection");

  socket.on("connecttoserverandroid", (id: any) => {
    console.log("on connecttoserverandroid");
    console.log(id);
    socket.join(id);
  });

  socket.on("connecttoserver", (id: any) => {
    console.log("on connecttoserver");
    socket.join(id.id);
    console.log("connecttoserver id: " + JSON.stringify(id));
  });
});
