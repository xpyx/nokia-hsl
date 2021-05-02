/**
 * Description: An Apollo client for making queries to HSL GraphQL API
 *
 * Course: Mobiiliprojekti TX00CK67-3008
 * Name: Ville Pystynen
 */

package com.xpyx.nokiahslvisualisation.networking.apolloClient

import com.apollographql.apollo.ApolloClient

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class ApolloClient {

    val client: ApolloClient = ApolloClient.builder()
        .serverUrl("https://api.digitransit.fi/routing/v1/routers/hsl/index/graphql")
        .okHttpClient(
            OkHttpClient.Builder()
                .connectTimeout(45, TimeUnit.SECONDS)
                .writeTimeout(45, TimeUnit.SECONDS)
                .readTimeout(45, TimeUnit.SECONDS)
                .build()
        )
        .build()

}