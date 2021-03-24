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
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class HomeFragment : Fragment() {

    private lateinit var mqttAndroidClient: MqttAndroidClient


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
        textViewNumMsgs.text = "0"

        btnPositions.setOnClickListener{
//            mqttClient.subscribe("/hfp/v2/journey/ongoing/vp/+/+/+/+/+/+/+/+/0/#")
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
}