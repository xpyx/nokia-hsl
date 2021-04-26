package com.xpyx.nokiahslvisualisation.repository

import android.content.Context
import com.xpyx.nokiahslvisualisation.networking.mqttHelper.MqttHelper
import com.xpyx.nokiahslvisualisation.networking.mqttHelper.TopicSetter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MQTTRepository {

    var mqtt = MqttHelper()

    // Connect
    suspend fun startMQTT(applicationContext: Context) {
        val job = GlobalScope.launch(Dispatchers.IO) {
            mqtt.connect(applicationContext)
        }
        job.join()
    }

    // Subscribe
    suspend fun subscribe(topicString: String) {
        val job = GlobalScope.launch(Dispatchers.IO) {
            mqtt.subscribe(topicString)
        }
        job.join()
    }


}
