package com.xpyx.nokiahslvisualisation.fragments.vehicles

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.api.AlertViewModel
import com.xpyx.nokiahslvisualisation.api.AlertViewModelFactory
import com.xpyx.nokiahslvisualisation.api.MQTTViewModel
import com.xpyx.nokiahslvisualisation.api.MQTTViewModelFactory
import com.xpyx.nokiahslvisualisation.networking.mqttHelper.MqttHelper
import com.xpyx.nokiahslvisualisation.repository.AlertRepository
import com.xpyx.nokiahslvisualisation.repository.MQTTRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

        counter++
        textViewNumMsgs = view?.findViewById(R.id.textViewNumMsgs)!!
        textViewMsgPayload = view?.findViewById(R.id.textViewMsgPayload)!!
        ("Number of MQTT messages: $counter").also { textViewNumMsgs?.text = it }

        // Alert viewmodel
        val mqttRepository = MQTTRepository()
        val mqttViewModelFactory = MQTTViewModelFactory(mqttRepository)
        mMQTTViewModel = ViewModelProvider(this, mqttViewModelFactory).get(MQTTViewModel::class.java)
        mMQTTViewModel.getMQTTData(view.context)
        mMQTTViewModel.myMQTTResponse.observe(viewLifecycleOwner) { response ->
            Log.d("DBG", response.toString())
            textViewMsgPayload.text = response
        }
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

