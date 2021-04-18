package com.xpyx.nokiahslvisualisation.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xpyx.nokiahslvisualisation.StopTimesListQuery

@Entity(tableName = "stoptimesitems")
data class StopTimesItem (
    @PrimaryKey(autoGenerate = true) val id: Int,
    @Embedded val stops: StopTimesListQuery.Stop
)
