package com.xpyx.nokiahslvisualisation.repository

import com.apollographql.apollo.api.Response
import com.xpyx.nokiahslvisualisation.GetAlertsQuery

interface AlertRepository {

    suspend fun queryAlertList(): Response<GetAlertsQuery.Data>

}