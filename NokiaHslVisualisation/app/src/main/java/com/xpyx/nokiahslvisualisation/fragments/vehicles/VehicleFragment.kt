package com.xpyx.nokiahslvisualisation.fragments.vehicles

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.sources.RasterSource
import com.mapbox.mapboxsdk.style.sources.TileSet
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.api.MQTTViewModel
import com.xpyx.nokiahslvisualisation.api.MQTTViewModelFactory
import com.xpyx.nokiahslvisualisation.model.mqtt.VehiclePosition
import com.xpyx.nokiahslvisualisation.repository.MQTTRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VehicleFragment : Fragment(), OnMapReadyCallback, PermissionsListener {

    private var counter: Int = 0
    private lateinit var textViewNumMsgs: TextView
    private lateinit var textViewMsgPayload: TextView
    private lateinit var mMQTTViewModel: MQTTViewModel

    private lateinit var listener: FragmentActivity
    private var mapView: MapView? = null
    private var permissionsManager: PermissionsManager = PermissionsManager(this)
    private lateinit var mapboxMap: MapboxMap


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            this.listener = context as FragmentActivity
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Mapbox.getInstance(
            listener, getString(R.string.mapbox_access_token)
        )

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_vehicles, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = view.findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)

        textViewNumMsgs = view.findViewById(R.id.textViewNumMsgs)
        textViewMsgPayload = view.findViewById(R.id.textViewMsgPayload)
        ("Number of MQTT messages: $counter").also { textViewNumMsgs.text = it }

        // MQTT viewmodel
        val mqttRepository = MQTTRepository()
        val mqttViewModelFactory = MQTTViewModelFactory(mqttRepository)
        mMQTTViewModel = ViewModelProvider(this, mqttViewModelFactory).get(MQTTViewModel::class.java)

        // Connect to MQTT broker, subscribe to topic and start receiving messages
        GlobalScope.launch {

            Log.d("DBG", "receiveMQTTMessages")

            receiveMQTTMessages()
        }
    }


    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.Builder().fromUri(
            "asset://local_style")) {

            val source = RasterSource(
                "map", TileSet("https://cdn.digitransit.fi/map/v1/hsl-map" + "/{z}/{x}/{y}.png", "mapbox://mapid")
            )

            val position = CameraPosition.Builder()
                .zoom(15.0)
                .tilt(20.0)
                .build()
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1000)
            it.addSource(source)
            enableLocationComponent(it)
        }
    }

    private fun enableLocationComponent(loadedMapStyle: Style) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(listener)) {

            // Create and customize the LocationComponent's options
            val customLocationComponentOptions = LocationComponentOptions.builder(listener)
                .trackingGesturesManagement(true)
                .pulseEnabled(true)
                .pulseColor(Color.GREEN)
                .pulseAlpha(.4f)
                .pulseInterpolator(BounceInterpolator())
                .build()

            val locationComponentActivationOptions = LocationComponentActivationOptions
                .builder(listener, loadedMapStyle)
                .locationComponentOptions(customLocationComponentOptions)
                .build()

            // Get an instance of the LocationComponent and then adjust its settings
            mapboxMap.locationComponent.apply {

                // Activate the LocationComponent with options
                activateLocationComponent(locationComponentActivationOptions)

                // Enable to make the LocationComponent visible
                if (context?.let {
                        ActivityCompat.checkSelfPermission(
                            it,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    } != PackageManager.PERMISSION_GRANTED && context?.let {
                        ActivityCompat.checkSelfPermission(
                            it,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    } != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                isLocationComponentEnabled = true

                // Set the LocationComponent's camera mode
                cameraMode = CameraMode.TRACKING_GPS_NORTH

                // Set the LocationComponent's render mode
                renderMode = RenderMode.NORMAL
            }
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(listener)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        Toast.makeText(listener, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationComponent(mapboxMap.style!!)
        } else {
            Toast.makeText(listener, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show()

        }
    }

    suspend fun receiveMQTTMessages() {
        val job = GlobalScope.launch(Dispatchers.IO) {
            view?.context?.let { mMQTTViewModel.connectMQTT(it) }
        }
        job.join()
        delay(1000) // wait for connection to be established
        mMQTTViewModel.receiveMessages(this)
    }

    fun updateUI(vehiclePosition: VehiclePosition) {
        var sumOF = object {
            var a = vehiclePosition.VP.oper
            var b = vehiclePosition.VP.veh
            var c = vehiclePosition.VP.lat
            var d = vehiclePosition.VP.long
        }

        mapView?.getMapAsync{ mapbox ->

            mapbox.addMarker(
                MarkerOptions()
                    .position(LatLng(sumOF.c.toDouble(), sumOF.d.toDouble(), 1.0))
            )
        }
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }


    // When exiting this fragment, unsubscribe from the topic
    override fun onPause() {
        super.onPause()
        mapView?.onPause()
        mMQTTViewModel.unsubscribe()
    }

    // When exiting this app, unsubscribe from the topic
    override fun onStop() {
        super.onStop()
        mapView?.onStop()
        mMQTTViewModel.destroy()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }
}