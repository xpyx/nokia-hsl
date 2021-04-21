package com.xpyx.nokiahslvisualisation.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import com.xpyx.nokiahslvisualisation.StopTimesListQuery
import com.xpyx.nokiahslvisualisation.utils.Converters

@Entity(tableName = "stoptimesitems")
@TypeConverters(Converters::class)
data class StopTimesItem (
    @PrimaryKey(autoGenerate = true) val itemId: Int,
    @Embedded val stops: StopTimesListQuery.Stop
//    @SerializedName("__typename") val typeName: String?,
//    @Embedded val stoptimesWithoutPatterns: StopTimesListQuery.StoptimesWithoutPattern
)
