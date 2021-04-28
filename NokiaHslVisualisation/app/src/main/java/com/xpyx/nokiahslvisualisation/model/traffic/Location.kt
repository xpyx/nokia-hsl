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

data class Location (
    @SerializedName("DEFINED") val locationDefined : Defined? = null,
    @SerializedName("INTERSECTION") val locationIntersection: Intersection? = null,
    @SerializedName("GEOLOC") val locationGeoloc : Geoloc? = null,
    @SerializedName("TPEGOpenLRBase64") val tPegOpenLrBase64 : String? = null,
    @SerializedName("NAVTECH") val navTech : NavTech? = null,
    @SerializedName("LENGTH") val locationLength : Double? = null,
    @SerializedName("POLITICAL_BOUNDARY") val locationPoliticalBoundary: PoliticalBoundary? = null
)