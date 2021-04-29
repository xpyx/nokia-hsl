/**
 * Description: Room Database entity for Traffic item
 *
 * Course: Mobiiliprojekti TX00CK67-3008
 * Name: Matias Hätönen
 */

package com.xpyx.nokiahslvisualisation.data

import androidx.room.*
import com.xpyx.nokiahslvisualisation.model.traffic.*
import com.xpyx.nokiahslvisualisation.utils.Converters

@Entity(tableName = "trafficitems")
@TypeConverters(Converters::class)
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
        @Embedded val location: Location?,
        @Embedded val traffic_item_detail: TrafficItemDetail?,
        val trafficItemDescriptionElement: List<TrafficItemDescriptionElement>?
)
