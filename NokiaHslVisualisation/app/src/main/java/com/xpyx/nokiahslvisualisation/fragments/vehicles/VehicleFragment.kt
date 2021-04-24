package com.xpyx.nokiahslvisualisation.fragments.vehicles

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.BounceInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.FrameLayout
import android.widget.TextView
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
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager
import com.mapbox.mapboxsdk.style.sources.RasterSource
import com.mapbox.mapboxsdk.style.sources.TileSet
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.api.MQTTViewModel
import com.xpyx.nokiahslvisualisation.api.MQTTViewModelFactory
import com.xpyx.nokiahslvisualisation.api.StopTimesViewModel
import com.xpyx.nokiahslvisualisation.api.StopTimesViewModelFactory
import com.xpyx.nokiahslvisualisation.model.late.Late
import com.xpyx.nokiahslvisualisation.model.mqtt.VehiclePosition
import com.xpyx.nokiahslvisualisation.networking.mqttHelper.TopicSetter
import com.xpyx.nokiahslvisualisation.repository.MQTTRepository
import com.xpyx.nokiahslvisualisation.repository.StopTimesRepository
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_vehicles.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate


class VehicleFragment : Fragment(), OnMapReadyCallback, PermissionsListener {

    private lateinit var mMQTTViewModel: MQTTViewModel
    private lateinit var mStopTimesApiViewModel: StopTimesViewModel
    private lateinit var listener: FragmentActivity
    private var mapView: MapView? = null
    private var permissionsManager: PermissionsManager = PermissionsManager(this)
    private lateinit var mapboxMap: MapboxMap
    private lateinit var editText: EditText
    private lateinit var editTextBusses: EditText
    private lateinit var editTextValue: Editable
    private lateinit var editTextValueBusses: Editable
    private lateinit var spinner: ProgressBar
    private var lateTime: Int = 0
    var topic: String = ""
    var busline: String = ""
    var buslineTopic: String = ""
    var positions = mutableMapOf<String, VehiclePosition>()

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

        // MQTT viewmodel
        val mqttRepository = MQTTRepository()
        val mqttViewModelFactory = MQTTViewModelFactory(mqttRepository)
        mMQTTViewModel =
            ViewModelProvider(this, mqttViewModelFactory).get(MQTTViewModel::class.java)

        // Connect to MQTT broker, subscribe to topic and start receiving messages
        GlobalScope.launch {
            connectMQTT()
        }

        val topicSetter = TopicSetter()

        // Checkboxes
        val listOfCheckBoxes = listOf<CheckBox>(
            bus,
            tram
        )

        listOfCheckBoxes.forEach {
            val name = it.text.toString()
            it.setOnCheckedChangeListener { _, _ ->
                if (it.isChecked) {
                    // subscribe to topic containing only trams or busses
                    if (name == "Show only trams") {
                        topic = "/hfp/v2/journey/ongoing/vp/tram/#"
                        Log.d("DBG topic", topic)
                        mMQTTViewModel.subscribe(topic)
                    } else if (name == "Show only busses") {
                        topic = "/hfp/v2/journey/ongoing/vp/bus/#"
                        Log.d("DBG topic", topic)
                        mMQTTViewModel.subscribe(topic)
                    }
                } else {
                    // clear map markers
                    mMQTTViewModel.unsubscribe(topic)
                }
            }
        }


        // Set up editText for vehicle late time
        editText = view.findViewById(R.id.edit_text_late_time)
        editText.inputType = InputType.TYPE_CLASS_NUMBER
        editTextValue = editText.text

        // Spinner set up and hide
        spinner = view.findViewById(R.id.spinner)
        spinner.visibility = View.GONE

        // Listen to editText and on complete set lateTime,
        // clear editText and hide keyboard
        editText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                // assign late time
                setLateTime(editTextValue.toString().toInt())

                // Show spinner
                spinner.visibility = View.VISIBLE

                // Unsubscribe from previous topics
                mMQTTViewModel.unsubscribe("/hfp/v2/journey/ongoing/#")

                // Get stoptimes
                mStopTimesApiViewModel.getStopTimesData()
                mStopTimesApiViewModel.myStopTimesApiResponse.observe(
                    viewLifecycleOwner,
                    { response ->
                        if (response != null) {

                            // Hide spinner
                            spinner.visibility = View.GONE

                            // The stoptimes data is here, iterate over the whole response
                            response.data?.stops()?.forEach {

                                if (it.stoptimesForPatterns()?.isNotEmpty() == true) {

                                    val routeId =
                                        it.stoptimesForPatterns()?.get(0)?.stoptimes()?.get(0)
                                            ?.trip()?.route()
                                            ?.gtfsId()?.substring(
                                                4
                                            )

                                    val transportMode =
                                        it.stoptimesForPatterns()?.get(0)?.stoptimes()?.get(0)
                                            ?.trip()?.route()
                                            ?.mode()

                                    val arrivalDelay =
                                        it.stoptimesForPatterns()?.get(0)?.stoptimes()?.get(0)
                                            ?.arrivalDelay()

                                    var directionId =
                                        it.stoptimesForPatterns()?.get(0)?.stoptimes()?.get(0)
                                            ?.trip()
                                            ?.directionId()

                                    // Change direction id according to instructions. Also note if null, then -> "+"
                                    if (directionId.equals("0")) {
                                        directionId = "1"
                                    } else if (directionId.equals("1")) {
                                        directionId = "2"
                                    }

                                    if (arrivalDelay != null) {
                                        if (arrivalDelay > lateTime) {

                                            val late = Late(
                                                routeId,
                                                transportMode.toString().toLowerCase(),
                                                arrivalDelay.toString(),
                                                directionId
                                            )

                                            val topicString = topicSetter.setTopic(late)
                                            Log.d("DBG topicString", topicString)
                                            mMQTTViewModel.subscribe(topicString)

                                            Log.d("DBG late vehicles", "routeId         : $routeId")
                                            Log.d("DBG late vehicles", "transportMode   : $transportMode")
                                            Log.d("DBG late vehicles","arrivalDelay     : $arrivalDelay")
                                            Log.d("DBG late vehicles","directionId      : $directionId")
                                            Log.d("DBG late vehicles","---------------------------")
                                        }
                                    }
                                }
                            }
                        } else {
                            // Log.d("DBG", response.toString())
                        }
                    })

