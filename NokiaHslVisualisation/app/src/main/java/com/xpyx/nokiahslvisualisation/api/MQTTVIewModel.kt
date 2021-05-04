/**
 * Description: ViewModel for handling MQTT operations
 *
 * Course: Mobiiliprojekti TX00CK67-3008
 * Name: Ville Pystynen
 */

package com.xpyx.nokiahslvisualisation.api

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ar.sceneform.ux.ArFragment
import com.xpyx.nokiahslvisualisation.fragments.map.MapFragment
import com.xpyx.nokiahslvisualisation.fragments.analytics.AnalyticsFragment
import com.xpyx.nokiahslvisualisation.fragments.ar.ARFragment
import com.xpyx.nokiahslvisualisation.model.mqtt.VehiclePosition
import com.xpyx.nokiahslvisualisation.repository.MQTTRepository
import kotlinx.coroutines.launch

class MQTTViewModel(private val mqttRepository: MQTTRepository): ViewModel() {

    fun connectMQTT(viewContext: Context) {
        viewModelScope.launch {
            mqttRepository.startMQTT(viewContext)
        }
    }

    fun subscribe(topicString: String) {
        viewModelScope.launch {
            mqttRepository.subscribe(topicString)
        }
    }

    fun receiveMessagesInARMAp(mapFragment: MapFragment) {
        viewModelScope.launch {
            mqttRepository.mqtt.receiveMessagesInARMap(mapFragment)
        }
    }

    fun receiveMessagesInARBus(arFragment: ARFragment) {
        viewModelScope.launch {
            mqttRepository.mqtt.receiveMessagesInARBus(arFragment)
        }
    }

    fun unsubscribe(topic: String) {
        viewModelScope.launch {
            mqttRepository.mqtt.unSubscribe(topic)
        }
    }

    fun destroy() {
        viewModelScope.launch {
            mqttRepository.mqtt.destroy()
        }
    }

}


