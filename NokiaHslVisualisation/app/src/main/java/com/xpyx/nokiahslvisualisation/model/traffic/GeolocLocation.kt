package com.xpyx.nokiahslvisualisation.model.traffic

import com.google.gson.annotations.SerializedName

data class GeolocLocation(
    @SerializedName("LATITUDE") val geolocLocationLatitude: Double? = null,
    @SerializedName("LONGITUDE") val geolocLocationLongitude: Double? = null
)
