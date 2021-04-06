package com.xpyx.nokiahslvisualisation.repository

import com.apollographql.apollo.api.Response
import com.xpyx.nokiahslvisualisation.AlertsListQuery

interface AlertRepository {

    suspend fun queryAlertList(): Response<AlertsListQuery.Data>

}