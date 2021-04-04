package com.xpyx.nokiahslvisualisation.api

import android.os.Looper
import com.apollographql.apollo.ApolloClient
import okhttp3.OkHttpClient

interface AlertsAPI {

    fun getApolloClient(): ApolloClient {
        check(Looper.myLooper() == Looper.getMainLooper()) {
            "Only the main thread can get the apolloClient instance"
        }

        val okHttpClient = OkHttpClient.Builder().build()
        return ApolloClient.builder()
            .serverUrl("https://rickandmortyapi.com/graphql")
            .okHttpClient(okHttpClient)
            .build()
    }
}