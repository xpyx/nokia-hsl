package com.xpyx.nokiahslvisualisation.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xpyx.nokiahslvisualisation.model.traffic.*

@Entity(tableName = "trafficitems")
data class TrafficItem (
    @PrimaryKey(autoGenerate = true) val idRoomDatabase: Int,
    val traffic_item_id: Long?,
    val traffic_item_status_short_desc: String?,
    val traffic_item_type_desc: String?,
    val start_time: String?,
    val end_time: String?,
    @Embedded val criticality: CRITICALITY?,
    val verified: Boolean?,
    //@Embedded val rds_tmc_locations: RDS_TMC_LOCATIONS?,
    //@Embedded val location: LOCATION?,
    //@Embedded val traffic_item_detail: TRAFFIC_ITEM_DETAIL?,
    //@Embedded val traffic_item_description: List<TRAFFIC_ITEM_DESCRIPTION>?

)