/**
 * Description: The class responsible for creating the instance of an AlertViewModel
 *
 * Course: Mobiiliprojekti TX00CK67-3008
 * Name: Ville Pystynen
 */

package com.xpyx.nokiahslvisualisation.api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xpyx.nokiahslvisualisation.repository.AlertRepository

class AlertViewModelFactory(private val alertRepository: AlertRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AlertViewModel(alertRepository) as T
    }
}