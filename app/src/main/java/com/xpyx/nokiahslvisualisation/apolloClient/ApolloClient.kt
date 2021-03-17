package com.xpyx.nokiahslvisualisation.apolloClient

import com.apollographql.apollo.ApolloClient

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class ApolloClient {

    val apolloClient = ApolloClient.builder()
        .serverUrl("https://api.digitransit.fi/graphiql/hsl/")
        .okHttpClient(
            OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
        )
        .build()

}