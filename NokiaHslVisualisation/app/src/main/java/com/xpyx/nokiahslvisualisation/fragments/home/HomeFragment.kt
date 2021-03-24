package com.xpyx.nokiahslvisualisation.fragments.home

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
import com.google.android.material.snackbar.Snackbar
import com.xpyx.nokiahslvisualisation.Constants.Companion.HSL_MQTT_HOST
import com.xpyx.nokiahslvisualisation.GetAlertsQuery
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.networking.apolloClient.ApolloClient
import com.xpyx.nokiahslvisualisation.networking.mqttClient.MqttClientHelper
import android.text.method.ScrollingMovementMethod

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.util.*

class HomeFragment : Fragment() {



    private val mqttClient by lazy {
        MqttClientHelper(activity)
    }

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

        // Get HSL Vehicle positions
        val btnPositions = view.findViewById<Button>(R.id.btn_positions)
        setMqttCallBack(view)

        // initialize 'num msgs received' field in the view
        var textViewNumMsgs = view.findViewById<TextView>(R.id.textViewNumMsgs)
        textViewNumMsgs.text = "0"

        btnPositions.setOnClickListener{
            mqttClient.subscribe("/hfp/v2/journey/ongoing/vp/+/+/+/+/+/+/+/+/0/#")
        }
        return view
    }

    private fun setMqttCallBack(view: View) {
        var textViewNumMsgs = view.findViewById<TextView>(R.id.textViewNumMsgs)
        var textViewMsgPayload = view.findViewById<TextView>(R.id.textViewMsgPayload)
        textViewMsgPayload.movementMethod = ScrollingMovementMethod()

        mqttClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(b: Boolean, s: String) {
                val snackbarMsg = "Connected to host:\n'$HSL_MQTT_HOST'."
                Log.w("Debug", snackbarMsg)
                Snackbar.make(view.findViewById(android.R.id.content), snackbarMsg, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
            override fun connectionLost(throwable: Throwable) {
                val snackbarMsg = "Connection to host lost:\n'$HSL_MQTT_HOST'"
                Log.w("Debug", snackbarMsg)
                Snackbar.make(view.findViewById(android.R.id.content), snackbarMsg, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
            @Throws(Exception::class)
            override fun messageArrived(topic: String, mqttMessage: MqttMessage) {
                Log.w("Debug", "Message received from host '$HSL_MQTT_HOST': $mqttMessage")
                textViewNumMsgs.text = ("${textViewNumMsgs.text.toString().toInt() + 1}")
                val str: String = "------------"+ Calendar.getInstance().time +"-------------\n$mqttMessage\n${textViewMsgPayload.text}"
                textViewMsgPayload.text = str
            }

            override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {
                Log.w("Debug", "Message published to host '$HSL_MQTT_HOST'")
            }
        })
    }

    override fun onDestroy() {
        mqttClient.destroy()
        super.onDestroy()
    }
}