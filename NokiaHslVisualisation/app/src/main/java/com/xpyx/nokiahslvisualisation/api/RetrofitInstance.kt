package com.xpyx.nokiahslvisualisation.api

import com.xpyx.nokiahslvisualisation.utils.Constants.Companion.BASE_URL_HERE_TRAFFIC
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val retrofitTraffic by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_HERE_TRAFFIC)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val trafficApi: TrafficApi by lazy {
        retrofitTraffic.create(TrafficApi::class.java)
    }
}