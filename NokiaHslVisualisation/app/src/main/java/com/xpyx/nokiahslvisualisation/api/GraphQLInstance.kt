package com.xpyx.nokiahslvisualisation.api

import com.apollographql.apollo.ApolloClient
import com.xpyx.nokiahslvisualisation.networking.AlertsApi
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object GraphQLInstance {

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

//    val alertsApi: AlertsApi by lazy {
//        ApolloClient.create(AlertsApi::class.java)
//    }

}