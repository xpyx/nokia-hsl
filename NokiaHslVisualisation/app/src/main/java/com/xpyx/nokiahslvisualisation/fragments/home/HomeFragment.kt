package com.xpyx.nokiahslvisualisation.fragments.home

import android.content.Context
import android.os.Bundle
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
import com.xpyx.nokiahslvisualisation.GetAlertsQuery
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.networking.apolloClient.ApolloClient
import android.text.method.ScrollingMovementMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import kotlin.properties.Delegates

class HomeFragment : Fragment() {

    private lateinit var mqttAndroidClient: MqttAndroidClient
    private val scope = CoroutineScope(Dispatchers.IO)
    private var counter: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Get HSL Alerts
        val btnAlerts = view.findViewById<Button>(R.id.btn_alerts)
        val alertText = view.findViewById<TextView>(R.id.textView)
        alertText.movementMethod = ScrollingMovementMethod()
        val apollo = ApolloClient()
        btnAlerts.setOnClickListener{
            apollo.client.query(
                GetAlertsQuery.builder().build()
            ).enqueue(object : ApolloCall.Callback<GetAlertsQuery.Data>() {

                override fun onFailure(e: ApolloException) {
                    Log.d("DBG, on failure", e.localizedMessage ?: "Error")
                }

                override fun onResponse(response: Response<GetAlertsQuery.Data>) {
                    Log.d("DBG, on response", response.data.toString())
                    alertText.text = response.data.toString()

                }
            })
        }

        // Get HSL Vehicle positions with MQTT
        // Connect to HSL MQTT broker
        connect(view.context)
        val btnPositions = view.findViewById<Button>(R.id.btn_positions)
        // initialize 'num msgs received' field in the view
        val textViewNumMsgs = view.findViewById<TextView>(R.id.textViewNumMsgs)
        textViewNumMsgs.text = counter.toString()
        btnPositions.setOnClickListener{
            scope.launch {
                subscribe("/hfp/v2/journey/ongoing/vp/+/+/+/+/+/+/+/+/0/#")
                receiveMessages()
            }
        }

        val btnStop = view.findViewById<Button>(R.id.btn_positions_stop)
        btnStop.setOnClickListener{
            scope.launch {
                unSubscribe("/hfp/v2/journey/ongoing/vp/+/+/+/+/+/+/+/+/0/#")
            }
        }
        return view
    }

    fun connect(applicationContext : Context) {
        mqttAndroidClient = MqttAndroidClient ( context?.applicationContext,"tcp://mqtt.hsl.fi:1883","YOUR CLIENT ID" )
        try {
            val token = mqttAndroidClient.connect()
            token.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken)                        {
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
}