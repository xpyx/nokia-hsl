package com.xpyx.nokiahslvisualisation.data

import androidx.lifecycle.LiveData

class StopTimesRepository(private val stopTimesDao: StopTimesDao) {

    val readAllData: LiveData<List<StopTimesItem>> = stopTimesDao.readAllData()

    suspend fun addStopTimesItem(stopTimesItem: StopTimesItem) {
        stopTimesDao.addStopTimesItem(stopTimesItem)
    }

    suspend fun checkIfExists(id: Int): Boolean {
        return stopTimesDao.productExists(id)
    }

}