package com.xpyx.nokiahslvisualisation.api

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xpyx.nokiahslvisualisation.fragments.map.MapFragment
import com.xpyx.nokiahslvisualisation.fragments.vehicles.VehicleFragment
import com.xpyx.nokiahslvisualisation.model.mqtt.VehiclePosition
import com.xpyx.nokiahslvisualisation.repository.MQTTRepository
import kotlinx.coroutines.launch

class MQTTViewModel(private val mqttRepository: MQTTRepository): ViewModel() {

    var myMQTTResponse: MutableLiveData<VehiclePosition> = MutableLiveData()

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

    fun receiveMessages(vehicleFragment: VehicleFragment) {
        viewModelScope.launch {
            mqttRepository.mqtt.receiveMessages(vehicleFragment)
        }
    }

    fun receiveMessagesInARMAp(mapFragment: MapFragment) {
        viewModelScope.launch {
            mqttRepository.mqtt.receiveMessagesInARMap(mapFragment)
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


