package com.xpyx.nokiahslvisualisation.repository

import com.xpyx.nokiahslvisualisation.api.RetrofitInstance
import com.xpyx.nokiahslvisualisation.model.traffic.Json4Kotlin_Base
import com.xpyx.nokiahslvisualisation.model.traffic.TRAFFIC_ITEMS
import retrofit2.Response

class ApiRepository {

    suspend fun getTrafficData(apiKey: String): Response<Json4Kotlin_Base> {
        return RetrofitInstance.trafficApi.getTraffic(apiKey)
    }
}