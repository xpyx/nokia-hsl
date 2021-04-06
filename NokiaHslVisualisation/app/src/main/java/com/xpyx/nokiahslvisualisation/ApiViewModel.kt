package com.xpyx.nokiahslvisualisation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.api.Response
import androidx.lifecycle.viewModelScope
import com.xpyx.nokiahslvisualisation.repository.ApiRepository
import kotlinx.coroutines.launch

class ApiViewModel(private val apiRepository: ApiRepository): ViewModel() {

    private val alertResponse: MutableLiveData<Response<AlertsListQuery.Data>> = MutableLiveData()

//    fun getAlerts() {
//        viewModelScope.launch {
//            val response: Response<AlertsListQuery.Data> = apiRepository.getAlerts()
//            alertResponse.value = response
//        }
//    }

}
