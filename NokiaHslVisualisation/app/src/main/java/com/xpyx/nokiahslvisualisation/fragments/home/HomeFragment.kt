/**
 * Description:
 *
 * Course: Mobiiliprojekti TX00CK67-3008
 * Name: Ville Pystynen
 */

package com.xpyx.nokiahslvisualisation.fragments.home

import android.content.Context
import android.content.res.Configuration
import android.location.Location
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
import com.xpyx.nokiahslvisualisation.utils.Constants
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_traffic_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mAlertViewModel: AlertItemViewModel
    private lateinit var mAlertApiViewModel: AlertViewModel
    private lateinit var adapter: AlertListAdapter
    private val listOfFilters = mutableMapOf<String, Any>()
    private val stringSearchView: String = "stringSearchView"

    val listOfCheckBoxNames = listOf<String>(
            "UNKNOWN_SEVERITY",
            "INFO",
            "WARNING",
            "SEVERE",
            "NO_SERVICE",
            "REDUCED_SERVICE",
            "SIGNIFICANT_DELAYS",
            "DETOUR",
            "ADDITIONAL_SERVICE",
            "MODIFIED_SERVICE",
            "OTHER_EFFECT",
            "UNKNOWN_EFFECT",
            "STOP_MOVED",
            "NO_EFFECT",
            "UNKNOWN_CAUSE",
            "OTHER_CAUSE",
            "TECHNICAL_PROBLEM",
            "STRIKE",
            "DEMONSTRATION",
            "CAUSE_MODIFIED_SERVICE",
            "ACCIDENT",
            "HOLIDAY",
            "WEATHER",
            "MAINTENANCE",
            "CONSTRUCTION",
            "POLICE_ACTIVITY",
            "MEDICAL_EMERGENCY"
    )

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
                listOfFilters[stringSearchView] = newText!!
                checkFilters()
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
            checkFilters()
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

        loadData()


        listOfCheckBoxes.forEach {
            val i = listOfCheckBoxes.indexOf(it)

            val name = listOfCheckBoxNames[i]
            Log.d("filter", name)
            val value = listOfFilters[name] as Boolean

            if (value) {
                it.isChecked = true
                it.jumpDrawablesToCurrentState()
            } else {
                it.isChecked = false
                it.jumpDrawablesToCurrentState()
            }

            it.setOnCheckedChangeListener { _, _ ->
                listOfFilters[name] = it.isChecked
                checkFilters()
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

    override fun onPause() {
        super.onPause()
        saveData()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun saveData() {
        val sPreferences = activity?.getSharedPreferences(Constants.HSL_ALERT_FILTERS, Context.MODE_PRIVATE)
        val editor = sPreferences?.edit()

        for (item in listOfCheckBoxNames) {
            editor?.putBoolean(item, listOfFilters[item] as Boolean)
            editor?.apply()
        }
    }

    private fun loadData() {
        val sPreferences = activity?.getSharedPreferences(Constants.HSL_ALERT_FILTERS, Context.MODE_PRIVATE)
        for (item in listOfCheckBoxNames) {
            listOfFilters[item] = sPreferences?.getBoolean(item, false) as Boolean
        }

        checkFilters()
    }

    private fun checkFilters() {
        var filterText = ""
        for (item in listOfCheckBoxNames) {
            val value = listOfFilters[item] as Boolean
            if (value) {
                filterText += "$item;"
            }
        }
        val stringSearch: String? = listOfFilters[stringSearchView] as String?
        if (stringSearch != null && stringSearch != "") {
            filterText += "$stringSearch;"
        }
        Log.d("filtering", filterText)

        adapter.filter.filter(filterText)

    }
}
