package com.xpyx.nokiahslvisualisation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xpyx.nokiahslvisualisation.api.ApiViewModel
import com.xpyx.nokiahslvisualisation.repository.ApiRepository

class ApiViewModelFactory(private val apiRepository: ApiRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ApiViewModel(apiRepository) as T
    }
}