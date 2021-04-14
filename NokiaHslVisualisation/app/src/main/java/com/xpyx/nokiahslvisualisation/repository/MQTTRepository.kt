package com.xpyx.nokiahslvisualisation.repository

import android.content.Context
import com.xpyx.nokiahslvisualisation.networking.mqttHelper.MqttHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MQTTRepository {

    var mqtt = MqttHelper()

    // Get HSL Vehicle positions with MQTT
    suspend fun startMQTT(applicationContext: Context) {
        val job = GlobalScope.launch(Dispatchers.IO) {
            mqtt.connect(applicationContext)
        }
        job.join()
    }
}
