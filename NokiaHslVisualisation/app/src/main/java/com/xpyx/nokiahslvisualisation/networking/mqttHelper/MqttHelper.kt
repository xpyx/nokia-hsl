package com.xpyx.nokiahslvisualisation.networking.mqttHelper

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.xpyx.nokiahslvisualisation.fragments.map.MapFragment
import com.xpyx.nokiahslvisualisation.fragments.analytics.AnalyticsFragment
import com.xpyx.nokiahslvisualisation.model.mqtt.VehiclePosition
import com.xpyx.nokiahslvisualisation.utils.Constants.Companion.HSL_CLIENT_USER_NAME
import com.xpyx.nokiahslvisualisation.utils.Constants.Companion.HSL_MQTT_HOST
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import kotlin.properties.Delegates

class MqttHelper {

    private lateinit var mqttAndroidClient: MqttAndroidClient
    private var connectionStatus by Delegates.notNull<Boolean>()
    private var time: Long = 0

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
                    // subscribe(topic)
                    Log.d("DBG", "MQTT connect done")
                    connectionStatus = true
                    // Give your callback on connection established here
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    connectionStatus = false
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

        if (connectionStatus) {

            val qos = 2 // Mention your qos value
            try {
                mqttAndroidClient.subscribe(topic, qos, null, object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken) {
                        // Give your callback on Subscription here
                        Log.d("DBG", "subscription to topic: $topic OK")

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
        } else {
            Log.d("DBG", "Subscribing not working because no connection")
        }

    }

    fun receiveMessages(analyticsFragment: AnalyticsFragment) {

        val gson = Gson()

        mqttAndroidClient.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable) {
                connectionStatus = false
                // Give your callback on failure here
                analyticsFragment.showToast(
                    "MQTT connection lost, restart current view by navigating to Alerts and then back to Vechiles")

            }

            override fun messageArrived(topic: String, message: MqttMessage) {

                try {
                    val data = String(message.payload, charset("UTF-8"))
                    val vehiclePosition = gson.fromJson(data, VehiclePosition::class.java)
                    //Log.d("DBG", "vehiclePosition: ${vehiclePosition.VP}")

                    // Set time
                    time = if (vehiclePosition.VP.toString().contains("oper=40") || vehiclePosition.VP.toString().contains("oper=50")) {
                        2000
                    } else {
                        15000
                    }
                    // Here I update the fragment that shows the data
                    analyticsFragment.updateUI(vehiclePosition, time)

                } catch (e: Exception) {
                    // Give your callback on error here
                    Log.d("DBG", "MQTT exception: $e")


                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
                // Acknowledgement on delivery complete
            }
        })
    }

    fun receiveMessagesInARMap(mapFragment: MapFragment) {

        val gson = Gson()

        mqttAndroidClient.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable) {
                connectionStatus = false
                // Give your callback on failure here
                Log.d("DBG", "MQTT connection lost")

            }

            override fun messageArrived(topic: String, message: MqttMessage) {

                try {
                    val data = String(message.payload, charset("UTF-8"))
                    val vehiclePosition = gson.fromJson(data, VehiclePosition::class.java)

                    // Set time
                    time = if (vehiclePosition.VP.toString().contains("oper=40") || vehiclePosition.VP.toString().contains("oper=50")) {
                        2000
                    } else {
                        15000
                    }

                    // Here I update the fragment that shows the data
                    mapFragment.updateUI(vehiclePosition, time)
                    //Log.d("DBG", "Vehi posi: $vehiclePosition")

                } catch (e: Exception) {
                    // Give your callback on error here
                    Log.d("DBG", "MQTT exception: $e")

                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
                // Acknowledgement on delivery complete
            }
        })
    }


    fun unSubscribe(topic: String) {

        if (connectionStatus) {
            try {
                val unsubToken = mqttAndroidClient.unsubscribe(topic)
                unsubToken.actionCallback = object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken) {
                        // Give your callback on unsubscribing here
                        Log.d("DBG", "Successfully unsubscribed from topic: $topic")
                    }

                    override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                        // Give your callback on failure here
                    }
                }
            } catch (e: MqttException) {
                // Give your callback on failure here
            }
        } else {
            Log.d("DBG", "Subscribing not working because no connection")
        }
    }

    fun destroy() {
        mqttAndroidClient.unregisterResources()
        mqttAndroidClient.disconnect()
    }
}
