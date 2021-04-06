package com.xpyx.nokiahslvisualisation.repository

import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.xpyx.nokiahslvisualisation.AlertsListQuery
import com.xpyx.nokiahslvisualisation.Constants.Companion.ALERT_API
import com.xpyx.nokiahslvisualisation.api.GraphQLInstance
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


class ApiRepository {

    private val apolloClient = ApolloClient.builder()
        .serverUrl(ALERT_API)
        .build()

//    suspend fun getTrafficData(apiKey: String): Response<TrafficData> {
//        return RetrofitInstance.trafficApi.getTraffic(apiKey)
//    }


//    suspend fun getAlerts(): Response<AlertsListQuery.Data> {
//
//        var re: Response<AlertsListQuery.Data>
//
//        val apollo = com.xpyx.nokiahslvisualisation.networking.apolloClient.ApolloClient()
//        apollo.client.query(
//            AlertsListQuery.builder().build()
//        ).enqueue(object : ApolloCall.Callback<AlertsListQuery.Data>() {
//
//            override fun onFailure(e: ApolloException) {
//                Log.d("DBG, on failure", e.localizedMessage ?: "Error")
//            }
//
//            override fun onResponse(response: Response<AlertsListQuery.Data>) {
//                Log.d("DBG, on response", response.data.toString())
//                re = response
//            }
//        })
//
//        return re
//    }


}