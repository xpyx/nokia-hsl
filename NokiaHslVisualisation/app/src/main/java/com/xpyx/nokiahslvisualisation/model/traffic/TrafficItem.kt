/**
 * Description: Data Class for Here traffic model
 *
 * Basics done with http://www.json2kotlin.com but altered a lot
 *
 * Course: Mobiiliprojekti TX00CK67-3008
 * Name: Matias Hätönen
 */

package com.xpyx.nokiahslvisualisation.model.traffic

import com.google.gson.annotations.SerializedName

data class TrafficItem (
        @SerializedName("TRAFFIC_ITEM_ID") val trafficItemId : Long? = null,
        @SerializedName("ORIGINAL_TRAFFIC_ITEM_ID") val originalTrafficItemId : Double? = null,
        @SerializedName("TRAFFIC_ITEM_STATUS_SHORT_DESC") val trafficItemStatusShortDesc : String? = null,
        @SerializedName("TRAFFIC_ITEM_TYPE_DESC") val trafficItemTypeDesc : String? = null,
        @SerializedName("START_TIME") val trafficItemStartTime : String? = null,
        @SerializedName("END_TIME") val trafficItemEndTime : String? = null,
        @SerializedName("ENTRY_TIME") val trafficItemEntryTime : String? = null,
        @SerializedName("CRITICALITY") val trafficItemCriticality : Ity? = null,
        @SerializedName("VERIFIED") val trafficItemVerified : Boolean? = null,
        @SerializedName("ABBREVIATION") val trafficItemAbbreviation : Abbreviation? = null,
        @SerializedName("RDS-TMC_LOCATIONS") val trafficitemRDSTmclocations : RDSTmcLocations? = null,
        @SerializedName("LOCATION") val trafficItemLocation : Location? = null,
        @SerializedName("TRAFFIC_ITEM_DETAIL") val trafficItemDetail : TrafficItemDetail? = null,
        @SerializedName("TRAFFIC_ITEM_DESCRIPTION") val trafficItemDescriptionElement : List<TrafficItemDescriptionElement>? = null,
        @SerializedName("mid") val trafficItemMid : String? = null,
        @SerializedName("PRODUCT") val trafficItemProduct : String? = null
)