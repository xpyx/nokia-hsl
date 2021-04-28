/**
 * Description: DAO for making queries into TrafficItemDatabase
 *
 * Course: Mobiiliprojekti TX00CK67-3008
 * Name: Matias Hätönen
 */

package com.xpyx.nokiahslvisualisation.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TrafficItemDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addTrafficItem(traffic: DataTrafficItem)

    @Query("SELECT * FROM trafficitems ORDER BY idRoomDatabase DESC")
    fun readAllData(): LiveData<List<DataTrafficItem>>

    @Query("SELECT * FROM trafficitems WHERE traffic_item_id = :id")
    suspend fun getTrafficItem(id: Long): DataTrafficItem

    @Query("SELECT EXISTS (SELECT 1 FROM trafficitems WHERE traffic_item_id = :item_id)")
    suspend fun productExists(item_id: Long): Boolean

    @Query("DELETE FROM trafficitems WHERE traffic_item_id = :item_id")
    suspend fun removeProduct(item_id: Long)
}