# AR Visualisation tool
#### for transportation problems in the HSL public transport region
 
[![CI Workflow](https://github.com/xpyx/nokia-hsl/actions/workflows/android-workflow.yaml/badge.svg)](https://github.com/xpyx/nokia-hsl/actions/workflows/android-workflow.yaml)
[![License: GPL v2](https://img.shields.io/badge/License-GPL%20v2-blue.svg)](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html)

This repository contains:

- An Android application for displaying realtime public transport data 
    - [High-frequency positioning](https://digitransit.fi/en/developers/apis/4-realtime-api/vehicle-positions/) of vehicles via MQTT [API Terms of use](https://digitransit.fi/en/developers/apis/6-terms-of-use)
    - [HSL alerts](https://digitransit.fi/en/developers/apis/1-routing-api/disruption-info/) via Apollo GraphQL Client
    - Traffic data from Here Maps API (you need to provide your own API key for this functionality)
    - AR map for displaying public transport vehicle positions and alerts from HERE maps
    - Data visualization

#### To run the Android app via Android Studio

1. Open NokiaHslVisualisation directory in Android Studio
2. Add here maps APIkey to local.properties
    - row should look like:
    `HERE_MAPS_API_KEY="<API KEY>"`
3. Preferably use your device's mobile data. Some network firewalls block MQTT traffic.

#### Install from APK

1. To get the apk, use the releases tab in this repository.

#### Install from Google Play

1. To install from Google Play you need to be part of the Mobile Project course of Metropolia. All those who are in the course can access the application installation via Google Play from [here](https://play.google.com/apps/internaltest/4699344503639103194). When you click the link it might tell you that "The application is not in use on this account." Then simply change your user to your Metropolia account and you will get access to the installation.

#### Instructions

There are 5 tabs to navigate to:

- Home: here you can find the HSL public transport alerts and browse them
- AR map: this is the place where you can access the AR map. Simply find a place for the map by moving your phone on a surface, when dots appear, tap your screen and the map will manifest itself onto the screen. You can filter the view by swiping from left to right. That brings out the filtering menu. You can hide it by swiping it back from right to left.
- Alerts: this is the place where you can find the alerts from HERE Maps API. First you need to input your private API key.
- AR view: this is the view that tells you where the vehicles are in the real world. It locates your position and creates a bounding box and then shows you all the vehicles as 3D objects on your screen.
- Analytics: this view shows you information of all the buses arriving at bus stops in greater Helsinki area


#### Used libraries:

- Jetpack Navigation 2.3.3 [License](https://developer.android.com/license)
- Room DB 2.2.6 [License](https://developer.android.com/license)
- Lifecycle 2.3.0 [License](https://developer.android.com/license)
- Coroutines 1.4.2 [License](https://developer.android.com/license)
- AR Core 1.23.0 [License](https://creativecommons.org/licenses/by/4.0/)
- Sceneform 1.17.1 [License](https://github.com/google-ar/sceneform-android-sdk/blob/master/LICENSE)
- OsmDroid 6.1.6 [License](https://github.com/osmdroid/osmdroid/blob/master/LICENSE)
- Retrofit 2.9.0 [License](https://github.com/square/retrofit/blob/master/LICENSE.txt)
- Jacoco 0.8.5 [License](https://www.eclemma.org/jacoco/trunk/doc/license.html)
- Apollo Graphql Client 2.5.5 [License](https://github.com/apollographql/apollo/blob/main/LICENSE)
- Paho Mqtt Client 1.1.0 [License](https://www.eclipse.org/org/documents/epl-v10.php)
- Glide 4.9.0 [License](https://search.maven.org/artifact/com.github.bumptech.glide/glide/4.9.0/aar)
- Gson 2.8.6 [License](https://github.com/google/gson/blob/master/LICENSE)
- AAChartKit [Github](https://github.com/AAChartModel/AAChartKit/blob/master/LICENSE)

---


[Demo video](https://youtu.be/bQxho0rFLpQ)


---


[Link to tools and instructions no more in use](README_OLD.md)
