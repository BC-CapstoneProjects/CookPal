1. download and install nodejs

2. run the commands below at command prompt from this folder
npm install
npm run start:dev

3. access web server using this url http://localhost:3000


create file called configenv.yaml and put this file in this folder app/src/main/assets, use information below to put in the file
# replace ip address with the ip address of where the node js server is running
# change env to aws to use awsserver
env: local
localserver: http://10.0.0.167:3000
awsserver: 

create file called creds.txt and put this file in this folder backend/src, put the information below in it, the appid and appkey need to be on one line and seperated by a comma
appid,appkey