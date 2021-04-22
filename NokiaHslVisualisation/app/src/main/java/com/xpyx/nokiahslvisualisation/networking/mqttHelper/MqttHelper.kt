package com.xpyx.nokiahslvisualisation.networking.mqttHelper

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.xpyx.nokiahslvisualisation.fragments.vehicles.VehicleFragment
import com.xpyx.nokiahslvisualisation.model.mqtt.VehiclePosition
import com.xpyx.nokiahslvisualisation.utils.Constants.Companion.HSL_CLIENT_USER_NAME
import com.xpyx.nokiahslvisualisation.utils.Constants.Companion.HSL_MQTT_HOST
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MqttHelper() {

    private lateinit var mqttAndroidClient: MqttAndroidClient

    // Topic variables:
    // See for more information -> https://digitransit.fi/en/developers/apis/4-realtime-api/vehicle-positions/#event-types

    // Use "+" for wildcard

    private var temporalType = "ongoing"    // The status of the journey, ongoing or upcoming

    private var eventType = "vp"            // One of vp, due, arr, dep, ars, pde, pas, wait, doo,
                                            // doc, tlr, tla, da, dout, ba, bout, vja, vjout
                                            // Note: events are not available for metros (metro),
                                            // U-line buses (ubus), robot buses (robot) and ferries (ferry).

    private var transportMode = "tram"       // The type of the vehicle. One of bus, tram, train,
                                            // ferry, metro, ubus (used by U-line buses and other
                                            // vehicles with limited realtime information) or robot (used by robot buses).

    private var routeId = "1010"            // The ID of the route the vehicle is running on. This
                                            // matches route_id in GTFS (field gtfsId of Route in the routing API).

    private var directionId = "1"           // The line direction of the trip, either 1 or 2.
                                            // Note: This does not exactly match direction_id in GTFS or the routing API.
                                            // Value 1 here is same as 0 in GTFS and the Routing API.
                                            // Value 2 here is same as 1 in GTFS and the Routing API.

    private var startTime = "+"             // The scheduled start time of the trip, i.e. the scheduled departure time
                                            // from the first stop of the trip. The format follows HH:mm in 24-hour
                                            // local time, not the 30-hour overlapping operating days present in GTFS.

    private var nextStop = "+"              // The ID of next stop or station. Updated on each departure from or
                                            // passing of a stop. EOL (end of line) after final stop and empty if the vehicle
                                            // is leaving HSL area. Matches stop_id in GTFS (value of gtfsId field, without
                                            // HSL: prefix, in Stop type in the routing API).

    private var geohashLevel = "+"          // By subscribing to specific geohash levels, you can reduce the amount of
                                            // traffic into the client. By only subscribing to level 0 the client gets
                                            // the most important status changes. The rough percentages of messages with a
                                            // specific geohash_level value out of all ongoing messages are:
                                            // 0: 3 % / 1: 0.09 % / 2: 0.9 % / 3: 8 % / 4: 43 % / 5: 44 %

    private var geohash = "+"               // The latitude and the longitude of the vehicle. The digits of the integer
                                            // parts are separated into their own level in the format <lat>;<long>, e.g. 60;24.
                                            // The digits of the fractional parts are split and interleaved into a custom format
                                            // so that e.g. (60.123, 24.789) becomes 60;24/17/28/39.
                                            // This format enables subscribing to specific geographic boundaries easily.
                                            // If the coordinates are missing, geohash_level and geohash have the concatenated
                                            // value 0////. This geohash scheme is greatly simplified from the original geohash scheme

    private var topic: String =
        "/hfp/v2/journey" +
        "/$temporalType" +
        "/$eventType" +
        "/$transportMode" +
        "/+/+" +
        "/$routeId" +
        "/$directionId" +
        "/$startTime" +
        "/$nextStop" +
        "/$geohashLevel" +
        "/$geohash/#"

    fun connect(applicationContext: Context) {

        mqttAndroidClient = MqttAndroidClient(
            applicationContext,
            HSL_MQTT_HOST,
            HSL_CLIENT_USER_NAME
        )

        try {
            val token = mqttAndroidClient.connect()
            token.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    subscribe(topic)
                    //connectionStatus = true
                    // Give your callback on connection established here
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    //connectionStatus = false
                    // Give your callback on connection failure here
                    exception.printStackTrace()
                }
            }
        } catch (e: MqttException) {
            // Give your callback on connection failure here
            e.printStackTrace()
        }
    }

    fun subscribe(topic: String) {
        val qos = 2 // Mention your qos value
        try {
            mqttAndroidClient.subscribe(topic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    // Give your callback on Subscription here
                    Log.d("DBG", "subscription OK")

                }

                override fun onFailure(
                    asyncActionToken: IMqttToken,
                    exception: Throwable
                ) {
                    // Give your subscription failure callback here
                }
            })
        } catch (e: MqttException) {
            System.err.println("Exception whilst subscribing to topic '$topic'")
            e.printStackTrace()
        }
    }

    fun receiveMessages(vehicleFragment: VehicleFragment) {

        val gson = Gson()

        mqttAndroidClient.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable) {
                //connectionStatus = false
                // Give your callback on failure here
            }

            override fun messageArrived(topic: String, message: MqttMessage) {

                try {
                    val data = String(message.payload, charset("UTF-8"))
                    val vehiclePosition = gson.fromJson(data, VehiclePosition::class.java)

                    // Here I update the fragment that shows the data
                    vehicleFragment.updateUI(vehiclePosition)

                    Log.d("DBG", vehiclePosition.toString())

                } catch (e: Exception) {
                    // Give your callback on error here
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
                // Acknowledgement on delivery complete
            }
        })
    }

    fun unSubscribe() {
        try {
            val unsubToken = mqttAndroidClient.unsubscribe(topic)
            unsubToken.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    // Give your callback on unsubscribing here
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    // Give your callback on failure here
                }
            }
        } catch (e: MqttException) {
            // Give your callback on failure here
        }
    }

    fun destroy() {
        mqttAndroidClient.unregisterResources()
        mqttAndroidClient.disconnect()
    }
}
