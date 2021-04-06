package com.xpyx.nokiahslvisualisation.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TrafficItemViewModel(application: Application) : AndroidViewModel(application) {

    val readAllData: LiveData<List<DataTrafficItem>>
    private val repository: TrafficItemRepository

    init {
        val trafficItemDao = TrafficItemDatabase.getDatabase(application).trafficDao()
        repository = TrafficItemRepository(trafficItemDao)
        readAllData = repository.readAllData
    }

    fun addTrafficData(trafficItem: DataTrafficItem){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTrafficItem(trafficItem)
        }
    }

    suspend fun getTrafficItem(id: Long): DataTrafficItem {
        lateinit var trafficItem: DataTrafficItem
        val job = viewModelScope.launch(Dispatchers.IO) {
            trafficItem = repository.getTrafficItem(id)
        }
        job.join()
        return trafficItem
    }

}