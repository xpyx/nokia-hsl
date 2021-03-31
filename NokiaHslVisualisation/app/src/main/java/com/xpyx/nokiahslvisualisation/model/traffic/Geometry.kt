package com.xpyx.nokiahslvisualisation.model.traffic

import com.google.gson.annotations.SerializedName

data class Geometry(
    @SerializedName("SHAPES") val shapes: Shapes? = null
)
