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


data class TrafficData (

	@SerializedName("TIMESTAMP") val trafficDataTimestamp : String? = null,
	@SerializedName("VERSION") val trafficDataVersion : Double? = null,
	@SerializedName("TRAFFIC_ITEMS") val trafficDataTrafficItems : TrafficItems? = null,
	@SerializedName("diagnostic") val trafficDataDiagnostic : Diagnostic? = null,
	@SerializedName("TIMESTAMP2") val trafficDataTimestamp2 : String? = null,
	@SerializedName("EXTENDED_COUNTRY_CODE") val trafficDataExtendedCountryCode : String? = null
)