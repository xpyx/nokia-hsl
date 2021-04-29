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

data class RDSTmcAlertC (
	@SerializedName("TRAFFIC_CODE") val rDSTmcAlertCTrafficCode: Long? = null,
	@SerializedName("QUANTIFIERS") val rDSTmcAlertCQuantifiers: Long? = null,
	@SerializedName("DESCRIPTION") val rDSTmcAlertCDescription: String? = null,
	@SerializedName("ALERTC_DURATION") val rDSTmcAlertCAlertcDuration: String? = null,
	@SerializedName("ALERTC_DIRECTION") val rDSTmcAlertCAlertcDirection: Long? = null,
	@SerializedName("UPDATE_CLASS") val rDSTmcAlertCUpdateClass: Long? = null,
	@SerializedName("PHRASE_CODE") val rDSTmcAlertCPhraseCode: String? = null,
	@SerializedName("EXTENT") val rDSTmcAlertCExtent: String? = null,
	@SerializedName("DURATION") val rDSTmcAlertCDuration: Long? = null,
	@SerializedName("ALERTC_Q_BINARY") val rDSTmcAlertCAlertcQBinary: String? = null,
	@SerializedName("URGENCY") val rDSTmcAlertCUrgency: String? = null
)