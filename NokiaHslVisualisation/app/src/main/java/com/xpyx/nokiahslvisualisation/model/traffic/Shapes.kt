package com.xpyx.nokiahslvisualisation.model.traffic

import com.google.gson.annotations.SerializedName

data class Shapes(
    @SerializedName("SHP") val shp: List<Shape>? = null
)
