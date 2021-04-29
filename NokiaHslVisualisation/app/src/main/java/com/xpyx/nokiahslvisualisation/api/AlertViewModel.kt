/**
 * Description:
 *
 * Course: Mobiiliprojekti TX00CK67-3008
 * Name: Ville Pystynen
 */

package com.xpyx.nokiahslvisualisation.api

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.Response
import com.xpyx.nokiahslvisualisation.AlertsListQuery
import com.xpyx.nokiahslvisualisation.repository.AlertRepository
import kotlinx.coroutines.launch

class AlertViewModel(private val alertRepository: AlertRepository): ViewModel() {

    val myAlertApiResponse: MutableLiveData<Response<AlertsListQuery.Data>> = MutableLiveData()

    fun getAlertData() {
        viewModelScope.launch {
            val response = alertRepository.getAlertData()
            myAlertApiResponse.value = response
        }
    }
}