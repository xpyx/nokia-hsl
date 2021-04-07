package com.xpyx.nokiahslvisualisation.model.traffic

import com.google.gson.annotations.SerializedName

data class Intersection(
    @SerializedName("ORIGIN") val origin: IntersectionLocation? = null,
    @SerializedName("TO") val to: IntersectionLocation? = null
)
