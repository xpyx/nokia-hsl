/**
 * Description: Returns Here traffic API ViewModels
 *
 * Course: Mobiiliprojekti TX00CK67-3008
 * Name: Matias Hätönen
 */

package com.xpyx.nokiahslvisualisation.api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xpyx.nokiahslvisualisation.repository.TrafficRepository

class TrafficApiViewModelFactory(private val trafficRepository: TrafficRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TrafficApiViewModel(trafficRepository) as T
    }
}