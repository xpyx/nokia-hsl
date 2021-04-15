package com.xpyx.nokiahslvisualisation.fragments.vehicles

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.api.MQTTViewModel
import com.xpyx.nokiahslvisualisation.api.MQTTViewModelFactory
import com.xpyx.nokiahslvisualisation.model.mqtt.VehiclePosition
import com.xpyx.nokiahslvisualisation.repository.MQTTRepository
import kotlinx.coroutines.*

class VehicleFragment : Fragment() {

    private var counter: Int = 0
    private lateinit var textViewNumMsgs: TextView
    private lateinit var textViewMsgPayload: TextView
    private lateinit var mMQTTViewModel: MQTTViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_vehicles, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Shared preferences
        val sharedPref = activity?.getSharedPreferences(
            getString(R.string.alert_filtering_preferences), Context.MODE_PRIVATE)

        textViewNumMsgs = view.findViewById(R.id.textViewNumMsgs)
        textViewMsgPayload = view.findViewById(R.id.textViewMsgPayload)
        ("Number of MQTT messages: $counter").also { textViewNumMsgs.text = it }

        // MQTT viewmodel
        val mqttRepository = MQTTRepository()
        val mqttViewModelFactory = MQTTViewModelFactory(mqttRepository)
        mMQTTViewModel = ViewModelProvider(this, mqttViewModelFactory).get(MQTTViewModel::class.java)

        // Connect to MQTT broker, subscribe to topic and start receiving messages
        GlobalScope.launch {
            receiveMQTTMessages()
        }
    }

    suspend fun receiveMQTTMessages() {
        val job = GlobalScope.launch(Dispatchers.IO) {
            view?.context?.let { mMQTTViewModel.connectMQTT(it) }
        }
        job.join()
        delay(1000)
        mMQTTViewModel.receiveMessages(this)
    }

    fun updateUI(vehiclePosition: VehiclePosition) {
        textViewMsgPayload.text = vehiclePosition.toString()
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

    // When exiting this fragment, unsubscribe from the topic
    override fun onPause() {
        super.onPause()
        mMQTTViewModel.unsubscribe()
    }

    // When exiting this app, unsubscribe from the topic
    override fun onStop() {
        super.onStop()
        mMQTTViewModel.destroy()
    }
}

