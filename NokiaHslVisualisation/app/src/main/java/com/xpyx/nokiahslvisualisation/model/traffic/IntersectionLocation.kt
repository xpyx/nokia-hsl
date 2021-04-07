package com.xpyx.nokiahslvisualisation.model.traffic

import com.google.gson.annotations.SerializedName

data class IntersectionLocation(
    @SerializedName("ID") val id: String? = null,
    @SerializedName("STREET1") val street1: Street? = null,
    @SerializedName("STREET2") val street2: Street? = null,
    @SerializedName("COUNTY") val county: String? = null,
    @SerializedName("STATE") val state: String? = null,
    @SerializedName("PROXIMITY") val proximity: Ity? = null
)
