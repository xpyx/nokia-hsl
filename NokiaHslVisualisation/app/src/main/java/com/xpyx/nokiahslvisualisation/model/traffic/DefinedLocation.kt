package com.xpyx.nokiahslvisualisation.model.traffic

import com.google.gson.annotations.SerializedName

data class DefinedOrigin (
    @SerializedName("ROADWAY")
    val roadway: DirectionClass? = null,

    @SerializedName("POINT")
    val point: DirectionClass? = null,

    @SerializedName("DIRECTION")
    val direction: DirectionClass? = null,

    @SerializedName("PROXIMITY")
    val proximity: Ity? = null
)
