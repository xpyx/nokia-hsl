package com.xpyx.nokiahslvisualisation.model.traffic

import com.google.gson.annotations.SerializedName

data class MetroArea (
    @SerializedName("value") val metroareaValue: String? = null,
    @SerializedName("ID") val metroAreaId: Long? = null
)
