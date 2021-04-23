# AR Visualisation tool
#### for transportation problems in the HSL public transport region

[![CI Workflow](https://github.com/xpyx/nokia-hsl/actions/workflows/android-workflow.yaml/badge.svg)](https://github.com/xpyx/nokia-hsl/actions/workflows/android-workflow.yaml)

This repository contains:

- An Android client for displaying realtime public transport data
    - [High-frequency positioning](https://digitransit.fi/en/developers/apis/4-realtime-api/vehicle-positions/) of vehicles via MQTT [API Terms of use](https://digitransit.fi/en/developers/apis/6-terms-of-use() 
    - [HSL alerts](https://digitransit.fi/en/developers/apis/1-routing-api/disruption-info/) via Apollo GraphQL Client
    - Traffic data from Here Maps API
    - AR map 

#### To run the Android app

1. Open NokiaHslVisualisation directory in Android Studio
2. Add here maps APIkey to local.properties
    - row should look like:
    `HERE_MAPS_API_KEY="<API KEY>"`
3. Preferably use your device's mobile data. Some network firewalls block MQTT traffic.

#### Used libraries:

- Jetpack Navigation 2.3.3 [License](https://developer.android.com/license)
- Room DB 2.2.6 [License](https://developer.android.com/license)
- Lifecycle 2.3.0 [License](https://developer.android.com/license)
- Coroutines 1.4.2 [License](https://developer.android.com/license)
- AR Sceneform 1.17.1 [License](https://github.com/google-ar/sceneform-android-sdk/blob/master/LICENSE)
- OsmDroid 6.1.6 [License](https://github.com/osmdroid/osmdroid/blob/master/LICENSE)
- Retrofit 2.9.0 [License](https://github.com/square/retrofit/blob/master/LICENSE.txt)
- Jacoco 0.8.5 [License](https://www.eclemma.org/jacoco/trunk/doc/license.html)
- Apollo Graphql Client 2.5.5 [License](https://github.com/apollographql/apollo/blob/main/LICENSE)
- Paho Mqtt Client 1.1.0 [License](https://www.eclipse.org/org/documents/epl-v10.php)
- Mapbox 9.6.1 [Terms of service](https://www.mapbox.com/legal/tos)


---


[Link to tools and instructions no more in use](README_OLD.md)
