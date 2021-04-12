package com.xpyx.nokiahslvisualisation.api

import com.xpyx.nokiahslvisualisation.model.traffic.TrafficData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AlertsApi {

    @GET("incidents.json?bbox=60.382,24.432;60.089,25.32")
    suspend fun getAlerts(@Query("apikey") q: String): Response<TrafficData>

}