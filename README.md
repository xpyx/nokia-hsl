## HSL Visualisation

[![CI Workflow](https://github.com/xpyx/nokia-hsl/actions/workflows/android-workflow.yaml/badge.svg)](https://github.com/xpyx/nokia-hsl/actions/workflows/android-workflow.yaml)

### To run the Android app, open NokiaHslVisualisation directory in Android Studio and run.

### To run the Realtime Transport Updates API:

#### Prerequisites:

[Node](https://nodejs.org/en/) | 
[Npm](https://www.npmjs.com/) | 
[Docker](https://www.docker.com/products/docker-desktop)

#### If you need the GFTS database locally, you can achieve it like this:

EDIT 23.3.2021: The database is now in Azure also, so this isn´t needed anymore

1. `npm install gtfs -g`
2. `cd Realtime-Transport-Updates-API`
3. `npm i` 
4. `docker-compose up -d`
5. `docker exec -it docker-gtfs-db bash`
6. `/opt/mssql-tools/bin/sqlcmd -S localhost -U SA -P 10Password`
7. 1> `CREATE DATABASE gtfsdb`
8. 2> `GO`
9. ctrl-c 
10. ctrl-d
11. `npm run import`

This is going to take a while. Go get coffee.

After this you have a docker container with the GTFS data from HSL to make queries to.

12. `npm run`
13. Go to (http://127.0.0.1:3000/api/routes/1001) to see route information

#### The GTFS database is in Azure now and the server connects to it with variables set in the .env file

1. Setup .env with Azure secrets from here: https://docs.google.com/spreadsheets/d/1x4Js820tdTT7_A2OFcWZzXjuSML6FdiHpvU0n0_NLWQ
2. `npm run prod`
3. Go to (http://127.0.0.1:3000/api/routes/1001) to see route information


### To get the MQTT feed of vehicles

1. `npm install -g mqtt`
2. `mqtt subscribe -h mqtt.hsl.fi -l mqtts -p 8883 -v \
  -t "/hfp/v2/journey/ongoing/vp/+/+/+/+/+/+/+/+/0/#"`

You can check for examples here (https://digitransit.fi/en/developers/apis/4-realtime-api/vehicle-positions/#examples)
