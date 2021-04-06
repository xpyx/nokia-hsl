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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.google.android.material.button.MaterialButton
import com.xpyx.nokiahslvisualisation.AlertsListQuery
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.networking.apolloClient.ApolloClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*


class HomeFragment : Fragment() {

    private lateinit var mqttAndroidClient: MqttAndroidClient
    private val scope = CoroutineScope(Dispatchers.IO)
    private var counter: Int = 0
    private lateinit var editText: EditText
    private lateinit var busLineValue: Editable
    private var topic: String = "/hfp/v2/journey/ongoing/vp/+/+/+/+/+/+/+/+/0/#"
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

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


        // RecyclerView init
        recyclerView = view.findViewById(R.id.alert_recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Init Apollo and try to get response
        val apollo = ApolloClient()
        lifecycleScope.launchWhenResumed {
            val response = try {
                apollo.client.query(AlertsListQuery()).await()
            } catch (e: ApolloException) {
                Log.d("AlertList", "Failure", e)
                null
            }

            // When response successfull, pass list of alerts to adapter
            val alerts = response?.data?.alerts()?.filterNotNull()
            if (alerts != null && !response.hasErrors()) {
                recyclerView.adapter =
                    AlertListAdapter(alerts as MutableList<AlertsListQuery.Alert>)
            }
        }


        // Get HSL Vehicle positions with MQTT
        // Connect to HSL MQTT broker
        connect(view.context)
        val btnPositions = view.findViewById<Button>(R.id.btn_positions)
        // set button background tint
        btnPositions.backgroundTintList = colorStates
        // initialize 'num msgs received' field in the view
        val textViewNumMsgs = view.findViewById<TextView>(R.id.textViewNumMsgs)
        textViewNumMsgs.text = counter.toString()

        // Get editText value
        editText = view.findViewById(R.id.editText)
        busLineValue = editText.text

        // Listen to editText, clear editText and hide keyboard
        editText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                return@OnEditorActionListener true
            }
            false
        })

        // Subscribe
        btnPositions.setOnClickListener {
            scope.launch {
                topic = "/hfp/v2/journey/ongoing/vp/+/+/+/10$busLineValue/+/+/+/+/0/#"
                Log.d("DBG", topic)
                subscribe(topic)
                receiveMessages()
                runOnUiThread {
                    editText.text.clear()
                    hideKeyboard()
                    (it as MaterialButton).apply {
                        isEnabled = false
                        isClickable = false
                    }
                }
            }
        }

        // Unsubscribe
        val btnStop = view.findViewById<Button>(R.id.btn_positions_stop)
        btnStop.backgroundTintList = colorStates
        (btnStop as MaterialButton).apply {
            isEnabled = true
            isClickable = true
        }
        btnStop.setOnClickListener {
            scope.launch {
                unSubscribe(topic)
                runOnUiThread {
                    (btnPositions as MaterialButton).apply {
                        isEnabled = true
                        isClickable = true
                    }
                }
            }
        }

        return view
    }

    fun connect(applicationContext: Context) {
        mqttAndroidClient = MqttAndroidClient(
            applicationContext,
            "tcp://mqtt.hsl.fi:1883",
            "YOUR CLIENT ID"
        )
        try {
            val token = mqttAndroidClient.connect()
            token.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Log.i("Connection", "success ")
                    //connectionStatus = true
                    // Give your callback on connection established here
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    //connectionStatus = false
                    Log.i("Connection", "failure")
                    // Give your callback on connection failure here
                    exception.printStackTrace()
                }
            }
        } catch (e: MqttException) {
            // Give your callback on connection failure here
            e.printStackTrace()
        }
    }


    fun subscribe(topic: String) {
        val qos = 2 // Mention your qos value
        try {
            mqttAndroidClient.subscribe(topic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    // Give your callback on Subscription here
                    Log.i("Connection", "subscribe success ")
                }

                override fun onFailure(
                    asyncActionToken: IMqttToken,
                    exception: Throwable
                ) {
                    // Give your subscription failure callback here
                    Log.i("Connection", "subscribe failure")

                }
            })
        } catch (e: MqttException) {
            // Give your subscription failure callback here
        }
    }

    fun receiveMessages() {

        mqttAndroidClient.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable) {
                //connectionStatus = false
                // Give your callback on failure here
            }

            override fun messageArrived(topic: String, message: MqttMessage) {
                val textViewNumMsgs = view?.findViewById<TextView>(R.id.textViewNumMsgs)
                val textViewMsgPayload = view?.findViewById<TextView>(R.id.textViewMsgPayload)
                try {
                    val data = String(message.payload, charset("UTF-8"))
                    // data is the desired received message
                    // Give your callback on message received here
                    Log.d("Connection", data)
                    counter++
                    runOnUiThread {
                        textViewNumMsgs?.text = "Number of MQTT messages: " + counter.toString()
                        textViewMsgPayload?.text = data
                    }


                } catch (e: Exception) {
                    // Give your callback on error here
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
                // Acknowledgement on delivery complete
            }
        })
    }

    fun unSubscribe(topic: String) {
        try {
            val unsubToken = mqttAndroidClient.unsubscribe(topic)
            unsubToken.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    // Give your callback on unsubscribing here
                    Log.i("Connection", "Unsubscribe success ")

                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    // Give your callback on failure here
                }
            }
        } catch (e: MqttException) {
            // Give your callback on failure here
        }
    }


    fun Fragment?.runOnUiThread(action: () -> Unit) {
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