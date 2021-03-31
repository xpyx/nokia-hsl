package com.xpyx.nokiahslvisualisation.model.traffic

import com.google.gson.annotations.SerializedName

data class Intersection(
    @SerializedName("ORIGIN") val origin: IntersectionOrigin? = null,
    @SerializedName("TO") val to: IntersectionOrigin? = null
)
