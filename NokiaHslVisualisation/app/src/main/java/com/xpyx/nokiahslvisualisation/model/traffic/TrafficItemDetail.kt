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

data class TrafficItemDetail (
	@SerializedName("ROAD_CLOSED") val trafficItemDetailRoadClosed: Boolean? = null,
	@SerializedName("EVENT") val trafficItemDetailEvent: Event?,
	@SerializedName("INCIDENT") val trafficItemDetailIncident: Incident?,
	@SerializedName("ALERTC") val trafficItemDetailAlertC: List<AlertCElement>? = null
)