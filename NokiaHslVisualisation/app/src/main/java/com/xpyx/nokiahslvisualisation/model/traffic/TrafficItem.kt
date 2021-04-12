/*
Copyright (c) 2021 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */

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