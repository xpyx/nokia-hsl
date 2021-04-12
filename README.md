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

[Link to tools and instructions no more in use](README_OLD.md)
