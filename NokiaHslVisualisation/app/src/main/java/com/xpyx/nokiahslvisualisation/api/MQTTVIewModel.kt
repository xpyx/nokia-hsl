package com.xpyx.nokiahslvisualisation.api

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xpyx.nokiahslvisualisation.networking.mqttHelper.MqttHelper
import com.xpyx.nokiahslvisualisation.repository.MQTTRepository
import kotlinx.coroutines.launch

class MQTTViewModel(private val mqttRepository: MQTTRepository): ViewModel() {

    val myMQTTResponse: MutableLiveData<String> = MutableLiveData()

    fun getMQTTData(viewContext: Context) {
        viewModelScope.launch {
            mqttRepository.startMQTT(viewContext)
            mqttRepository.mqtt.receiveMessages()
        }
    }
}