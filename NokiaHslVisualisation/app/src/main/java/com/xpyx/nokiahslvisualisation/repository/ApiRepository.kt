package com.xpyx.nokiahslvisualisation.repository

import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.xpyx.nokiahslvisualisation.AlertsListQuery
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

//import com.xpyx.nokiahslvisualisation.api.RetrofitInstance
//import com.xpyx.nokiahslvisualisation.model.traffic.TrafficData
//import retrofit2.Response

class ApiRepository {

//    suspend fun getTrafficData(apiKey: String): Response<TrafficData> {
//        return RetrofitInstance.trafficApi.getTraffic(apiKey)
//    }


    suspend fun queryAlertList(): Response<AlertsListQuery.Data> {

            apolloClient.query(
                AlertsListQuery.builder().build()
            ).enqueue(object : ApolloCall.Callback<AlertsListQuery.Data>() {

                override fun onFailure(e: ApolloException) {
                    Log.d("DBG, on failure", e.localizedMessage ?: "Error")
                }

                override fun onResponse(response: Response<AlertsListQuery.Data>) {
                    Log.d("DBG, on response", response.data.toString())
                    return response
                }
            })
    }

    companion object {

        private val apolloClient: ApolloClient by lazy {
            ApolloClient.builder()
                .serverUrl("https://api.digitransit.fi/routing/v1/routers/hsl/index/graphql")
                .okHttpClient(
                    OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build()
                )
                .build()
        }
    }
}