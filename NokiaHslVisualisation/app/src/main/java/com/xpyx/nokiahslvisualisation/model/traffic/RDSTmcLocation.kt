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

data class RDSTmcLocation (
    @SerializedName("EBU_COUNTRY_CODE") val rDSTmcLocationEbuCountryCode: String? = null,
    @SerializedName("TABLE_ID") val rDSTmcLocationTableID: Long? = null,
    @SerializedName("LOCATION_ID") val rDSTmcLocationLocationID: String? = null,
    @SerializedName("LOCATION_DESC") val rDSTmcLocationLocationDesc: String? = null,
    @SerializedName("RDS_DIRECTION") val rDSTmcLocationRdsDirection: String? = null
)
