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

    @Query("SELECT * FROM stoptimesitems ORDER BY id DESC")
    fun readAllData(): LiveData<List<StopTimesItem>>

    @Query("SELECT EXISTS (SELECT 1 FROM stoptimesitems WHERE id = :id)")
    suspend fun productExists(id: Int): Boolean

}