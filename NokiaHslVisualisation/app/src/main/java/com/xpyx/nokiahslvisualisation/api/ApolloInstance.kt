package com.xpyx.nokiahslvisualisation.api

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.xpyx.nokiahslvisualisation.AlertsListQuery
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object ApolloInstance {

    private val apolloAlerts by lazy {
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

//
//    val alertsApi: AlertsApi {
//
//
//        val response = try {
//            apolloAlerts.client.query(AlertsListQuery()).await()
//        } catch (e: ApolloException) {
//            Log.e("AlertList", "Failure", e)
//            null
//        }
//    }
}
