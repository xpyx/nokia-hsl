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

data class TrafficItemDescriptionElement (

	@SerializedName("value") val trafficItemDescriptionElementValue : String? = null,
	@SerializedName("TYPE") val trafficItemDescriptionElementType : String? = null
)