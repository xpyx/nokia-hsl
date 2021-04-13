package com.xpyx.nokiahslvisualisation.repository

import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.xpyx.nokiahslvisualisation.AlertsListQuery
import com.xpyx.nokiahslvisualisation.networking.apolloClient.ApolloClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AlertRepository {

    suspend fun getAlertData(): Response<AlertsListQuery.Data> {
        val apollo = ApolloClient()
        lateinit var alertItems: Response<AlertsListQuery.Data>
        val job = GlobalScope.launch(Dispatchers.IO) {
            alertItems = apollo.client.query(AlertsListQuery()).await()
        }
        job.join()
        return alertItems
    }
}
