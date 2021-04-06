package com.xpyx.nokiahslvisualisation.networking

import android.os.Looper
import com.apollographql.apollo.ApolloClient
import com.xpyx.nokiahslvisualisation.utils.Constants.Companion.ALERT_API
import okhttp3.OkHttpClient

class AlertsApi {

    fun getApolloClient(): ApolloClient {
        check(Looper.myLooper() == Looper.getMainLooper()) {
            "Only the main thread can get the apolloClient instance"
        }

        val okHttpClient = OkHttpClient.Builder().build()
        return ApolloClient.builder()
            .serverUrl(ALERT_API)
            .okHttpClient(okHttpClient)
            .build()
    }

}