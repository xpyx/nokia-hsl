package com.xpyx.nokiahslvisualisation.model.mqtt

data class VehiclePosition(
    val VP: SingleVehiclePositionData
)

data class SingleVehiclePositionData(
    val desi: Int,
    val dir: Int,
    val oper: Int,
    val veh: Int,
    val tst: String,
    val tsi: Int,
    val spd: Float,
    val hdg: Int,
    val lat: Float,
    val long: Float,
    val acc: Double,
    val dl: Int,
    val odo: Double,
    val drst: Int,
    val oday: String,
    val jrn: Int,
    val line: Int,
    val start: String,
    val loc: String,
    val stop: String,
    val route: String,
    val occu: Int

)