/**
 * Description: ViewModel setup for alert items
 *
 * Course: Mobiiliprojekti TX00CK67-3008
 * Name: Ville Pystynen
 */

package com.xpyx.nokiahslvisualisation.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlertItemViewModel(application: Application) : AndroidViewModel(application) {

    val readAllData: LiveData<List<AlertItem>>
    private val repository: AlertItemRepository

    init {
        val alertItemDao = AlertItemDatabase.getDatabase(application).alertDao()
        repository = AlertItemRepository(alertItemDao)
        readAllData = repository.readAllData
    }

    fun addAlertItem(alertItem: AlertItem){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addAlertItem(alertItem)
        }
    }

    // Check if exists by alertId
    suspend fun checkIfExists(alertId: String): Boolean {
        var exists = false
        val job = viewModelScope.launch(Dispatchers.IO) {
            exists = repository.checkIfExists(alertId)
        }
        job.join()
        return exists
    }

}