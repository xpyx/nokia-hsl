package com.xpyx.nokiahslvisualisation.model.traffic

import com.google.gson.annotations.SerializedName

data class DirectionClass (
    @SerializedName("DESCRIPTION") val description: List<TrafficItemDescriptionElement>? = null,
    @SerializedName("ID") val id: Long? = null
)
