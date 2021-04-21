package com.xpyx.nokiahslvisualisation.data

import androidx.lifecycle.LiveData

class StopTimesItemRepository(private val stopTimesDao: StopTimesDao) {

    val readAllData: LiveData<List<StopTimesItem>> = stopTimesDao.readAllData()

    suspend fun addStopTimesItem(stopTimesItem: StopTimesItem) {
        stopTimesDao.addStopTimesItem(stopTimesItem)
    }

    suspend fun getStopTimesItem(id: Long): StopTimesItem {
        return stopTimesDao.getStopTimesItem(id)
    }

    suspend fun checkIfExists(itemId: Int): Boolean {
        return stopTimesDao.productExists(itemId)
    }

}