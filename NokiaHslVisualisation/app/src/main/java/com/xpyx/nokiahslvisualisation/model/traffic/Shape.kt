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

data class Shape(
    @SerializedName("value")val shapeValue: String? = null,
    @SerializedName("FC") val fc: Long? = null,
    @SerializedName("LID") val lid: String? = null,
    @SerializedName("LE") val le: Double? = null,
    @SerializedName("FW") val fw: String? = null
)
