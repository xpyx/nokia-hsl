package com.xpyx.nokiahslvisualisation.fragments.map

import android.app.Activity
import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.InputType
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.google.ar.core.HitResult
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.api.MQTTViewModel
import com.xpyx.nokiahslvisualisation.api.MQTTViewModelFactory
import com.xpyx.nokiahslvisualisation.api.StopTimesViewModel
import com.xpyx.nokiahslvisualisation.api.StopTimesViewModelFactory
import com.xpyx.nokiahslvisualisation.data.DataTrafficItem
import com.xpyx.nokiahslvisualisation.data.TrafficItemViewModel
import com.xpyx.nokiahslvisualisation.model.late.Late
import com.xpyx.nokiahslvisualisation.model.mqtt.VehiclePosition
import com.xpyx.nokiahslvisualisation.networking.mqttHelper.TopicSetter
import com.xpyx.nokiahslvisualisation.repository.MQTTRepository
import com.xpyx.nokiahslvisualisation.repository.StopTimesRepository
import com.xpyx.nokiahslvisualisation.utils.LineToRoute
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_map.btn_clear
import kotlinx.android.synthetic.main.fragment_map.bus
import kotlinx.android.synthetic.main.fragment_map.tram
import kotlinx.android.synthetic.main.fragment_map.vehicle_count
import kotlinx.android.synthetic.main.fragment_vehicles.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker


class MapFragment : Fragment(), SeekBar.OnSeekBarChangeListener {

    private lateinit var arFrag: ArFragment
    private var viewRenderable: ViewRenderable? = null
    private var mTransparencyBar: SeekBar? = null
    private var mHeightBar: SeekBar? = null
    private var mWidthBar: SeekBar? = null

    private lateinit var trafficList : List<DataTrafficItem>
    private lateinit var mTrafficViewModel: TrafficItemViewModel

    private lateinit var map: org.osmdroid.views.MapView
    private var transparency = 0f
    private var height = 900
    private var width = 900
    private lateinit var apa: LinearLayout

