/**
 * Description: The class responsible for creating the instance of an MQTTViewModel
 *
 * Course: Mobiiliprojekti TX00CK67-3008
 * Name: Ville Pystynen
 */

package com.xpyx.nokiahslvisualisation.api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xpyx.nokiahslvisualisation.repository.MQTTRepository

class MQTTViewModelFactory(private val mqttRepository: MQTTRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MQTTViewModel(mqttRepository) as T
    }
}