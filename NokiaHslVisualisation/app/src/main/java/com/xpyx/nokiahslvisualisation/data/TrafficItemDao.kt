package com.xpyx.nokiahslvisualisation.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TrafficItemDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addTrafficItem(traffic: TrafficItem)

    @Query("SELECT * FROM trafficitems ORDER BY id DESC")
    fun readAllData(): LiveData<List<TrafficItem>>

}