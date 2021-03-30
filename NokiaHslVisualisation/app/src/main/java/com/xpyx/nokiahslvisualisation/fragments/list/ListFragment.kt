package com.xpyx.nokiahslvisualisation.fragments.list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.api.ApiViewModel
import com.xpyx.nokiahslvisualisation.api.ApiViewModelFactory
import com.xpyx.nokiahslvisualisation.data.TrafficItem
import com.xpyx.nokiahslvisualisation.data.TrafficItemViewModel
import com.xpyx.nokiahslvisualisation.model.traffic.Json4Kotlin_Base
import com.xpyx.nokiahslvisualisation.model.traffic.TRAFFIC_ITEM
import com.xpyx.nokiahslvisualisation.model.traffic.TRAFFIC_ITEMS
import com.xpyx.nokiahslvisualisation.repository.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response

class ListFragment : Fragment(){

    private val trafficInfoList = mutableListOf<TRAFFIC_ITEM>()

    private val bus1 = FakeBus("Line 200","Late 10 minutes because of traffic.")
    private val bus2 = FakeBus("Line 41 HKI","Missing one bus on line, 3 missed launches.")
    private val busList = mutableListOf<FakeBus>(bus1, bus2)
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


        val adapter = context?.let { BusListAdapter()}

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
        refreshButton = requireContext().getString(R.string.here_maps_api_key)

        hereTrafficApiKey = ${{ secrets.HERE_MAPS_API_KEY }}

        viewModel.getTrafficData(hereTrafficApiKey)
        viewModel.myTrafficApiResponse.observe(viewLifecycleOwner, { response ->
            if (response.isSuccessful) {
                insertToTrafficDatabase(response)
            } else {
                Log.d("DBG", response.errorBody().toString())
            }
        })

    }

    private fun insertToTrafficDatabase(response: Response<Json4Kotlin_Base>) {
        Log.d("Traffic", response.body()!!.toString())
        val trafficItemList = response.body()!!.traffic_items
        for (item: TRAFFIC_ITEM in trafficItemList.traffic_item) {
            GlobalScope.launch(context = Dispatchers.IO) {
                val traffic_item_id = item.traffic_item_id
                val traffic_item_status_short_desc = item.traffic_item_status_short_desc
                val traffic_item_type_desc = item.traffic_item_type_desc
                val start_time = item.start_time
                val end_time = item.end_time
                val criticality = item.criticality
                val verified = item.verified
                val rds_tmc_locations = item.rds_tmc_locations
                val location = item.location
                val traffic_item_detail = item.traffic_item_detail
                val traffic_item_description = item.traffic_item_description

                val traffic = TrafficItem(
                    0,
                    traffic_item_id,
                    traffic_item_status_short_desc,
                    traffic_item_type_desc,
                    start_time,
                    end_time,
                    criticality,
                    verified,
                    //rds_tmc_locations,
                    //location,
                    //traffic_item_detail,
                    //traffic_item_description
                )

                mTrafficViewModel.addTrafficData(traffic)

                Log.d("TRAFFIC", "Successfully added traffic item: $traffic_item_id")

            }
        }
    }


}
