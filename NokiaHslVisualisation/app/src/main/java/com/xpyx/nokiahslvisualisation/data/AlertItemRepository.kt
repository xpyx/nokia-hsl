/**
 * Description: LiveData and DAO connections for alerts
 *
 * Course: Mobiiliprojekti TX00CK67-3008
 * Name: Ville Pystynen
 */

package com.xpyx.nokiahslvisualisation.data

import androidx.lifecycle.LiveData

class AlertItemRepository(private val alertItemDao: AlertItemDao) {

    val readAllData: LiveData<List<AlertItem>> = alertItemDao.readAllData()

    suspend fun addAlertItem(alertItem: AlertItem) {
        alertItemDao.addAlertItem(alertItem)
    }

    suspend fun checkIfExists(alertId: String): Boolean {
        return alertItemDao.productExists(alertId)
    }

}