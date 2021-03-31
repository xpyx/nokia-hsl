package com.xpyx.nokiahslvisualisation.repository

import com.xpyx.nokiahslvisualisation.api.RetrofitInstance
import com.xpyx.nokiahslvisualisation.model.traffic.TrafficData
import retrofit2.Response

class ApiRepository {

    suspend fun getTrafficData(apiKey: String): Response<TrafficData> {
        return RetrofitInstance.trafficApi.getTraffic(apiKey)
    }
}