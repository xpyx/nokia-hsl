package com.xpyx.nokiahslvisualisation.fragments.home

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.xpyx.nokiahslvisualisation.AlertsListQuery
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.networking.apolloClient.ApolloClient
import android.text.method.ScrollingMovementMethod
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.viewModels
import com.google.android.material.button.MaterialButton
import com.xpyx.nokiahslvisualisation.view.adapter.AlertAdapter
import com.xpyx.nokiahslvisualisation.view.state.ViewState
import com.xpyx.nokiahslvisualisation.viewmodel.AlertsViewModel
import com.xpyx.nokiahslvisualisation.databinding.FragmentHomeBinding

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val alertAdapter by lazy { AlertAdapter() }
    private val viewModel by viewModels<AlertsViewModel>()

//    private lateinit var mqttAndroidClient: MqttAndroidClient
//    private val scope = CoroutineScope(Dispatchers.IO)
//    private var counter: Int = 0
//    private lateinit var editText: EditText
//    private lateinit var busLineValue: Editable
//    private var topic: String = "/hfp/v2/journey/ongoing/vp/+/+/+/+/+/+/+/+/0/#"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.alerts.adapter = alertAdapter
        viewModel.queryAlertsList()
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.alertsList.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ViewState.Loading -> {
                    binding.alerts.visibility = View.GONE
                    binding.alertsFetchProgress.visibility = View.VISIBLE
                }
                is ViewState.Success -> {
                    if (response.value?.data?.alerts?.size == 0) {
                        alertAdapter.submitList(emptyList())
                        binding.alertsFetchProgress.visibility = View.GONE
                        binding.alerts.visibility = View.GONE
                    } else {
                        binding.alerts.visibility = View.VISIBLE
                    }
                    val results = response.value?.data?.alerts
                    alertAdapter.submitList(results)
                    binding.alertsFetchProgress.visibility = View.GONE
                }
                is ViewState.Error -> {
                    alertAdapter.submitList(emptyList())
                    binding.alertsFetchProgress.visibility = View.GONE
                    binding.alerts.visibility = View.GONE
                }
            }
        }
    }


//         Inflate the layout for this fragment
//        val view = inflater.inflate(R.layout.fragment_home, container, false)
//
//        // create a color state list programmatically
//        val states = arrayOf(
//            intArrayOf(android.R.attr.state_enabled), // enabled
//            intArrayOf(-android.R.attr.state_enabled) // disabled
//        )
//        val colors = intArrayOf(
//            Color.parseColor("#FF3700B3"), // enabled color
//            Color.parseColor("#E6E6FA") // disabled color
//        )
//        val colorStates = ColorStateList(states,colors)
//
//        // Get HSL Alerts
//        val btnAlerts = view.findViewById<Button>(R.id.btn_alerts)
//        btnAlerts.backgroundTintList = colorStates
//
//
//        // Get HSL Vehicle positions with MQTT
//        // Connect to HSL MQTT broker
//        connect(view.context)
//        val btnPositions = view.findViewById<Button>(R.id.btn_positions)
//        // set button background tint
//        btnPositions.backgroundTintList = colorStates
//        // initialize 'num msgs received' field in the view
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
//
//                return@OnEditorActionListener true
//            }
//            false
//        })
//
//        // Subscribe
//        btnPositions.setOnClickListener{
//            scope.launch {
//                topic = "/hfp/v2/journey/ongoing/vp/+/+/+/10$busLineValue/+/+/+/+/0/#"
//                Log.d("DBG", topic)
//                subscribe(topic)
//                receiveMessages()
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
//        // Unsubscribe
//        val btnStop = view.findViewById<Button>(R.id.btn_positions_stop)
//        btnStop.backgroundTintList = colorStates
//        (btnStop as MaterialButton).apply {
//            isEnabled = true
//            isClickable = true
//        }
//        btnStop.setOnClickListener{
//            scope.launch {
//                unSubscribe(topic)
//                runOnUiThread {
//                    (btnPositions as MaterialButton).apply {
//                        isEnabled = true
//                        isClickable = true
//                    }
//                }
//            }
//        }
//
//        return view
//    }

