/**
 * Description:
 *
 * Course: Mobiiliprojekti TX00CK67-3008
 * Name: Ville Pystynen
 */

package com.xpyx.nokiahslvisualisation.api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xpyx.nokiahslvisualisation.repository.StopTimesRepository

class StopTimesViewModelFactory(private val stopTimesRepository: StopTimesRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return StopTimesViewModel(stopTimesRepository) as T
    }
}