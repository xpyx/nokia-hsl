package com.xpyx.nokiahslvisualisation.repository

import com.xpyx.nokiahslvisualisation.api.RetrofitInstance
import com.xpyx.nokiahslvisualisation.model.traffic.TRAFFIC_ITEM
import retrofit2.Response

class ApiRepository {

    suspend fun getTrafficData(apikey: String): Response<ArrayList<TRAFFIC_ITEM>> {
        return RetrofitInstance.trafficApi.getTraffic(apikey)
    }
}