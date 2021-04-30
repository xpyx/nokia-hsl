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

data class Geoloc (
	@SerializedName("ORIGIN") val geolocOrigin : GeolocLocation? = null,
	@SerializedName("TO") val geolocTo : List<GeolocLocation>? = null,
	@SerializedName("GEOMETRY") val geometry: Geometry? = null
)