                editText.text.clear()
                hideKeyboard()
                view.clearFocus()
                return@OnEditorActionListener true
            }
            false
        })

        // Set up editText for searching a bus line
        editTextBusses = view.findViewById(R.id.edit_text_bus_line)
        editTextBusses.inputType = InputType.TYPE_CLASS_NUMBER
        editTextValueBusses = editTextBusses.text

        // Listen to editTextBusses and on complete set busline,
        // clear editText and hide keyboard
        editTextBusses.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                // assign busline
                busline = (editTextValueBusses.toString())

                // Show spinner
                spinner.visibility = View.VISIBLE

                // Unsubscribe from previous topics
                mMQTTViewModel.unsubscribe(buslineTopic)

                buslineTopic = "/hfp/v2/journey/+/vp/+/+/+/10$busline/#"
                mMQTTViewModel.subscribe(buslineTopic)

                editTextBusses.text.clear()
                hideKeyboard()
                view.clearFocus()
                return@OnEditorActionListener true
            }
            false
        })

        // StopTimes API viewmodel
        val stopTimesRepository = StopTimesRepository()
        val stopTimesViewModelFactory = StopTimesViewModelFactory(stopTimesRepository)
        mStopTimesApiViewModel =
            ViewModelProvider(this, stopTimesViewModelFactory).get(StopTimesViewModel::class.java)

        // MAP
        mapView = view.findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)

    }


    private fun setLateTime(seconds: Int): Boolean {
        lateTime = seconds
        return true
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(
            Style.Builder().fromUri(
                "asset://local_style"
            )
        ) {

            val source = RasterSource(
                "map",
                TileSet(
                    "https://cdn.digitransit.fi/map/v1/hsl-map" + "/{z}/{x}/{y}.png",
                    "mapbox://mapid"
                )
            )

            val position = CameraPosition.Builder()
                .zoom(25.0)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        Toast.makeText(listener, R.string.user_location_permission_explanation, Toast.LENGTH_LONG)
            .show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationComponent(mapboxMap.style!!)
        } else {
            Toast.makeText(
                listener,
                R.string.user_location_permission_not_granted,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    suspend fun connectMQTT() {
        val job = GlobalScope.launch(Dispatchers.IO) {
            view?.context?.let { mMQTTViewModel.connectMQTT(it) }
        }
        job.join()
        delay(1000) // wait for connection to be established
        mMQTTViewModel.receiveMessages(this)
    }

    fun updateUI(vehiclePosition: VehiclePosition) {
        spinner.visibility = View.GONE

        // If positions map contains the vehicle, just update it's info
        if (positions.containsKey(vehiclePosition.VP.oper.toString()+
                    vehiclePosition.VP.veh.toString())) {

        // If positions map doesn't contain the vehicle, add it there
        } else {
            positions[vehiclePosition.VP.oper.toString()+
                    vehiclePosition.VP.veh.toString()] = vehiclePosition
        }

        Log.d("DBG positions map", positions.toString())

        val title = "Line: ${vehiclePosition.VP.desi}"

        val snippet = """ 
            Operator: ${vehiclePosition.VP.oper}
            Vehicle: ${vehiclePosition.VP.veh}   
            
            Speed: ${vehiclePosition.VP.spd} m/s
            Heading: ${vehiclePosition.VP.hdg} degrees
            Acceleration: ${vehiclePosition.VP.acc} m/s2
            Odometer reading: ${vehiclePosition.VP.odo} m
            Offset from timetable: ${vehiclePosition.VP.dl} seconds
        """.trimIndent()

        val sumOF = object {
            var a = vehiclePosition.VP.oper
            var b = vehiclePosition.VP.veh
            var c = vehiclePosition.VP.lat
            var d = vehiclePosition.VP.long
        }



        mapView?.getMapAsync { mapbox ->

/*            // Initialize the MarkerViewManager
            var markerViewManager = MarkerViewManager(mapView, mapboxMap)

            // Use an XML layout to create a View object
            val customView: View = LayoutInflater.from(context).inflate(
                R.layout.marker, null
            )
            customView.layoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)

            // Set the View's TextViews with content
            val titleTextView = customView.findViewById<TextView>(R.id.marker_window_title)
            titleTextView.text = vehiclePosition.VP.oper.toString()

            val snippetTextView = customView.findViewById<TextView>(R.id.marker_window_snippet)
            snippetTextView.text = description

            // Use the View to create a MarkerView which will eventually be given to
            // the plugin's MarkerViewManager class

            // Use the View to create a MarkerView which will eventually be given to
            // the plugin's MarkerViewManager class
            var markerView = MarkerView(LatLng(sumOF.c.toDouble(), sumOF.d.toDouble()), customView)
            markerViewManager.addMarker(markerView)*/

            val mark = mapbox.addMarker(
                MarkerOptions()
                    .position(LatLng(sumOF.c.toDouble(), sumOF.d.toDouble(), 1.0))
                    .title(title)
                    .snippet(snippet)
            )

            Handler().postDelayed(Runnable { mapboxMap.removeMarker(mark) }, 4000)
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
        mMQTTViewModel.unsubscribe("#")
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