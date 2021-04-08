package com.xpyx.nokiahslvisualisation.model.traffic

import com.google.gson.annotations.SerializedName

data class RDSTmcLocation (
    @SerializedName("EBU_COUNTRY_CODE") val rDSTmcLocationEbuCountryCode: String? = null,
    @SerializedName("TABLE_ID") val rDSTmcLocationTableID: Long? = null,
    @SerializedName("LOCATION_ID") val rDSTmcLocationLocationID: String? = null,
    @SerializedName("LOCATION_DESC") val rDSTmcLocationLocationDesc: String? = null,
    @SerializedName("RDS_DIRECTION") val rDSTmcLocationRdsDirection: String? = null
)
