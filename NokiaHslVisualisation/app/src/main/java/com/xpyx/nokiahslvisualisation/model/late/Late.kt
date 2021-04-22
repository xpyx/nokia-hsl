package com.xpyx.nokiahslvisualisation.model.late

data class Late(
    val routeId: String?,
    val transportMode: String?,
    val arrivalDelay: String?,
    var directionId: String?
)