package com.xpyx.nokiahslvisualisation.model.traffic

import com.google.gson.annotations.SerializedName

data class Incident (
    @SerializedName("RESPONSE_VEHICLES") val responseVehicles: Boolean? = null,
    @SerializedName("MISCELLANEOUS_INCIDENT") val miscellaneousIncident: MiscellaneousIncident? = null
)