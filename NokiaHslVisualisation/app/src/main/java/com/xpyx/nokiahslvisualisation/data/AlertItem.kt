/**
 * Description:
 *
 * Course: Mobiiliprojekti TX00CK67-3008
 * Name: Ville Pystynen
 */

package com.xpyx.nokiahslvisualisation.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alertitems")
data class AlertItem(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val alertId: String?,
    val alertHeaderText: String?,
    val alertDescriptionText: String?,
    val effectiveStartDate: String?,
    val effectiveEndDate: String?,
    val alertUrl: String?,
    val alertSeverityLevel: String?,
    val alertCause: String?,
    val alertEffect: String?
)