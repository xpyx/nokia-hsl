package com.xpyx.nokiahslvisualisation.repository

import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.xpyx.nokiahslvisualisation.StopTimesListQuery
import com.xpyx.nokiahslvisualisation.networking.apolloClient.ApolloClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class StopTimesRepository {

    suspend fun getStopTimesData(): Response<StopTimesListQuery.Data> {
        val apollo = ApolloClient()
        lateinit var stopTimeItems: Response<StopTimesListQuery.Data>
        val job = GlobalScope.launch(Dispatchers.IO) {
            stopTimeItems = apollo.client.query(StopTimesListQuery()).await()
        }
        job.join()
        return stopTimeItems
    }
}
