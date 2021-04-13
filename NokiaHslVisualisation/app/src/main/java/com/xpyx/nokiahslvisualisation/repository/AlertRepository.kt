package com.xpyx.nokiahslvisualisation.repository

import android.util.Log
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
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
            val alertItems = try {
                apollo.client.query(AlertsListQuery()).await()
            } catch (e: ApolloException) {
                Log.e("AlertList", "Failure", e)
                null
            }
        }
        job.join()
        return alertItems
    }


}
