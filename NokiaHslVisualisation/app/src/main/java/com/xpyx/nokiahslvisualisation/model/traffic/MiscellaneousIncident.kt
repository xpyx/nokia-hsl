package com.xpyx.nokiahslvisualisation.model.traffic

import com.google.gson.annotations.SerializedName

data class MiscellaneousIncident (
    @SerializedName("MISCELLANEOUS_TYPE_DESC") val miscellaneousTypeDesc: String? = null
)
