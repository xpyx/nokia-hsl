package com.xpyx.nokiahslvisualisation.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.CheckBox
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.api.Response
import com.xpyx.nokiahslvisualisation.AlertsListQuery
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.StopTimesListQuery
import com.xpyx.nokiahslvisualisation.api.*
import com.xpyx.nokiahslvisualisation.data.*
import com.xpyx.nokiahslvisualisation.repository.AlertRepository
import com.xpyx.nokiahslvisualisation.repository.StopTimesRepository
import com.xpyx.nokiahslvisualisation.api.AlertViewModel
import com.xpyx.nokiahslvisualisation.api.AlertViewModelFactory
import com.xpyx.nokiahslvisualisation.data.AlertItem
import com.xpyx.nokiahslvisualisation.data.AlertItemViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mAlertViewModel: AlertItemViewModel
    private lateinit var mStopTimesItemViewModel: StopTimesItemViewModel
    private lateinit var mAlertApiViewModel: AlertViewModel
    private lateinit var mStopTimesApiViewModel: StopTimesViewModel
    private lateinit var adapter: AlertListAdapter

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

        // Set top bar search
        setHasOptionsMenu(true)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Alert viewmodel
        val alertRepository = AlertRepository()
        val alertViewModelFactory = AlertViewModelFactory(alertRepository)
        mAlertApiViewModel =
            ViewModelProvider(this, alertViewModelFactory).get(AlertViewModel::class.java)
        mAlertApiViewModel.getAlertData()
        mAlertApiViewModel.myAlertApiResponse.observe(viewLifecycleOwner, { response ->
            if (response != null && !response.hasErrors()) {
                insertToAlertDatabase(response)
            } else {
                // Log.d("DBG", response.toString())
            }
        })

        // StopTimes API viewmodel
        val stopTimesRepository = StopTimesRepository()
        val stopTimesViewModelFactory = StopTimesViewModelFactory(stopTimesRepository)
        mStopTimesApiViewModel =
            ViewModelProvider(this, stopTimesViewModelFactory).get(StopTimesViewModel::class.java)
        mStopTimesApiViewModel.getStopTimesData()
        mStopTimesApiViewModel.myStopTimesApiResponse.observe(viewLifecycleOwner, { response ->
            if (response != null) {

                // The stoptimes data is here
                response.data?.stops()?.forEach {
                    if (it.stoptimesForPatterns()?.isNotEmpty() == true) {
                        val routeId = it.stoptimesForPatterns()?.get(0)?.stoptimes()?.get(0)?.trip()?.route()?.gtfsId()?.substring(4)
                        val transportMode = it.stoptimesForPatterns()?.get(0)?.stoptimes()?.get(0)?.trip()?.route()?.mode()
                        val arrivalDelay = it.stoptimesForPatterns()?.get(0)?.stoptimes()?.get(0)?.arrivalDelay()
                        var directionId = it.stoptimesForPatterns()?.get(0)?.stoptimes()?.get(0)?.trip()?.directionId()

                        // Change direction id according to instructions. Also note if null, then -> "+"
                        if (directionId.equals("0")) {
                            directionId = "1"
                        } else if (directionId.equals("1")) {
                            directionId = "2"
                        }

                        if (arrivalDelay != null) {
                            if (arrivalDelay > 240) {
                                Log.d("DBG late vehicles", "routeId         : $routeId")
                                Log.d("DBG late vehicles", "transportMode   : $transportMode")
                                Log.d("DBG late vehicles", "arrivalDelay    : $arrivalDelay")
                                Log.d("DBG late vehicles", "directionId     : $directionId")
                                Log.d("DBG late vehicles", "---------------------------")
                            }
                        }
                    }
                }

                // Log.d("DBG", "@ mStopTimesApiViewModel.myStopTimesApiResponse.observe + $response")
                // insertToStopTimesDatabase(response)
            } else {
                // Log.d("DBG", response.toString())
            }
        })

        // Drawer filtering
        val listOfCheckBoxes = listOf<CheckBox>(
            UNKNOWN_SEVERITY,
            INFO,
            WARNING,
            SEVERE,
            NO_SERVICE,
            REDUCED_SERVICE,
            SIGNIFICANT_DELAYS,
            DETOUR,
            ADDITIONAL_SERVICE,
            MODIFIED_SERVICE,
            OTHER_EFFECT,
            UNKNOWN_EFFECT,
            STOP_MOVED,
            NO_EFFECT,
            UNKNOWN_CAUSE,
            OTHER_CAUSE,
            TECHNICAL_PROBLEM,
            STRIKE,
            DEMONSTRATION,
            CAUSE_MODIFIED_SERVICE,
            ACCIDENT,
            HOLIDAY,
            WEATHER,
            MAINTENANCE,
            CONSTRUCTION,
            POLICE_ACTIVITY,
            MEDICAL_EMERGENCY
        )

        listOfCheckBoxes.forEach {
            val name = it.text.toString()
            it.setOnCheckedChangeListener { _, _ ->
                if (it.isChecked) {
                    adapter.filter.filter(name)
                } else if (!it.isChecked) {
                    adapter.filter.filter("")
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

                        // Log.d("DBG", "Alert already in database")

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

    private fun insertToStopTimesDatabase(response: Response<StopTimesListQuery.Data>) {
        if (response.data?.stops() != null) {
            response.data?.stops()!!.forEach { item ->
                if (item.stoptimesForPatterns()?.isEmpty() != true) {
                    GlobalScope.launch(context = Dispatchers.IO) {
                        val stopTimes = StopTimesItem(
                            0,
                            item
                        )
                        mStopTimesItemViewModel.addStopItem(stopTimes)
                    }
                }
            }
        }
    }
}
