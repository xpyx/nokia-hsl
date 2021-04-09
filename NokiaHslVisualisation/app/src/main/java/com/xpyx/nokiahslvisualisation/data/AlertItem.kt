package com.xpyx.nokiahslvisualisation.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alertitems")
data class AlertItem (
    @PrimaryKey(autoGenerate = true) val id: Int,
    val alertHeaderText: String?,
    val alertDescriptionText: String?,
    val effectiveStartDate: String?,
    val effectiveEndDate: String?,
    val alertUrl: String?
)