    // From vehicles
    private lateinit var mMQTTViewModel: MQTTViewModel
    private lateinit var mStopTimesApiViewModel: StopTimesViewModel
//    private lateinit var listener: FragmentActivity
    private lateinit var editText: EditText
    private lateinit var editTextBusses: EditText
    private lateinit var editTextValue: Editable
    private lateinit var editTextValueLine: Editable
    private lateinit var spinner: ProgressBar
    private var lateTime: Int = 0
    var topic: String = ""
    var lineToSearch: String = ""
    var positions = mutableMapOf<String, VehiclePosition>()
    var listOfTopics = mutableListOf<String>()


//      THIS IS FROM VEHICLES
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (context is Activity) {
//            this.listener = context as FragmentActivity
//        }
//    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ctx = requireActivity().applicationContext

        // From vehicles starts here ------------------------------------------------->

        // Clear button
        btn_clear.setOnClickListener {
            if (topic.isNotEmpty()) {
                mMQTTViewModel.unsubscribe(topic)
                if (tram.isChecked) {
                    tram.toggle()
                } else if (bus.isChecked) {
                    bus.toggle()
                }

            }

            if (!listOfTopics.isEmpty()) {
                listOfTopics.forEach {
                    mMQTTViewModel.unsubscribe(it)
                }
            }

            Handler().postDelayed(Runnable { vehicle_count.visibility = View.GONE }, 1500)
        }

        // Hide vehicle count textviews
        vehicle_count.visibility = View.GONE

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
        val lineToRoute = LineToRoute()

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
                        // Clear positions map
                        positions.clear()
                        // First clear other topics
                        mMQTTViewModel.unsubscribe(topic)
                        // Set topic and subscribe
                        topic = "/hfp/v2/journey/ongoing/vp/tram/#"
                        mMQTTViewModel.subscribe(topic)
                    } else if (name == "Show only busses") {
                        // Clear positions map
                        positions.clear()
                        // First clear other topics
                        mMQTTViewModel.unsubscribe(topic)
                        // Set topic and subscribe
                        topic = "/hfp/v2/journey/ongoing/vp/bus/#"
                        mMQTTViewModel.subscribe(topic)
                    }
                } else {
                    // Clear other topics
                    mMQTTViewModel.unsubscribe(topic)

                    // Hide the vehicle count textview after 2,5 seconds after unchecking
                    Handler().postDelayed(Runnable { vehicle_count.visibility = View.GONE }, 1500)
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

                // Clear positions map
                positions.clear()

                // assign late time
                setLateTime(editTextValue.toString().toInt())

                // Show spinner
                spinner.visibility = View.VISIBLE

                // Unsubscribe from previous topics
                mMQTTViewModel.unsubscribe("/hfp/v2/journey/ongoing/vp/#")

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

                                            topic = topicSetter.setTopic(late)
                                            listOfTopics.add(topic)
                                            mMQTTViewModel.subscribe(topic)

                                            Log.d("DBG late vehicles",
                                                """routeId          : $routeId
                                                transportMode    : $transportMode
                                                arrivalDelay     : $arrivalDelay
                                                directionId      : $directionId
                                                ---------------------------""".trimIndent())
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
        editTextValueLine = editTextBusses.text


        // Listen to editTextBusses and on complete set busline,
        // clear editText and hide keyboard
        editTextBusses.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                // Clear positions map
                positions.clear()

                // Convert line to route ID
                val line = (editTextValueLine.toString())
                lineToSearch = lineToRoute.convertLineToRoute(line)

                // Show spinner
                spinner.visibility = View.VISIBLE

                // Unsubscribe from previous topics
                mMQTTViewModel.unsubscribe(topic)

                topic = "/hfp/v2/journey/+/vp/+/+/+/$lineToSearch/#"
                mMQTTViewModel.subscribe(topic)

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


        // From vehicles ends here ---------------------------------------------------->

        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(ctx, androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext()))
        setSeekBars()


        mTrafficViewModel = ViewModelProvider(this).get(TrafficItemViewModel::class.java)

        mTrafficViewModel.readAllData.observe(viewLifecycleOwner, { traffic ->
            trafficList = traffic

        })



        arFrag = childFragmentManager.findFragmentById(
            R.id.sceneform_fragment
        ) as ArFragment

        ViewRenderable.builder()
            .setView(requireContext(), R.layout.mymap)
            .build()
            .thenAcceptAsync {
                //load apa as container of map to get size control

                apa = it.view as LinearLayout
                map = it.view.findViewById<org.osmdroid.views.MapView>(R.id.map)
                map.setTileSource(TileSourceFactory.MAPNIK)
                map.setBuiltInZoomControls(true)
                map.setMultiTouchControls(true)
                map.minZoomLevel = 10.0
                map.zoomController
                map.controller.setCenter(GeoPoint(60.17, 24.95))
                viewRenderable = it

            }


        arFrag.setOnTapArPlaneListener { hitResult: HitResult?, _, _ ->
            if (viewRenderable == null) {
                return@setOnTapArPlaneListener
            }

            arFrag.getPlaneDiscoveryController().hide();
            arFrag.getPlaneDiscoveryController().setInstructionView(null);
            arFrag.getArSceneView().getPlaneRenderer().setEnabled(false);
            //Creates a new anchor at the hit location
            val anchor = hitResult!!.createAnchor()
            //Creates a new anchorNode attaching it to anchor
            val anchorNode = AnchorNode(anchor)
            // Add anchorNode as root scene node's child
            anchorNode.setParent(arFrag.arSceneView.scene)
            // Can be selected, rotated...
            val viewNode = TransformableNode(arFrag.transformationSystem)
            // Add viewNode as anchorNode's child
            viewNode.setParent(anchorNode)
            viewNode.renderable = viewRenderable
            // Sets this as the selected node in the TransformationSystem
            mTransparencyBar?.visibility = View.VISIBLE
            mHeightBar?.visibility = View.VISIBLE
            mWidthBar?.visibility = View.VISIBLE

            viewNode.select()

            setMapMarkers()

        }

        mTransparencyBar?.setOnSeekBarChangeListener(this)
        mHeightBar?.setOnSeekBarChangeListener(this)
        mWidthBar?.setOnSeekBarChangeListener(this)
        refresh_map_button.setOnClickListener { map.setTileSource(TileSourceFactory.MAPNIK) }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        when (seekBar){
            mTransparencyBar -> {
                transparency = progress.toFloat() / TRANSPARENCY_MAX.toFloat()
                map.alpha = transparency
            }
            mHeightBar -> {
                height = progress
                var params = apa.layoutParams
                params.height = height
                apa.layoutParams = params
            }
            mWidthBar -> {
                width = progress
                var params = apa.layoutParams
                params.width = width
                apa.layoutParams = params
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}

    companion object {
        private const val TRANSPARENCY_MAX = 100
        private const val HEIGHT_MAX = 2475
        private const val HEIGHT_MIN = 200
        private const val WIDTH_MAX = 2475
        private const val WIDTH_MIN = 200
    }

    fun setSeekBars(){
        mTransparencyBar = activity?.findViewById(R.id.transparencySeekBar)
        mTransparencyBar?.max = TRANSPARENCY_MAX
        mTransparencyBar?.progress = 100

        mHeightBar = activity?.findViewById(R.id.heightSeekBar)
        mHeightBar?.max = HEIGHT_MAX
        mHeightBar?.min = HEIGHT_MIN
        mHeightBar?.progress = 2475

        mWidthBar = activity?.findViewById(R.id.widthSeekBar)
        mWidthBar?.max = WIDTH_MAX
        mWidthBar?.min = WIDTH_MIN
        mWidthBar?.progress = 2475

    }
    fun setMapMarkers(){
        var lathigh = 0.0
        var lgthigh = 0.0
        var latlow = 90.0
        var lgtlow = 90.0


        for (item in trafficList){


        val trafficItemLatitude = item.location?.locationGeoloc?.geolocOrigin?.geolocLocationLatitude!!
        val trafficItemLongitude = item.location.locationGeoloc.geolocOrigin.geolocLocationLongitude!!
            val trafficTitle = item.traffic_item_type_desc
            val defined = item.location.locationDefined

            val locationText: String = if (defined != null) {
                if (defined.definedOrigin?.definedLocationDirection != null) {
                    "From: ${defined.definedOrigin.definedLocationRoadway?.directionClassDescription?.get(0)?.trafficItemDescriptionElementValue} towards ${defined.definedOrigin.definedLocationDirection.directionClassDescription?.get(0)?.trafficItemDescriptionElementValue} from ${defined.definedOrigin.definedLocationPoint?.directionClassDescription?.get(0)?.trafficItemDescriptionElementValue} to ${defined.definedTo?.definedLocationPoint?.directionClassDescription?.get(0)?.trafficItemDescriptionElementValue}"
                } else {
                    "From: ${defined.definedOrigin?.definedLocationRoadway?.directionClassDescription?.get(0)?.trafficItemDescriptionElementValue} from ${defined.definedOrigin?.definedLocationPoint?.directionClassDescription?.get(0)?.trafficItemDescriptionElementValue} to ${defined.definedTo?.definedLocationPoint?.directionClassDescription?.get(0)?.trafficItemDescriptionElementValue}"
                }
            } else {
                val address = getAddress(trafficItemLatitude, trafficItemLongitude)
                "Problem: ${item.trafficItemDescriptionElement?.get(0)?.trafficItemDescriptionElementValue} Location: $address"
            }
            lathigh = Math.max(trafficItemLatitude, lathigh)
            latlow = Math.min(trafficItemLatitude, latlow)
            lgthigh = Math.max(trafficItemLongitude, lgthigh)
            lgtlow = Math.min(trafficItemLongitude, lgtlow)


        addMarker(trafficItemLatitude, trafficItemLongitude, trafficTitle, locationText)
    }
        val b= BoundingBox(lathigh, lgthigh, latlow, lgtlow)
        map.post(Runnable {
            map.zoomToBoundingBox(
                b, true,100
            )
            map.minZoomLevel = 10.0
        })

        //map.invalidate()
    }
    private fun addMarker(lat: Double, lon: Double, trafficTitle: String?, locationText: String) {

        // Custom markers if needed

        val marker = Marker(map)
        marker.position = GeoPoint(lat, lon)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        // Icon made by Freepik from www.flaticon.com
        marker.icon = ResourcesCompat.getDrawable(resources,R.drawable.map_warning_icon, requireContext().theme)
        marker.title = "$trafficTitle"
        marker.subDescription = "$locationText"
        //marker.setInfoWindow(null)

        map.overlays.add(marker)

    }

    private fun getAddress(lat: Double?, lng: Double?): String {
        val geoCoder = Geocoder(context)
        val list = geoCoder.getFromLocation(lat ?: 0.0, lng ?: 0.0, 1)
        return list[0].getAddressLine(0)
    }

    // These are from Vehicles

    fun updateUI(vehiclePosition: VehiclePosition) {
        spinner.visibility = View.GONE

        // If positions map contains the vehicle, just update it's info
        if (positions.containsKey(
                vehiclePosition.VP.oper.toString() +
                        vehiclePosition.VP.veh.toString()
            )
        ) {

            // If positions map doesn't contain the vehicle, add it there
        } else {
            positions[vehiclePosition.VP.oper.toString() +
                    vehiclePosition.VP.veh.toString()] = vehiclePosition
        }

        vehicle_count.visibility = View.VISIBLE
        vehicle_count.text = context?.getString(R.string.vehicle_count, positions.size.toString())

        // Details of the vehicle on the marker
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


        // TODO: Change this to OSMDroid map marker
    //        mapView?.getMapAsync { mapbox ->
//
//            val mark = mapbox.addMarker(
//                MarkerOptions()
//                    .position(
//                        LatLng(
//                            vehiclePosition.VP.lat.toDouble(),
//                            vehiclePosition.VP.long.toDouble(),
//                            1.0
//                        )
//                    )
//                    .title(title)
//                    .snippet(snippet)
//            )
//
//            Handler().postDelayed(Runnable { mapboxMap.removeMarker(mark) }, 2000)
//        }
    }



    suspend fun connectMQTT() {
        val job = GlobalScope.launch(Dispatchers.IO) {
            view?.context?.let { mMQTTViewModel.connectMQTT(it) }
        }
        job.join()
        delay(1000) // wait for connection to be established
        mMQTTViewModel.receiveMessagesInARMAp(this)
    }

    private fun setLateTime(seconds: Int): Boolean {
        lateTime = seconds
        return true
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