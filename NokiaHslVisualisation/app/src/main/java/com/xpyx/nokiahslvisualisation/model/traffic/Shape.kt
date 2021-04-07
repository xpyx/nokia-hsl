package com.xpyx.nokiahslvisualisation.model.traffic

import com.google.gson.annotations.SerializedName

data class Shape(
    val value: String? = null,
    @SerializedName("FC") val fc: Long? = null,
    @SerializedName("LID") val lid: String? = null,
    @SerializedName("LE") val le: Double? = null,
    @SerializedName("FW") val fw: String? = null
)
