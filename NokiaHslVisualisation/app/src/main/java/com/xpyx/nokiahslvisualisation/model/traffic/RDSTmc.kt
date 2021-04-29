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

data class RDSTmc (
	@SerializedName("ORIGIN") val rDSTmcOrigin: RDSTmcLocation? = null,
	@SerializedName("TO") val rDSTmcTo: RDSTmcLocation? = null,
	@SerializedName("DIRECTION") val rDSTmcDirection: String? = null,
	@SerializedName("ALERTC") val rDSTmcAlertc: RDSTmcAlertC? = null,
	@SerializedName("LENGTH") val rDSTmcLength: Double? = null,
	@SerializedName("PRIMARY_OFFSET") val rDSTmcPrimaryOffset: Double? = null
)