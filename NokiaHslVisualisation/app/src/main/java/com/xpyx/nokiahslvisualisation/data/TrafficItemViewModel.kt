package com.xpyx.nokiahslvisualisation.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TrafficItemViewModel(application: Application) : AndroidViewModel(application) {

    val readAllData: LiveData<List<TrafficItem>>
    private val repository: TrafficItemRepository

    init {
        val trafficItemDao = TrafficItemDatabase.getDatabase(application).trafficDao()
        repository = TrafficItemRepository(trafficItemDao)
        readAllData = repository.readAllData
    }

    fun addTrafficData(trafficItem: TrafficItem){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTrafficItem(trafficItem)
        }
    }

    suspend fun getTrafficItem(id: Long): TrafficItem {
        lateinit var trafficItem: TrafficItem
        val job = viewModelScope.launch(Dispatchers.IO) {
            trafficItem = repository.getTrafficItem(id)
        }
        job.join()
        return trafficItem
    }

}