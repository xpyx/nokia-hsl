/**
 * Description: Livedata and DAO connections
 *
 * Course: Mobiiliprojekti TX00CK67-3008
 * Name: Matias Hätönen
 */

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
    suspend fun checkIfExists(item_id: Long): Boolean {
        return trafficItemDao.productExists(item_id)
    }

    fun removeProductIfNotExists(item_id: Long){}



}