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

data class IntersectionLocation(
    @SerializedName("ID") val intersectionLocationId: String? = null,
    @SerializedName("STREET1") val street1: Street? = null,
    @SerializedName("STREET2") val street2: Street? = null,
    @SerializedName("COUNTY") val county: String? = null,
    @SerializedName("STATE") val state: String? = null,
    @SerializedName("PROXIMITY") val intersectionLocationProximity: Ity? = null
)
