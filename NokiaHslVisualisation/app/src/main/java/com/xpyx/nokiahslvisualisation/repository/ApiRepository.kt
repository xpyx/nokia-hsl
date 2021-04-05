package com.xpyx.nokiahslvisualisation.repository


import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.exception.ApolloException
import com.xpyx.nokiahslvisualisation.AlertsListQuery
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

import com.xpyx.nokiahslvisualisation.api.RetrofitInstance
import com.xpyx.nokiahslvisualisation.model.traffic.TrafficData
import retrofit2.Response

class ApiRepository {

    suspend fun getTrafficData(apiKey: String): com.apollographql.apollo.api.Response<TrafficData> {
        return RetrofitInstance.trafficApi.getTraffic(apiKey)

    }



}