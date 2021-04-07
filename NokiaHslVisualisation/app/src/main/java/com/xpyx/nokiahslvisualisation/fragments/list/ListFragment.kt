
package com.xpyx.nokiahslvisualisation.fragments.list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.api.ApiViewModel
import com.xpyx.nokiahslvisualisation.api.ApiViewModelFactory
import com.xpyx.nokiahslvisualisation.data.DataTrafficItem
import com.xpyx.nokiahslvisualisation.data.TrafficItemViewModel
import com.xpyx.nokiahslvisualisation.model.traffic.TrafficData
import com.xpyx.nokiahslvisualisation.repository.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response

class ListFragment : Fragment(){


    private lateinit var recyclerView: RecyclerView
    private lateinit var refreshButton: Button
    private lateinit var mTrafficViewModel: TrafficItemViewModel
    private lateinit var viewModel: ApiViewModel
    private lateinit var hereTrafficApiKey: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list, container, false)


        val adapter = context?.let { TrafficListAdapter(requireContext())}

        // Recycler view
        recyclerView = view.findViewById(R.id.bus_recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter


        mTrafficViewModel = ViewModelProvider(this).get(TrafficItemViewModel::class.java)
        mTrafficViewModel.readAllData.observe(viewLifecycleOwner, { traffic ->
            adapter?.setData(traffic)
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // API view model setup
        val repository = ApiRepository()
        val viewModelFactory = ApiViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ApiViewModel::class.java)


        // Button
        refreshButton = view.findViewById(R.id.traffic_refresh_button)

        hereTrafficApiKey = resources.getString(R.string.here_maps_api_key)
        viewModel.getTrafficData(hereTrafficApiKey)
        viewModel.myTrafficApiResponse.observe(viewLifecycleOwner, { response ->
            if (response.isSuccessful) {
                insertToTrafficDatabase(response)
            } else {
                Log.d("DBG", response.errorBody().toString())
            }
        })

    }

    private fun insertToTrafficDatabase(response: Response<TrafficData>) {
        Log.d("Traffic", response.body()!!.toString())
        val trafficItemList = response.body()!!.trafficItems
        if (trafficItemList != null) {
            for (item: com.xpyx.nokiahslvisualisation.model.traffic.TrafficItem in trafficItemList.trafficItem!!) {
                GlobalScope.launch(context = Dispatchers.IO) {
                    val traffic_item_id = item.trafficItemId
                    val traffic_item_status_short_desc = item.trafficItemStatusShortDesc
                    val traffic_item_type_desc = item.trafficItemTypeDesc
                    val start_time = item.startTime
                    val end_time = item.endTime
                    val criticality = item.criticality
                    val verified = item.verified
                    val rds_tmc_locations = item.rds_tmcLocations
                    val location = item.location
                    Log.d("DBG_ITEM",item.location.toString())
                    val traffic_item_detail = item.trafficItemDetail
                    val traffic_item_description = item.trafficItemDescriptionElement

                    val traffic = DataTrafficItem(
                        0,
                        traffic_item_id,
                        traffic_item_status_short_desc,
                        traffic_item_type_desc,
                        start_time,
                        end_time,
                        criticality,
                        verified,
                        rds_tmc_locations,
                        //location,
                        //traffic_item_detail,
                        traffic_item_description
                    )

                    mTrafficViewModel.addTrafficData(traffic)

                    Log.d("TRAFFIC", "Successfully added traffic item: $traffic_item_id")

                }
            }
        }
    }


}