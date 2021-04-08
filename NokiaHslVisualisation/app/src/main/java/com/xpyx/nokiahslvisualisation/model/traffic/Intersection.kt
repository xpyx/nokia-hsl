package com.xpyx.nokiahslvisualisation.model.traffic

import com.google.gson.annotations.SerializedName

data class Intersection(
    @SerializedName("ORIGIN") val intersectionOrigin: IntersectionLocation? = null,
    @SerializedName("TO") val intersectionTo: IntersectionLocation? = null
)
