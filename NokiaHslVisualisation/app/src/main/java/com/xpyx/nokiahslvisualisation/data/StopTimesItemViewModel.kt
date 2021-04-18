package com.xpyx.nokiahslvisualisation.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StopTimesItemViewModel(application: Application) : AndroidViewModel(application) {

    val readAllData: LiveData<List<StopTimesItem>>
    private val itemRepository: StopTimesItemRepository

    init {
        val stopTimesItemDao = StopTimesDatabase.getDatabase(application).stopTimesDao()
        itemRepository = StopTimesItemRepository(stopTimesItemDao)
        readAllData = itemRepository.readAllData
    }

    fun addStopItem(stopTimesItem: StopTimesItem){
        viewModelScope.launch(Dispatchers.IO) {
            itemRepository.addStopTimesItem(stopTimesItem)
        }
    }

    // Check if exists by alertId
    suspend fun checkIfExists(id: Int): Boolean {
        var exists = false
        val job = viewModelScope.launch(Dispatchers.IO) {
            exists = itemRepository.checkIfExists(id)
        }
        job.join()
        return exists
    }

}