/**
 * Description: DAO for making queries into alert database
 *
 * Course: Mobiiliprojekti TX00CK67-3008
 * Name: Ville Pystynen
 */

package com.xpyx.nokiahslvisualisation.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AlertItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAlertItem(alert: AlertItem)

    @Query("SELECT * FROM alertitems ORDER BY id DESC")
    fun readAllData(): LiveData<List<AlertItem>>

    @Query("SELECT EXISTS (SELECT 1 FROM alertitems WHERE alertId = :alertId)")
    suspend fun productExists(alertId: String): Boolean

}