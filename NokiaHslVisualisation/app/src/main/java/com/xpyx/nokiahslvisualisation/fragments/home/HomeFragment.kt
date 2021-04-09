package com.xpyx.nokiahslvisualisation.fragments.home

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.google.android.material.button.MaterialButton
import com.xpyx.nokiahslvisualisation.AlertsListQuery
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.api.ApiViewModel
import com.xpyx.nokiahslvisualisation.api.ApiViewModelFactory
import com.xpyx.nokiahslvisualisation.data.AlertItem
import com.xpyx.nokiahslvisualisation.data.AlertItemViewModel
import com.xpyx.nokiahslvisualisation.data.DataTrafficItem
import com.xpyx.nokiahslvisualisation.data.TrafficItemViewModel
import com.xpyx.nokiahslvisualisation.model.traffic.TrafficData
import com.xpyx.nokiahslvisualisation.networking.apolloClient.ApolloClient
import com.xpyx.nokiahslvisualisation.networking.mqttHelper.MqttHelper
import com.xpyx.nokiahslvisualisation.repository.ApiRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.*


class HomeFragment : Fragment() {

//    private val scope = CoroutineScope(Dispatchers.IO)
    private var counter: Int = 0
//    private lateinit var editText: EditText
//    private lateinit var busLineValue: Editable
    private var topic: String = "/hfp/v2/journey/ongoing/vp/+/+/+/+/+/+/+/+/0/#"
    private lateinit var recyclerView: RecyclerView
    private lateinit var mAlertViewModel: AlertItemViewModel
    private lateinit var hereTrafficViewModel: ApiViewModel
    private lateinit var hereTrafficApiKey: String
    private lateinit var mTrafficViewModel: TrafficItemViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // RecyclerView init
        val adapter = context?.let { AlertListAdapter() }
        recyclerView = view.findViewById(R.id.alert_recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // ViewModel init
        mAlertViewModel = ViewModelProvider(this).get(AlertItemViewModel::class.java)
        mAlertViewModel.readAllData.observe(viewLifecycleOwner, { alerts ->
            adapter?.setData(alerts)
        })

        // Set up Room DB Traffic view model
        mTrafficViewModel = ViewModelProvider(this).get(TrafficItemViewModel::class.java)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Create a color state list programmatically for BUTTONS
        val states = arrayOf(
            intArrayOf(android.R.attr.state_enabled), // enabled
            intArrayOf(-android.R.attr.state_enabled) // disabled
        )
        val colors = intArrayOf(
            Color.parseColor("#FF3700B3"), // enabled color
            Color.parseColor("#E6E6FA") // disabled color
        )
        val colorStates = ColorStateList(states, colors)

        // Init Apollo and try to get response
        val apollo = ApolloClient()
        lifecycleScope.launchWhenResumed {
            val response = try {
                apollo.client.query(AlertsListQuery()).await()
            } catch (e: ApolloException) {
                Log.e("AlertList", "Failure", e)
                null
            }
            // When response successfull, pass list of alerts to adapter
            if (response != null && !response.hasErrors()) {
                insertToAlertDatabase(response)
            }
        }
//
//        // Get HSL Vehicle positions with MQTT
//        // First init the helper class
//        val mqtt = MqttHelper(this)
//
//        // Connect to HSL MQTT broker
//        mqtt.connect(view.context)
//        val btnPositions = view.findViewById<Button>(R.id.btn_positions)
//
//        // Set button background tint
//        btnPositions.backgroundTintList = colorStates
//
//        // Initialize 'num msgs received' field in the view
//        val textViewNumMsgs = view.findViewById<TextView>(R.id.textViewNumMsgs)
//        textViewNumMsgs.text = counter.toString()
//
//        // Get editText value
//        editText = view.findViewById(R.id.editText)
//        busLineValue = editText.text
//
//        // Listen to editText, clear editText and hide keyboard
//        editText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                return@OnEditorActionListener true
//            }
//            false
//        })
//
//        // Subscribe button
//        btnPositions.setOnClickListener {
//            scope.launch {
//                topic = "/hfp/v2/journey/ongoing/vp/+/+/+/10$busLineValue/+/+/+/+/0/#"
//                Log.d("DBG", topic)
//                mqtt.subscribe(topic)
//                mqtt.receiveMessages()
//                runOnUiThread {
//                    editText.text.clear()
//                    hideKeyboard()
//                    (it as MaterialButton).apply {
//                        isEnabled = false
//                        isClickable = false
//                    }
//                }
//            }
//        }
//
//        // Unsubscribe button
//        val btnStop = view.findViewById<Button>(R.id.btn_positions_stop)
//        btnStop.backgroundTintList = colorStates
//        (btnStop as MaterialButton).apply {
//            isEnabled = true
//            isClickable = true
//        }
//        btnStop.setOnClickListener {
//            scope.launch {
//                mqtt.unSubscribe(topic)
//                runOnUiThread {
//                    (btnPositions as MaterialButton).apply {
//                        isEnabled = true
//                        isClickable = true
//                    }
//                }
//            }
//        }

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
        val trafficItemList = response.body()!!.trafficDataTrafficItems
        if (trafficItemList != null) {
            for (item: com.xpyx.nokiahslvisualisation.model.traffic.TrafficItem in trafficItemList.trafficItem!!) {
                GlobalScope.launch(context = Dispatchers.IO) {
                    val traffic_item_id = item.trafficItemId
                    val traffic_item_status_short_desc = item.trafficItemStatusShortDesc
                    val traffic_item_type_desc = item.trafficItemTypeDesc
                    val start_time = item.trafficItemStartTime
                    val end_time = item.trafficItemEndTime
                    val criticality = item.trafficItemCriticality
                    val verified = item.trafficItemVerified
                    val rds_tmc_locations = item.trafficitemRDSTmclocations
                    val location = item.trafficItemLocation
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
                            location,
                            traffic_item_detail,
                            traffic_item_description
                    )

                    if (criticality != null) {
                        if (criticality.ityDescription.equals("critical")) {
                            mTrafficViewModel.addTrafficData(traffic)
                            Log.d("TRAFFIC", "Successfully added traffic item: $traffic_item_id")
                        }
                    }


                }
            }
        }
    }

    private fun insertToAlertDatabase(response: Response<AlertsListQuery.Data>) {

        var exists: Boolean

        Log.d("Traffic", response.toString())
        val alertItemList = response.data?.alerts()
        if (alertItemList != null) {
            for (item in alertItemList) {
                GlobalScope.launch(context = Dispatchers.IO) {
                    exists =
                        item.alertHeaderText()?.let { mAlertViewModel.checkIfExists(it) } == true
                    if (exists) {
                        Log.d("DBG", "Alert exists in database already")
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
                        Log.d("DBG", "Alert added to database")
                    }
                }
            }
        }
    }

    fun updateUI(data: String) {
//        counter++
//        val textViewNumMsgs = view?.findViewById<TextView>(R.id.textViewNumMsgs)
//        val textViewMsgPayload = view?.findViewById<TextView>(R.id.textViewMsgPayload)
//        ("Number of MQTT messages: $counter").also { textViewNumMsgs?.text = it }
//        textViewMsgPayload?.text = data
    }

    // For running on UI thread
    private fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return // Fragment not attached to an Activity
        activity?.runOnUiThread(action)
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
