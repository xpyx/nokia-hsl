package com.xpyx.nokiahslvisualisation.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xpyx.nokiahslvisualisation.model.traffic.*

@Entity(tableName = "trafficitems")
data class TrafficItem(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val hereId: Int?,
    val traffic_item_id: Int,
    val traffic_item_status_short_desc: String,
    val traffic_item_type_desc: String,
    val start_time: String,
    val end_time: String,
    val criticality: CRITICALITY,
    val verified: Boolean,
    val rds_tmc_locations: RDS_TMC_LOCATIONS,
    val location: LOCATION,
    val traffic_item_detail: TRAFFIC_ITEM_DETAIL,
    val traffic_item_description: TRAFFIC_ITEM_DESCRIPTION

)