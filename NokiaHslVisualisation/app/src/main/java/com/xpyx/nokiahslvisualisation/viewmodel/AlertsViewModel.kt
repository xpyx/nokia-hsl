package com.xpyx.nokiahslvisualisation.viewmodel

import com.xpyx.nokiahslvisualisation.repository.AlertRepository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.xpyx.nokiahslvisualisation.GetAlertsQuery
import com.xpyx.nokiahslvisualisation.view.ViewState

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val repository: AlertRepository,
) : ViewModel() {

    private val _alertsList by lazy {
        MutableLiveData<ViewState<Response<GetAlertsQuery.Data>>>()
    }

    val alertsList:
            LiveData<ViewState<Response<GetAlertsQuery.Data>>>
        get() = _alertsList

    fun queryAlertsList() = viewModelScope.launch {
        _alertsList.postValue(ViewState.Loading())
        try {
            val response = repository.queryAlertList()
            _alertsList.postValue(ViewState.Success(response))
        } catch (e: ApolloException) {
            Log.d("ApolloException", "Failure", e)
            _alertsList.postValue(ViewState.Error("Error fetching alerts"))
        }
    }
}