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
import com.xpyx.nokiahslvisualisation.StopTimesListQuery
import com.xpyx.nokiahslvisualisation.repository.StopTimesRepository
import kotlinx.coroutines.launch

class StopTimesViewModel(private val stopTimesRepository: StopTimesRepository): ViewModel() {

    val myStopTimesApiResponse: MutableLiveData<Response<StopTimesListQuery.Data>> = MutableLiveData()

    fun getStopTimesData() {
        viewModelScope.launch {
            val response = stopTimesRepository.getStopTimesData()
            myStopTimesApiResponse.value = response
        }
    }
}