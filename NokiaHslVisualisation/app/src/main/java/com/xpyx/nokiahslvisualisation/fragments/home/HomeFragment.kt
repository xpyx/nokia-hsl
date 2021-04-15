package com.xpyx.nokiahslvisualisation.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Switch
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.api.Response
import com.xpyx.nokiahslvisualisation.AlertsListQuery
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.api.AlertViewModel
import com.xpyx.nokiahslvisualisation.api.AlertViewModelFactory
import com.xpyx.nokiahslvisualisation.api.ApiViewModel
import com.xpyx.nokiahslvisualisation.api.ApiViewModelFactory
import com.xpyx.nokiahslvisualisation.data.AlertItem
import com.xpyx.nokiahslvisualisation.data.AlertItemViewModel
import com.xpyx.nokiahslvisualisation.data.DataTrafficItem
import com.xpyx.nokiahslvisualisation.data.TrafficItemViewModel
import com.xpyx.nokiahslvisualisation.model.traffic.TrafficData
import com.xpyx.nokiahslvisualisation.repository.AlertRepository
import com.xpyx.nokiahslvisualisation.repository.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mAlertViewModel: AlertItemViewModel
    private lateinit var hereTrafficViewModel: ApiViewModel
    private lateinit var hereTrafficApiKey: String
    private lateinit var mTrafficViewModel: TrafficItemViewModel
    private lateinit var mAlertApiViewModel: AlertViewModel
    private lateinit var adapter: AlertListAdapter
    private lateinit var drawer: DrawerLayout


    private val trafficIdRoomList = mutableListOf<Long>()
    private val trafficIdApiList = mutableListOf<Long>()

    // Set search at the top menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        val menuItem = menu.findItem(R.id.action_search)
        val searchView = menuItem.actionView as androidx.appcompat.widget.SearchView
        searchView.setOnQueryTextListener(object : OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                // do smthing
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // RecyclerView init
        adapter = context?.let { AlertListAdapter() }!!
        recyclerView = view.findViewById(R.id.alert_recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // AlertViewModel init
        mAlertViewModel = ViewModelProvider(this).get(AlertItemViewModel::class.java)
        mAlertViewModel.readAllData.observe(viewLifecycleOwner, { alerts ->
            adapter.setData(alerts)
        })

        // Set up Room DB Traffic view model
        mTrafficViewModel = ViewModelProvider(this).get(TrafficItemViewModel::class.java)
        mTrafficViewModel.readAllData.observe(viewLifecycleOwner, { traffic ->
            for (item in traffic) {
                trafficIdRoomList.add(item.traffic_item_id!!)
            }
        })

        // Set top bar search
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Alert viewmodel
        val alertRepository = AlertRepository()
        val alertViewModelFactory = AlertViewModelFactory(alertRepository)
        mAlertApiViewModel = ViewModelProvider(this, alertViewModelFactory).get(AlertViewModel::class.java)
        mAlertApiViewModel.getAlertData()
        mAlertApiViewModel.myAlertApiResponse.observe(viewLifecycleOwner, { response ->
            if (response != null && !response.hasErrors()) {
                insertToAlertDatabase(response)
            } else {
                Log.d("DBG", response.toString())
            }
        })

        // HERE MAPS TRAFFIC API view model setup
        val repository = ApiRepository()
        val viewModelFactory = ApiViewModelFactory(repository)
        hereTrafficViewModel = ViewModelProvider(this, viewModelFactory).get(ApiViewModel::class.java)
        hereTrafficApiKey = resources.getString(R.string.here_maps_api_key)
        hereTrafficViewModel.getTrafficData(hereTrafficApiKey)
        hereTrafficViewModel.myTrafficApiResponse.observe(viewLifecycleOwner, { response ->
            if (response.isSuccessful) {
                insertToTrafficDatabase(response)
            } else {
                Log.d("DBG", response.errorBody().toString())
            }
        })
    }

    private fun insertToTrafficDatabase(response: retrofit2.Response<TrafficData>) {
        var exists: Boolean
        val trafficItemList = response.body()!!.trafficDataTrafficItems
        if (trafficItemList != null) {
            for (item: com.xpyx.nokiahslvisualisation.model.traffic.TrafficItem in trafficItemList.trafficItem!!) {
                trafficIdApiList.add(item.trafficItemId!!)

                GlobalScope.launch(context = Dispatchers.IO) {
                    exists = mTrafficViewModel.checkIfExists(item.trafficItemId)

                    if (!exists) {

                        val trafficItemId = item.trafficItemId
                        val trafficItemStatusShortDesc = item.trafficItemStatusShortDesc
                        val trafficItemTypeDesc = item.trafficItemTypeDesc
                        val startTime = item.trafficItemStartTime
                        val endTime = item.trafficItemEndTime
                        val criticality = item.trafficItemCriticality
                        val verified = item.trafficItemVerified
                        val rdsTmcLocations = item.trafficitemRDSTmclocations
                        val location = item.trafficItemLocation
                        val trafficItemDetail = item.trafficItemDetail
                        val trafficItemDescriptionElement = item.trafficItemDescriptionElement

                        val traffic = DataTrafficItem(
                            0,
                            trafficItemId,
                            trafficItemStatusShortDesc,
                            trafficItemTypeDesc,
                            startTime,
                            endTime,
                            criticality,
                            verified,
                            rdsTmcLocations,
                            location,
                            trafficItemDetail,
                            trafficItemDescriptionElement
                        )

                        if (criticality != null) {
                            if (criticality.ityDescription.equals("critical")) {
                                mTrafficViewModel.addTrafficData(traffic)
                                Log.d("TRAFFIC", "Successfully added traffic item: $trafficItemId")
                            }
                        }
                    }
                }
            }
        }

        // Remove ended traffic items
        if (trafficIdApiList.isNotEmpty() && trafficIdRoomList.isNotEmpty()) {
            for (item in trafficIdRoomList){
                if (trafficIdApiList.contains(item)){
                    GlobalScope.launch(context = Dispatchers.IO) {
                        mTrafficViewModel.removeIfNotExists(item)
                        Log.d("REMOVED_ITEM", "Removed item with id: $item")
                    }
                }
            }
        }
    }

    private fun insertToAlertDatabase(response: Response<AlertsListQuery.Data>) {
        var exists: Boolean
        if (response.data?.alerts() != null) {
            response.data?.alerts()!!.forEach { item ->
                GlobalScope.launch(context = Dispatchers.IO) {

                    exists = mAlertViewModel.checkIfExists(item.id())

                    if (exists) {

                        Log.d("DBG", "Alert already in database")

                    } else {
                        val alertId = item.id()
                        val alertHeaderText = item.alertHeaderText()
                        val alertDescriptionText = item.alertDescriptionText()
                        val effectiveStartDate = item.effectiveStartDate().toString()
                        val effectiveEndDate = item.effectiveEndDate().toString()
                        val alertUrl = item.alertUrl()
                        val alertSeverityLevel = item.alertSeverityLevel().toString()
                        val alertCause = item.alertCause().toString()
                        val alertEffect = item.alertEffect().toString()

                        val alert = AlertItem(
                            0,
                            alertId,
                            alertHeaderText,
                            alertDescriptionText,
                            effectiveStartDate,
                            effectiveEndDate,
                            alertUrl,
                            alertSeverityLevel,
                            alertCause,
                            alertEffect
                        )
                        mAlertViewModel.addAlertItem(alert)
                    }
                }
            }
        }
    }
}
