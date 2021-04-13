package com.xpyx.nokiahslvisualisation.repository

import android.content.Context
import com.xpyx.nokiahslvisualisation.networking.mqttHelper.MqttHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MQTTRepository {

    companion object {
        private var mqtt = MqttHelper()
        private var topic: String = "/hfp/v2/journey/ongoing/vp/+/+/+/+/+/+/+/+/0/#"
    }

    // Get HSL Vehicle positions with MQTT
    suspend fun getMQTTData(applicationContext: Context) {
        val job = GlobalScope.launch(Dispatchers.IO) {
            mqtt.connect(applicationContext)
        }
        job.join()
        
    }
}
