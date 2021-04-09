package com.xpyx.nokiahslvisualisation.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AlertItemDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addAlertItem(alert: AlertItem)

    @Query("SELECT * FROM alertitems ORDER BY id DESC")
    fun readAllData(): LiveData<List<AlertItem>>

    @Query("SELECT EXISTS (SELECT 1 FROM alertitems WHERE alertHeaderText = :text)")
    suspend fun productExists(text: String): Boolean

}