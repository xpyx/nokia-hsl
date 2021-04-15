package com.xpyx.nokiahslvisualisation.fragments.vehicles

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.sources.RasterSource
import com.mapbox.mapboxsdk.style.sources.TileSet
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.api.MQTTViewModel
import com.xpyx.nokiahslvisualisation.api.MQTTViewModelFactory
import com.xpyx.nokiahslvisualisation.model.mqtt.VehiclePosition
import com.xpyx.nokiahslvisualisation.repository.MQTTRepository
import kotlinx.coroutines.*
import java.util.*

class VehicleFragment : Fragment() {

    private var counter: Int = 0
    private lateinit var textViewNumMsgs: TextView
    private lateinit var textViewMsgPayload: TextView
    private lateinit var mMQTTViewModel: MQTTViewModel
    private var mapView: MapView? = null
    var buses = mutableListOf<Any>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        context?.let { Mapbox.getInstance(
                it.applicationContext,
                getString(R.string.mapbox_access_token)
        ) }
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_vehicles, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        mapView = view.findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync { mapboxMap ->
            // mapboxMap.getStyle {
            //  mapboxMap.setStyle(Style.MAPBOX_STREETS) {
            // Adding a raster source layer

            mapboxMap.setStyle(Style.Builder().fromUri("asset://local_style")) {

                val source = RasterSource(
                        "map",
                        TileSet( "https://cdn.digitransit.fi/map/v1/hsl-map" + "/{z}/{x}/{y}.png","mapbox://mapid")
                )




                val position = CameraPosition.Builder()
                        .target(LatLng(60.1733244, 24.9410248))
                        .zoom(10.0)
                        .tilt(20.0)
                        .build()
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1000)
                it.addSource(source)


            }
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
        var sumOF = object {
            var a = vehiclePosition.VP.oper
            var b = vehiclePosition.VP.veh
            var c = vehiclePosition.VP.lat
            var d = vehiclePosition.VP.long
        }

        mapView?.getMapAsync{mapbox ->

            mapbox.addMarker(
                    MarkerOptions()
                            .position(LatLng(sumOF.c.toDouble(), sumOF.d.toDouble(), 1.0))
            )
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

