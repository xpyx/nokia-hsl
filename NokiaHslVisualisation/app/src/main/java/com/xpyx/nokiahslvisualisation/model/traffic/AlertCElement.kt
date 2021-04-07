package com.xpyx.nokiahslvisualisation.model.traffic

import com.google.gson.annotations.SerializedName

data class AlertCElement (
    @SerializedName("EVENT_CODE") val eventCode: Long? = null,
    @SerializedName("ALERTC_Q") val alertCQ: Long? = null
)