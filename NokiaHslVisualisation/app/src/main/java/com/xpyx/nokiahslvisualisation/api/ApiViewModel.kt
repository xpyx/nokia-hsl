package com.xpyx.nokiahslvisualisation.api

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xpyx.nokiahslvisualisation.model.traffic.TrafficData
import com.xpyx.nokiahslvisualisation.repository.ApiRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class ApiViewModel(private val apiRepository: ApiRepository): ViewModel() {

    val myTrafficApiResponse: MutableLiveData<Response<TrafficData>> = MutableLiveData()

    fun getTrafficData(apiKey: String) {
        viewModelScope.launch {
            val response = apiRepository.getTrafficData(apiKey)
            myTrafficApiResponse.value = response
        }
    }
}