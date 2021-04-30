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

data class DefinedLocation (
    @SerializedName("ROADWAY")
    val definedLocationRoadway: DirectionClass? = null,

    @SerializedName("POINT")
    val definedLocationPoint: DirectionClass? = null,

    @SerializedName("DIRECTION")
    val definedLocationDirection: DirectionClass? = null,

    @SerializedName("PROXIMITY")
    val definedLocationProximity: Ity? = null
)
