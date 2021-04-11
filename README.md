# AR Visualisation tool
#### for transportation problems in the HSL public transport region

[![CI Workflow](https://github.com/xpyx/nokia-hsl/actions/workflows/android-workflow.yaml/badge.svg)](https://github.com/xpyx/nokia-hsl/actions/workflows/android-workflow.yaml)

This repository contains:

- Android client for displaying realtime public transport data
    - [High-frequency positioning](https://digitransit.fi/en/developers/apis/4-realtime-api/vehicle-positions/) of vehicles via MQTT 
    - [HSL alerts](https://digitransit.fi/en/developers/apis/1-routing-api/disruption-info/) via Apollo GraphQL Client
    - Traffic data from Here Maps API
    - AR map 
- Node Express server for connecting to HSL GTFS-RT feed and to a (slow) SQL Server in Azure cloud

Used libraries:

- Jetpack Navigation 2.3.3 [License](https://developer.android.com/license)
- Apollo Graphql Client 2.5.5 [License](https://github.com/apollographql/apollo/blob/main/LICENSE)
- Paho Mqtt Client 1.1.0 [License](https://www.eclipse.org/org/documents/epl-v10.php)
- Mapbox 9.6.1 [Terms of service](https://www.mapbox.com/legal/tos)
- Jacoco 0.8.5 [License](https://www.eclemma.org/jacoco/trunk/doc/license.html)
- Room DB 2.2.6 [License](https://developer.android.com/license)
- Retrofit 2.9.0 [License](https://github.com/square/retrofit/blob/master/LICENSE.txt)
- Lifecycle 2.3.0 [License](https://developer.android.com/license)
- OsmDroid 6.1.6 [License](https://github.com/osmdroid/osmdroid/blob/master/LICENSE)
- Coroutines 1.4.2 [License](https://developer.android.com/license)
- AR Sceneform 1.17.1 [License](https://github.com/google-ar/sceneform-android-sdk/blob/master/LICENSE)

#### To run the Android app

1. Open NokiaHslVisualisation directory in Android Studio
2. Add here maps APIkey to local.properties
    - row should look like:
    `HERE_MAPS_API_KEY="<API KEY>"`

#### To run the Realtime Transport Updates API server

Prerequisites: [Node](https://nodejs.org/en/) & [Npm](https://www.npmjs.com/)

You will need to set the connection variables in the .env file

0. `cd Realtime-Transport-Updates-API`
1. `cp .env.example .env`
2. Edit the `.env` file. Add the Azure secrets from this sheet: https://docs.google.com/spreadsheets/d/1x4Js820tdTT7_A2OFcWZzXjuSML6FdiHpvU0n0_NLWQ
3. If you can't access the sheet, contact ville.pystynen@metropolia.fi
4. `npm run prod`
5. Go to (http://127.0.0.1:3000/api/routes/1001) to see route information

## Tools no more in use (updated in case a library is dropped)

Docker

## Instructions no more in use 

#### To get the MQTT feed of vehicles

1. `npm install -g mqtt`
2. `mqtt subscribe -h mqtt.hsl.fi -l mqtts -p 8883 -v \
  -t "/hfp/v2/journey/ongoing/vp/+/+/+/+/+/+/+/+/0/#"`

You can check for examples here (https://digitransit.fi/en/developers/apis/4-realtime-api/vehicle-positions/#examples)

#### If you need the GFTS database locally, you can achieve it like this:

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
