package com.xpyx.nokiahslvisualisation.model.traffic

import com.google.gson.annotations.SerializedName

data class MetroArea (
    val value: String? = null,
    @SerializedName("ID") val id: Long? = null
)
