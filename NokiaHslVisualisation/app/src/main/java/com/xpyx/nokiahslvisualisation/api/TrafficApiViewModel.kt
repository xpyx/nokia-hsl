package com.xpyx.nokiahslvisualisation.api

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xpyx.nokiahslvisualisation.model.traffic.TrafficData
import com.xpyx.nokiahslvisualisation.repository.TrafficRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class TrafficApiViewModel(private val trafficRepository: TrafficRepository): ViewModel() {

    val myTrafficApiResponse: MutableLiveData<Response<TrafficData>> = MutableLiveData()

    fun getTrafficData(apiKey: String) {
        viewModelScope.launch {
            val response = trafficRepository.getTrafficData(apiKey)
            myTrafficApiResponse.value = response
        }
    }
}