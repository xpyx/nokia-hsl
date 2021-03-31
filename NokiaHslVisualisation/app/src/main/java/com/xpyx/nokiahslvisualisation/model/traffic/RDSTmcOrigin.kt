package com.xpyx.nokiahslvisualisation.model.traffic

import com.google.gson.annotations.SerializedName

data class RDSTmcOrigin (
    @SerializedName("EBU_COUNTRY_CODE") val ebuCountryCode: String? = null,
    @SerializedName("TABLE_ID") val tableID: Long? = null,
    @SerializedName("LOCATION_ID") val locationID: String? = null,
    @SerializedName("LOCATION_DESC") val locationDesc: String? = null,
    @SerializedName("RDS_DIRECTION") val rdsDirection: String? = null
)
