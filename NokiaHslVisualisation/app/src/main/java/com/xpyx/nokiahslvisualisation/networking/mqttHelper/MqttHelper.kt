package com.xpyx.nokiahslvisualisation.networking.mqttHelper

import android.content.Context
import android.util.Log
import com.xpyx.nokiahslvisualisation.utils.Constants.Companion.HSL_CLIENT_USER_NAME
import com.xpyx.nokiahslvisualisation.utils.Constants.Companion.HSL_MQTT_HOST
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MqttHelper {

    private lateinit var mqttAndroidClient: MqttAndroidClient

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
                    Log.i("Connection", "success ")
                    Log.i("Connection", mqttAndroidClient.isConnected.toString())

                    //connectionStatus = true
                    // Give your callback on connection established here
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    //connectionStatus = false
                    Log.i("Connection", "failure")
                    // Give your callback on connection failure here
                    exception.printStackTrace()
                }
            }
        } catch (e: MqttException) {
            // Give your callback on connection failure here
            e.printStackTrace()
        }
    }

    fun unSubscribe(topic: String) {
        try {
            val unsubToken = mqttAndroidClient.unsubscribe(topic)
            unsubToken.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    // Give your callback on unsubscribing here
                    Log.i("Connection", "Unsubscribe success ")

                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    // Give your callback on failure here
                }
            }
        } catch (e: MqttException) {
            // Give your callback on failure here
        }
    }

    fun receiveMessages() {

        mqttAndroidClient.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable) {
                //connectionStatus = false
                // Give your callback on failure here
            }

            override fun messageArrived(topic: String, message: MqttMessage) {

                try {
                    val data = String(message.payload, charset("UTF-8"))
                    // data is the desired received message
                    // Give your callback on message received here
                    Log.d("Connection", data)
//                    vehicleFragment.updateUI(data)

                } catch (e: Exception) {
                    // Give your callback on error here
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
                // Acknowledgement on delivery complete
            }
        })
    }

    fun subscribe(topic: String) {
        val qos = 2 // Mention your qos value
        try {
            mqttAndroidClient.subscribe(topic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    // Give your callback on Subscription here
                    Log.i("Connection", "subscribe success ")
                }

                override fun onFailure(
                    asyncActionToken: IMqttToken,
                    exception: Throwable
                ) {
                    // Give your subscription failure callback here
                    Log.i("Connection", "subscribe failure")

                }
            })
        } catch (e: MqttException) {
            System.err.println("Exception whilst subscribing to topic '$topic'")
            e.printStackTrace()
        }
    }

    fun destroy() {
        mqttAndroidClient.unregisterResources()
        mqttAndroidClient.disconnect()
    }

}