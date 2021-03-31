package com.xpyx.nokiahslvisualisation.model.traffic

import com.google.gson.annotations.SerializedName

data class GeolocOrigin(
    @SerializedName("LATITUDE") val latitude: Double? = null,
    @SerializedName("LONGITUDE") val longitude: Double? = null
)
