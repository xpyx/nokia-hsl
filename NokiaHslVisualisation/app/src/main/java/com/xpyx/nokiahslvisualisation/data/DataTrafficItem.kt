package com.xpyx.nokiahslvisualisation.data

import androidx.room.*
import com.xpyx.nokiahslvisualisation.model.traffic.*
import com.xpyx.nokiahslvisualisation.utils.Converters

@TypeConverters(Converters::class)
@Entity(tableName = "trafficitems")
data class DataTrafficItem (
    @PrimaryKey(autoGenerate = true) val idRoomDatabase: Int,
    val traffic_item_id: Long?,
    val traffic_item_status_short_desc: String?,
    val traffic_item_type_desc: String?,
    val start_time: String?,
    val end_time: String?,
    @Embedded val criticality: Ity?,
    val verified: Boolean?,
    @Embedded val rds_tmcLocations: RDSTmcLocations?,
    //@Embedded val location: Location?,
    //@Embedded val traffic_item_detail: TrafficItemDetail?,
    val trafficItemDescriptionElement: List<TrafficItemDescriptionElement>?
) /*{
    constructor(idRoomDatabase: Int, traffic_item_id: Long?, traffic_item_status_short_desc: String?, traffic_item_type_desc: String?, start_time: String?, end_time: String?, criticality: CRITICALITY?, verified: Boolean, traffic_item_description: List<TRAFFIC_ITEM_DESCRIPTION?>) : this(idRoomDatabase, traffic_item_id, traffic_item_status_short_desc, traffic_item_type_desc, start_time, end_time, criticality, verified, traffic_item_description)
}*/
