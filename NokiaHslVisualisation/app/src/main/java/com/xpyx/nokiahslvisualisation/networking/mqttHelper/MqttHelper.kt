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

class MqttHelper {

    private lateinit var mqttAndroidClient: MqttAndroidClient
    private var topic: String = "/hfp/v2/journey/ongoing/vp/+/+/+/+/+/+/+/+/0/#"

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
