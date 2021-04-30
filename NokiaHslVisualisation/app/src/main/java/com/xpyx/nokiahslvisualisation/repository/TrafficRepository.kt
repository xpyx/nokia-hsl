/**
 * Description: Returns the instances for making Here traffic API calls
 *
 * Course: Mobiiliprojekti TX00CK67-3008
 * Name: Matias Hätönen
 */

package com.xpyx.nokiahslvisualisation.repository

import com.xpyx.nokiahslvisualisation.api.RetrofitInstance
import com.xpyx.nokiahslvisualisation.model.traffic.TrafficData
import retrofit2.Response

class TrafficRepository {

    suspend fun getTrafficData(apiKey: String): Response<TrafficData> {
        return RetrofitInstance.trafficApi.getTraffic(apiKey)
    }

}
