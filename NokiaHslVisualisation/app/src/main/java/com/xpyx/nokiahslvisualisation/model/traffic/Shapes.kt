package com.xpyx.nokiahslvisualisation.model.traffic

import com.google.gson.annotations.SerializedName

data class Shapes(
    @SerializedName("SHP") val shape: List<Shape>? = null
)
