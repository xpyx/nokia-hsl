/**
 * Description:
 *
 * Fragment to list causes of possible traffic jams. User can filter data by
 * 1) Criticality - Critical, Major and Minor
 * 2) Distance - Minimum and maximum
 * 3) Miscellaneous - Road closed and Response vehicles
 * 4) Type - Event or Incident
 *
 *
 * Course: Mobiiliprojekti TX00CK67-3008
 * Name: Matias Hätönen
 */

package com.xpyx.nokiahslvisualisation.fragments.traffic_list

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.google.android.material.slider.RangeSlider
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.R.*
import com.xpyx.nokiahslvisualisation.api.TrafficApiViewModel
import com.xpyx.nokiahslvisualisation.api.TrafficApiViewModelFactory
import com.xpyx.nokiahslvisualisation.data.DataTrafficItem
import com.xpyx.nokiahslvisualisation.data.TrafficItemViewModel
import com.xpyx.nokiahslvisualisation.model.traffic.TrafficData
import com.xpyx.nokiahslvisualisation.repository.TrafficRepository
import com.xpyx.nokiahslvisualisation.utils.Constants
import kotlinx.android.synthetic.main.fragment_traffic_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class TrafficListFragment : Fragment(){


    private lateinit var recyclerView: RecyclerView
    private lateinit var hereTrafficViewModelTraffic: TrafficApiViewModel
    private lateinit var hereTrafficApiKey: String
    private lateinit var mTrafficViewModel: TrafficItemViewModel
    private val trafficIdApiList = mutableListOf<Long>()
    private lateinit var adapter: TrafficListAdapter
    private lateinit var viewHere: View
    private val trafficIdRoomList = mutableListOf<Long>()
    private val listOfFilters = mutableMapOf<String, Any>()
    private val listOfBooleanFilterNames = listOf(
            "critical",
            "major",
            "minor",
            "road_closed",
            "response_vehicles",
            "incident",
            "event"
    )
    private val listOfDistanceFilterNames = listOf(
            "min_lat_difference",
            "max_lat_difference",
            "min_lon_difference",
            "max_lon_difference"
    )
    private var listOfCheckBoxes = listOf<CheckBox>()
    private var listOfRadioButtons = listOf<RadioButton>()


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationNow: Location = Location("LocationInit")
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewHere = inflater.inflate(layout.fragment_traffic_list, container, false)

        adapter = context?.let { TrafficListAdapter(requireContext()) }!!

        // Recycler view
        recyclerView = viewHere.findViewById(R.id.bus_recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        getLocationUpdates()
        mTrafficViewModel = ViewModelProvider(this).get(TrafficItemViewModel::class.java)
        mTrafficViewModel.readAllData.observe(viewLifecycleOwner, { traffic ->
            adapter.setData(traffic)
            checkFilters()
            for (item in traffic) {
                trafficIdRoomList.add(item.traffic_item_id!!)
            }
        })
        return viewHere
    }

    @SuppressLint("LogNotTimber")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listOfCheckBoxes = listOf<CheckBox>(
                critical_checkbox,
                major_checkbox,
                minor_checkbox,
                road_closed_checkbox,
                response_vehicles_checkbox
        )
        listOfRadioButtons = listOf<RadioButton>(
                incident_radio_button,
                event_radio_button
        )


        // HERE MAPS TRAFFIC API view model setup
        val repository = TrafficRepository()
        val viewModelFactory = TrafficApiViewModelFactory(repository)
        hereTrafficViewModelTraffic =
                ViewModelProvider(this, viewModelFactory).get(TrafficApiViewModel::class.java)

        hereTrafficApiKey = resources.getString(string.here_maps_api_key)
        //hereTrafficApiKey = ""

        loadData()
        if (hereTrafficApiKey.isNotEmpty()) {
            hereTrafficViewModelTraffic.getTrafficData(hereTrafficApiKey)
            hereTrafficViewModelTraffic.myTrafficApiResponse.observe(viewLifecycleOwner, { response ->
                if (response.isSuccessful) {
                    insertToTrafficDatabase(response)
                } else {
                    Log.d("DBG", response.errorBody().toString())
                }
            })

        } else /*IF PUBLISHED IN PLAY STORE*/ {
            Toast.makeText(requireContext(), getString(string.toast_api_key), Toast.LENGTH_LONG).show()
            add_api_key_layout.layoutParams.apply {
                (this as LinearLayout.LayoutParams).weight = 1F
                this.height = 80
            }
            parent_linear_layout.requestLayout()

            api_key_link_button.setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.API_KEY_LINK_URL))
                requireContext().startActivity(i)
            }

            edit_text_api_key.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE && !edit_text_api_key.text.isNullOrEmpty()) {
                    hereTrafficApiKey = edit_text_api_key.text.toString()
                    hereTrafficViewModelTraffic.getTrafficData(hereTrafficApiKey)
                    hereTrafficViewModelTraffic.myTrafficApiResponse.observe(viewLifecycleOwner, { response ->
                        if (response.isSuccessful) {
                            insertToTrafficDatabase(response)
                        } else {
                            Log.d("DBG", response.errorBody().toString())
                        }
                    })
                    hideKeyboard()
                    add_api_key_layout.layoutParams.apply {
                        (this as LinearLayout.LayoutParams).weight = 0F
                        this.height = 0
                    }
                    parent_linear_layout.requestLayout()
                    return@OnEditorActionListener true
                }
                false
            })

            add_api_key_button.setOnClickListener {
                if (!edit_text_api_key.text.isNullOrEmpty()) {
                    hereTrafficApiKey = edit_text_api_key.text.toString()
                    hereTrafficViewModelTraffic.getTrafficData(hereTrafficApiKey)
                    hereTrafficViewModelTraffic.myTrafficApiResponse.observe(viewLifecycleOwner, { response ->
                        if (response.isSuccessful) {
                            insertToTrafficDatabase(response)
                        } else {
                            Log.d("DBG", response.errorBody().toString())
                        }
                    })
                    hideKeyboard()
                    add_api_key_layout.layoutParams.apply {
                        (this as LinearLayout.LayoutParams).weight = 0F
                        this.height = 0
                    }
                    parent_linear_layout.requestLayout()
                }
            }

        }

        checkFilters()
        distance_slider.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener{
            override fun onStartTrackingTouch(slider: RangeSlider) {
                Log.d("RangeSlider", "Started tracking touch")
            }
            override fun onStopTrackingTouch(slider: RangeSlider) {
                Log.d("RangeSlider", "Stopped tracking touch")
                // Kilometer values to coordinate differences
                listOfFilters["min_lat_difference"] = convertToCoordinates()[0]
                listOfFilters["max_lat_difference"] = convertToCoordinates()[1]
                listOfFilters["min_lon_difference"] = convertToCoordinates()[0]
                listOfFilters["max_lon_difference"] = convertToCoordinates()[1]
                checkFilters()
            }
        })

        clear_all_button.setOnClickListener{

            radio_group_criticality.clearCheck()
            critical_checkbox.isChecked = false
            major_checkbox.isChecked = false
            minor_checkbox.isChecked = false
            road_closed_checkbox.isChecked = false
            response_vehicles_checkbox.isChecked = false

            distance_slider.setValues(0F, 150F)

            for (item in listOfBooleanFilterNames) {
                listOfFilters[item] = false
            }
            listOfFilters["min_lat_difference"] = convertToCoordinates()[0]
            listOfFilters["max_lat_difference"] = convertToCoordinates()[1]
            listOfFilters["min_lon_difference"] = convertToCoordinates()[0]
            listOfFilters["max_lon_difference"] = convertToCoordinates()[1]
            checkFilters()
        }

        listOfCheckBoxes.forEach {
            val i = listOfCheckBoxes.indexOf(it)

            val name = listOfBooleanFilterNames[i].toLowerCase().replace("\\s+".toRegex(), "_")
            Log.d("DEBUGGING",name)
            val value = listOfFilters[name] as Boolean
            if (value) {
                it.isChecked = true
                it.jumpDrawablesToCurrentState()
            } else {
                it.isChecked = false
                it.jumpDrawablesToCurrentState()
            }

            it.setOnCheckedChangeListener  {_, _ ->
                listOfFilters[name] = it.isChecked
                checkFilters()
            }

        }

        listOfRadioButtons.forEach {
            val i = listOfRadioButtons.indexOf(it)+5

            val name = listOfBooleanFilterNames[i].toLowerCase(Locale.ROOT).replace("\\s+".toRegex(), "_")
            val value = listOfFilters[name] as Boolean
            if (value) {
                it.isChecked = true
                it.jumpDrawablesToCurrentState()
            } else {
                it.isChecked = false
                it.jumpDrawablesToCurrentState()
            }

            it.setOnCheckedChangeListener {_, _ ->
                listOfFilters[name] = it.isChecked
                checkFilters()
            }
        }
    }

    private fun convertToCoordinates() : List<Double> {
        val differenceMin = distance_slider.values[0]/110.574
        val differenceMax = distance_slider.values[1]/110.574
        return listOf(differenceMin, differenceMax)
    }
    private fun convertFromCoordinates(coordinateList: List<Double>) : List<Float> {
        val differenceMin = coordinateList[0]*110.574
        val differenceMax = coordinateList[1]*110.574
        return listOf(differenceMin.toFloat(), differenceMax.toFloat())
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


                        mTrafficViewModel.addTrafficData(traffic)
                        Log.d("TRAFFIC", "Successfully added traffic item: $trafficItemId")

                    }
                }
            }
        }

        // Remove ended traffic items
        if (trafficIdApiList.isNotEmpty() && trafficIdRoomList.isNotEmpty()) {
            for (item in trafficIdRoomList) {
                if (trafficIdApiList.contains(item)) {
                    GlobalScope.launch(context = Dispatchers.IO) {
                        mTrafficViewModel.removeIfNotExists(item)
                        Log.d("REMOVED_ITEM", "Removed item with id: $item")
                    }
                }
            }
        }
    }


    override fun onPause() {
        super.onPause()
        saveData()
        stopLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        loadData()
        startLocationUpdates()
    }

    private fun saveData() {

        val sPreferences = activity?.getSharedPreferences(Constants.TRAFFIC_FILTERS, MODE_PRIVATE)
        val editor = sPreferences?.edit()
        for (item in listOfBooleanFilterNames) {
            editor?.putBoolean(item, listOfFilters[item] as Boolean)
            editor?.apply()
        }

        for (item in listOfDistanceFilterNames) {
            val value = listOfFilters[item] as Double
            editor?.putFloat(item, value.toFloat())
            editor?.apply()
            Log.d("FILTERSAVED", "$item: $value")
        }
        if (!hereTrafficApiKey.isNullOrEmpty()) {
            val apikeyPreferences = activity?.getSharedPreferences(Constants.TRAFFIC_API_KEY, MODE_PRIVATE)
            val editor2 = apikeyPreferences?.edit()
            editor2?.putString(Constants.TRAFFIC_API_KEY, hereTrafficApiKey)
            editor2?.apply()
        }
    }

    private fun loadData() {

        val apiKeyPreferences = activity?.getSharedPreferences(Constants.TRAFFIC_API_KEY, MODE_PRIVATE)
        val buffer: String? = apiKeyPreferences?.getString(Constants.TRAFFIC_API_KEY, "")
        if (!buffer.isNullOrEmpty()) {
            hereTrafficApiKey = buffer
        }

        val sPreferences = activity?.getSharedPreferences(Constants.TRAFFIC_FILTERS, MODE_PRIVATE)
        for (item in listOfBooleanFilterNames) {
            listOfFilters[item] = sPreferences?.getBoolean(item, false) as Boolean
        }
        for (item in listOfDistanceFilterNames) {
            if (item == "min_lat_difference" || item == "min_lon_difference"){
                listOfFilters[item] = sPreferences?.getFloat(item, 0.0F)?.toDouble() as Double
            } else {
                listOfFilters[item] = sPreferences?.getFloat(item, 1.3565576076507568F)?.toDouble() as Double
            }
        }
        val list = listOf(listOfFilters["min_lat_difference"] as Double, listOfFilters["max_lat_difference"] as Double)
        distance_slider.setValues(convertFromCoordinates(list)[0], convertFromCoordinates(list)[1])

        checkFilters()
    }

    private fun checkFilters() {

        var filterText = ""
        for (item in listOfBooleanFilterNames) {
            val value = listOfFilters[item] as Boolean
            if (value) {
                filterText += "$item;"
            }
        }

        val minValue = distance_slider.values[0]
        val maxValue = distance_slider.values[1]

        if ((minValue > 0.0 || maxValue < 150.0 ) && locationNow != Location("LocationInit")){
            filterText += "${locationNow.latitude};${locationNow.longitude};"

            val minDistanceFilter = listOfFilters["min_lat_difference"] as Double
            filterText += "$minDistanceFilter;"
            val maxDistanceFilter = listOfFilters["max_lat_difference"] as Double
            filterText += "$maxDistanceFilter;"

        }

        adapter.filter.filter(filterText)

    }

    private fun getLocationUpdates() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationRequest = LocationRequest()
        locationRequest.interval = 100
        locationRequest.fastestInterval = 1000
        locationRequest.smallestDisplacement = 30F
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY //set according to your app function
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return

                if (locationResult.locations.isNotEmpty()) {
                    val location = locationResult.lastLocation
                    locationNow = location
                }
            }
        }
    }

    private fun startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                0
            )
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                0
            )
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null /* Looper */
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // For hiding the soft keyboard
    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}