//    fun connect(applicationContext : Context) {
//        mqttAndroidClient = MqttAndroidClient ( context?.applicationContext,"tcp://mqtt.hsl.fi:1883","YOUR CLIENT ID" )
//        try {
//            val token = mqttAndroidClient.connect()
//            token.actionCallback = object : IMqttActionListener {
//                override fun onSuccess(asyncActionToken: IMqttToken)                        {
//                    Log.i("Connection", "success ")
//                    //connectionStatus = true
//                    // Give your callback on connection established here
//                }
//                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
//                    //connectionStatus = false
//                    Log.i("Connection", "failure")
//                    // Give your callback on connection failure here
//                    exception.printStackTrace()
//                }
//            }
//        } catch (e: MqttException) {
//            // Give your callback on connection failure here
//            e.printStackTrace()
//        }
//    }
//
//
//    fun subscribe(topic: String) {
//        val qos = 2 // Mention your qos value
//        try {
//            mqttAndroidClient.subscribe(topic, qos, null, object : IMqttActionListener {
//                override fun onSuccess(asyncActionToken: IMqttToken) {
//                    // Give your callback on Subscription here
//                    Log.i("Connection", "subscribe success ")
//                }
//                override fun onFailure(
//                    asyncActionToken: IMqttToken,
//                    exception: Throwable
//                ) {
//                    // Give your subscription failure callback here
//                    Log.i("Connection", "subscribe failure")
//
//                }
//            })
//        } catch (e: MqttException) {
//            // Give your subscription failure callback here
//        }
//    }
//
//    fun receiveMessages() {
//
//        mqttAndroidClient.setCallback(object : MqttCallback {
//            override fun connectionLost(cause: Throwable) {
//                //connectionStatus = false
//                // Give your callback on failure here
//            }
//            override fun messageArrived(topic: String, message: MqttMessage) {
//                val textViewNumMsgs = view?.findViewById<TextView>(R.id.textViewNumMsgs)
//                val textViewMsgPayload = view?.findViewById<TextView>(R.id.textViewMsgPayload)
//                try {
//                    val data = String(message.payload, charset("UTF-8"))
//                    // data is the desired received message
//                    // Give your callback on message received here
//                    Log.d("Connection", data)
//                    counter++
//                    runOnUiThread {
//                        textViewNumMsgs?.text = "Number of MQTT messages: " + counter.toString()
//                        textViewMsgPayload?.text = data
//                    }
//
//
//                } catch (e: Exception) {
//                    // Give your callback on error here
//                }
//            }
//            override fun deliveryComplete(token: IMqttDeliveryToken) {
//                // Acknowledgement on delivery complete
//            }
//        })
//    }
//
//    fun unSubscribe(topic: String) {
//        try {
//            val unsubToken = mqttAndroidClient.unsubscribe(topic)
//            unsubToken.actionCallback = object : IMqttActionListener {
//                override fun onSuccess(asyncActionToken: IMqttToken) {
//                    // Give your callback on unsubscribing here
//                    Log.i("Connection", "Unsubscribe success ")
//
//                }
//                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
//                    // Give your callback on failure here
//                }
//            }
//        } catch (e: MqttException) {
//            // Give your callback on failure here
//        }
//    }
//
//
//    fun Fragment?.runOnUiThread(action: () -> Unit) {
//        this ?: return
//        if (!isAdded) return // Fragment not attached to an Activity
//        activity?.runOnUiThread(action)
//    }
//
//    // For hiding the soft keyboard
//    private fun Fragment.hideKeyboard() {
//        view?.let { activity?.hideKeyboard(it) }
//    }
//
//    private fun Context.hideKeyboard(view: View) {
//        val inputMethodManager =
//            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
//    }

}