package com.xpyx.nokiahslvisualisation.repository

import com.xpyx.nokiahslvisualisation.api.RetrofitInstance
import com.xpyx.nokiahslvisualisation.model.traffic.TRAFFIC_ITEMS
import retrofit2.Response

class ApiRepository {

    suspend fun getTrafficData(apiKey: String): Response<TRAFFIC_ITEMS> {
        return RetrofitInstance.trafficApi.getTraffic(apiKey)
    }
}