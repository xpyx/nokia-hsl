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
import com.google.android.material.button.MaterialButton
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.networking.mqttHelper.MqttHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VehicleFragment : Fragment() {

    private val scope = CoroutineScope(Dispatchers.IO)
    private var counter: Int = 0
    private lateinit var editText: EditText
    private lateinit var busLineValue: Editable
    private var topic: String = "/hfp/v2/journey/ongoing/vp/+/+/+/+/+/+/+/+/0/#"

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)
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

        // Get HSL Vehicle positions with MQTT
        // First init the helper class
        val mqtt = MqttHelper(this)

        // Connect to HSL MQTT broker
        mqtt.connect(view.context)
        val btnPositions = view.findViewById<Button>(R.id.btn_positions)

        // Set button background tint
        btnPositions.backgroundTintList = colorStates

        // Initialize 'num msgs received' field in the view
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

        // Subscribe button
        btnPositions.setOnClickListener {
            scope.launch {
                topic = "/hfp/v2/journey/ongoing/vp/+/+/+/10$busLineValue/+/+/+/+/0/#"
                Log.d("DBG", topic)
                mqtt.subscribe(topic)
                mqtt.receiveMessages()
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

        // Unsubscribe button
        val btnStop = view.findViewById<Button>(R.id.btn_positions_stop)
        btnStop.backgroundTintList = colorStates
        (btnStop as MaterialButton).apply {
            isEnabled = true
            isClickable = true
        }
        btnStop.setOnClickListener {
            scope.launch {
                mqtt.unSubscribe(topic)
                runOnUiThread {
                    (btnPositions as MaterialButton).apply {
                        isEnabled = true
                        isClickable = true
                    }
                }
            }
        }


    }

    fun updateUI(data: String) {
        counter++
        val textViewNumMsgs = view?.findViewById<TextView>(R.id.textViewNumMsgs)
        val textViewMsgPayload = view?.findViewById<TextView>(R.id.textViewMsgPayload)
        ("Number of MQTT messages: $counter").also { textViewNumMsgs?.text = it }
        textViewMsgPayload?.text = data
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

