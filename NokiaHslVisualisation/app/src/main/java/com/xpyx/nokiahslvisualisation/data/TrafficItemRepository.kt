package com.xpyx.nokiahslvisualisation.data

import androidx.lifecycle.LiveData

class TrafficItemRepository(private val trafficItemDao: TrafficItemDao) {
    val readAllData: LiveData<List<TrafficItem>> = trafficItemDao.readAllData()

    suspend fun addTrafficItem(trafficItem: TrafficItem) {
        trafficItemDao.addTrafficItem(trafficItem)
    }

    suspend fun getTrafficItem(id: Int): TrafficItem {
        return trafficItemDao.getTrafficItem(id)
    }

}