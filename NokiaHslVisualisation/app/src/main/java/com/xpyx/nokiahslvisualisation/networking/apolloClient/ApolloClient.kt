package com.xpyx.nokiahslvisualisation.networking.apolloClient

import com.apollographql.apollo.ApolloClient

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class ApolloClient {

    val client: ApolloClient = ApolloClient.builder()
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