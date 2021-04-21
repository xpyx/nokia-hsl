package com.xpyx.nokiahslvisualisation.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StopTimesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStopTimesItem(stopTimesItem: StopTimesItem)

    @Query("SELECT * FROM stoptimesitems ORDER BY itemId DESC")
    fun readAllData(): LiveData<List<StopTimesItem>>

    @Query("SELECT * FROM stoptimesitems WHERE itemId = :id")
    suspend fun getStopTimesItem(id: Long): StopTimesItem

    @Query("SELECT EXISTS (SELECT 1 FROM stoptimesitems WHERE itemId = :itemId)")
    suspend fun productExists(itemId: Int): Boolean

}