package com.xpyx.nokiahslvisualisation.model.traffic

import com.google.gson.annotations.SerializedName

data class PoliticalBoundary (
    @SerializedName("METRO_AREA") val metroArea: MetroArea? = null
)
