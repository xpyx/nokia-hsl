package com.xpyx.nokiahslvisualisation.data

import androidx.lifecycle.LiveData

class TrafficItemRepository(private val trafficItemDao: TrafficItemDao) {
    val readAllData: LiveData<List<DataTrafficItem>> = trafficItemDao.readAllData()

    suspend fun addTrafficItem(trafficItem: DataTrafficItem) {
        trafficItemDao.addTrafficItem(trafficItem)
    }

    suspend fun getTrafficItem(id: Long): DataTrafficItem {
        return trafficItemDao.getTrafficItem(id)
    }

}