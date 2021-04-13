package com.xpyx.nokiahslvisualisation.repository

import android.content.Context
import com.xpyx.nokiahslvisualisation.networking.mqttHelper.MqttHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MQTTRepository {

    private var topic: String = "/hfp/v2/journey/ongoing/vp/+/+/+/+/+/+/+/+/0/#"

    // Get HSL Vehicle positions with MQTT
    fun getMQTTData(applicationContext: Context) {
        val mqtt = MqttHelper()
        mqtt.connect(applicationContext)
        CoroutineScope(Dispatchers.IO).launch {
            // TODO: Subscribe not working
            // java.lang.NullPointerException: Attempt to invoke virtual method
            // 'void org.eclipse.paho.android.service.MqttService.subscribe(
            // java.lang.String, java.lang.String, int, java.lang.String, java.lang.String
            // )' on a null object reference
            mqtt.subscribe(topic)
//            mqtt.receiveMessages()
        }
    }
}