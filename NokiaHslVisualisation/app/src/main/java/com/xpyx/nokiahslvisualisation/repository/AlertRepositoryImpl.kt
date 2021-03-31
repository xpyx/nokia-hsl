package com.xpyx.nokiahslvisualisation.repository

import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.xpyx.nokiahslvisualisation.GetAlertsQuery
import com.xpyx.nokiahslvisualisation.networking.apolloClient.AlertsAPI
import javax.inject.Inject

class AlertRepositoryImpl @Inject constructor(
    private val webService: AlertsAPI
): AlertRepository {

    override suspend fun queryAlertList(): Response<GetAlertsQuery.Data> {
        return webService.getApolloClient().query(GetAlertsQuery()).await()
    }
}