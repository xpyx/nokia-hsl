package com.xpyx.nokiahslvisualisation.fragments.ar

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.ar.core.TrackingState
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.core.exceptions.UnavailableException
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
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

import kotlinx.android.synthetic.main.fragment_ar.*
import uk.co.appoly.arcorelocation.LocationMarker
import uk.co.appoly.arcorelocation.LocationScene
import java.lang.ref.WeakReference
import java.util.concurrent.CompletableFuture


class ARFragment : Fragment(), OnMapReadyCallback, PermissionsListener {

    private lateinit var listener: FragmentActivity
    private var mapView: MapView? = null
    private var permissionsManager: PermissionsManager = PermissionsManager(this)
    private lateinit var mapboxMap: MapboxMap
    private var arCoreInstallRequested = false

    // Our ARCore-Location scene
    private var locationScene: LocationScene? = null

    private var arHandler = Handler(Looper.getMainLooper())

    lateinit var loadingDialog: AlertDialog

    private val resumeArElementsTask = Runnable {
        locationScene?.resume()
        fragment.resume()
    }


    private var userGeolocation = Geolocation.EMPTY_GEOLOCATION

    private var VehicleSet: MutableSet<Vehicle> = mutableSetOf()
    private var areAllMarkersLoaded = false


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
        return inflater.inflate(R.layout.fragment_ar, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = view.findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)
        setupLoadingDialog()

        VehicleSet.add(Vehicle("1","2",60.2700,24.400,90.0))
        VehicleSet.add(Vehicle("1","203",60.278996,24.443877,90.0))
        renderVenues()

            }



    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.Builder().fromUri(
                "asset://local_style")) {

            val source = RasterSource(
                    "map", TileSet("https://cdn.digitransit.fi/map/v1/hsl-map" + "/{z}/{x}/{y}.png", "mapbox://mapid")
            )


            val position = CameraPosition.Builder()
                    .zoom(25.5)
                    .tilt(20.0)
                    .build()
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1000)
            it.addSource(source)


            enableLocationComponent(it)
        }
    }

    @SuppressLint("MissingPermission")
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
                isLocationComponentEnabled = true

// Set the LocationComponent's camera mode
                cameraMode = CameraMode.TRACKING_COMPASS

// Set the LocationComponent's render mode
                renderMode = RenderMode.COMPASS
            }
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(listener)
        }
    }



    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        for (permission in permissionsToExplain) {
            Toast.makeText(listener, permission, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationComponent(mapboxMap.style!!)
        } else {
            Toast.makeText(listener, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show()

        }
    }
    private fun setupLoadingDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        val dialogHintMainView =
            LayoutInflater.from(context).inflate(R.layout.loading_dialog, null) as LinearLayout
        alertDialogBuilder.setView(dialogHintMainView)
        loadingDialog = alertDialogBuilder.create()
        loadingDialog.setCanceledOnTouchOutside(false)
    }
    private fun setupSession() {
        if (fragment == null) {
            Log.e("aaa","fragment is null")
            return
        }

        if (fragment.session == null) {
            try {
                val session = AugmentedRealityLocationUtils.setupSession(requireActivity(), arCoreInstallRequested)
                if (session == null) {
                    Log.e("aaa","session is null")
                    arCoreInstallRequested = true
                    return
                } else {
                    Log.e("aaa","have a cup of frag.session")
                    fragment.setupSession(session)
                }
            } catch (e: UnavailableException) {
                Log.e("aaa","shiit catch 22")
                AugmentedRealityLocationUtils.handleSessionException(requireActivity(), e)
            }
        }
        if (locationScene == null) {
            locationScene = LocationScene(requireActivity(), fragment)
            locationScene!!.setMinimalRefreshing(true)
            locationScene!!.setOffsetOverlapping(true)
//            locationScene!!.setRemoveOverlapping(true)
            locationScene!!.anchorRefreshInterval = 2000
        }
        try {
            resumeArElementsTask.run()
        } catch (e: CameraNotAvailableException) {
            Toast.makeText(context, "Unable to get camera", Toast.LENGTH_LONG).show()
            //finish()
            return
        }

        if (userGeolocation == Geolocation.EMPTY_GEOLOCATION) {
            LocationAsyncTask(WeakReference(this@ARFragment)).execute(locationScene!!)
        }
    }

    fun fetchVenues(deviceLatitude: Double, deviceLongitude: Double) {
        loadingDialog.dismiss()
        userGeolocation = Geolocation(deviceLatitude.toString(), deviceLongitude.toString())

    }
    private fun renderVenues() {
        setupAndRenderVenuesMarkers()
        updateVenuesMarkers()
    }

    private fun setupAndRenderVenuesMarkers() {
        val uri = Uri.parse("file:///android_asset/bus3.gltf")
        VehicleSet.forEach { vehicle ->
            val completableFutureViewRenderable = ModelRenderable.builder()
                .setSource(context, RenderableSource.builder().setSource(context, uri, RenderableSource.SourceType.GLTF2)
                    .setScale(0.1f)
                    .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                    .build())
                .setRegistryId(("bus3"))
                .build()
            CompletableFuture.anyOf(completableFutureViewRenderable)
                .handle<Any> { _, throwable ->
                    //here we know the renderable was built or not
                    if (throwable != null) {
                        // handle renderable load fail
                        return@handle null
                    }
                    try {
                        val venueMarker = LocationMarker(
                            vehicle.long,
                            vehicle.lat,
                            setVenueNode(vehicle, completableFutureViewRenderable)
                        )
                        arHandler.postDelayed({
                            attachMarkerToScene(
                                venueMarker,
                                completableFutureViewRenderable
                            )
                            if (VehicleSet.indexOf(vehicle) == VehicleSet.size - 1) {
                                areAllMarkersLoaded = true
                            }
                        }, 200)

                    } catch (ex: Exception) {
                        //                        showToast(getString(R.string.generic_error_msg))
                    }
                    null
                }
        }
    }

    private fun updateVenuesMarkers() {
        fragment.scene.addOnUpdateListener()
        {
            if (!areAllMarkersLoaded) {
                return@addOnUpdateListener
            }

            locationScene?.mLocationMarkers?.forEach { locationMarker ->
                locationMarker.height =
                    AugmentedRealityLocationUtils.generateRandomHeightBasedOnDistance(
                        locationMarker?.anchorNode?.distance ?: 0
                    )
            }


            val frame = fragment!!.arFrame ?: return@addOnUpdateListener
            if (frame.camera.trackingState != TrackingState.TRACKING) {
                return@addOnUpdateListener
            }
            locationScene!!.processFrame(frame)
        }
    }


    private fun attachMarkerToScene(
        locationMarker: LocationMarker,
        layoutRendarable: CompletableFuture<ModelRenderable>
    ) {
        resumeArElementsTask.run {
            locationMarker.scalingMode = LocationMarker.ScalingMode.FIXED_SIZE_ON_SCREEN
            locationMarker.scaleModifier =
                AugmentedRealityLocationUtils.INITIAL_MARKER_SCALE_MODIFIER

            locationScene?.mLocationMarkers?.add(locationMarker)
            locationMarker.anchorNode?.isEnabled = true


        }
        locationMarker.setRenderEvent { locationNode ->

            resumeArElementsTask.run {
                computeNewScaleModifierBasedOnDistance(locationMarker, locationNode.distance)

            }
        }
    }
    private fun checkAndRequestPermissions() {
        if (!PermissionUtils.hasLocationAndCameraPermissions(requireContext())) {
            PermissionUtils.requestCameraAndLocationPermissions(requireActivity())
        } else {
            Log.e("AaA","setting up")
            setupSession()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, results: IntArray) {
        if (!PermissionUtils.hasLocationAndCameraPermissions(requireContext())) {
            Toast.makeText(context, R.string.camera_and_location_permission_request, Toast.LENGTH_LONG).show()
            if (!PermissionUtils.shouldShowRequestPermissionRationale(Activity())) {
                // Permission denied with checking "Do not ask again".
                PermissionUtils.launchPermissionSettings(Activity())
            }
           // finish()
        }
    }

    private fun computeNewScaleModifierBasedOnDistance(locationMarker: LocationMarker, distance: Int) {
        val scaleModifier = AugmentedRealityLocationUtils.getScaleModifierBasedOnRealDistance(distance)
        return if (scaleModifier == AugmentedRealityLocationUtils.INVALID_MARKER_SCALE_MODIFIER) {
            detachMarker(locationMarker)
        } else {
            locationMarker.scaleModifier = scaleModifier
        }
    }

    private fun detachMarker(locationMarker: LocationMarker) {
        locationMarker.anchorNode?.anchor?.detach()
        locationMarker.anchorNode?.isEnabled = false
        locationMarker.anchorNode = null
    }


    private fun setVenueNode(venue: Vehicle, completableFuture: CompletableFuture<ModelRenderable>): Node {
        val node = Node()

        node.renderable = completableFuture.get()

        /* val nodeLayout = completableFuture.get().view
         val venueName = nodeLayout.name
         val markerLayoutContainer = nodeLayout.pinContainer
         venueName.text = venue.name
         markerLayoutContainer.visibility = View.GONE */
        node.setOnTouchListener { _, _ ->
            Toast.makeText(context, "Bussi no: ${venue.veh}", Toast.LENGTH_SHORT).show()


            false
        }

        Glide.with(this)
            .load(venue.veh)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        // .into(nodeLayout.categoryIcon)

        return node
    }








    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        checkAndRequestPermissions()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        fragment.session?.let {
            locationScene?.pause()
            fragment?.pause()
        }
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
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
class LocationAsyncTask(private val activityWeakReference: WeakReference<ARFragment>) :
    AsyncTask<LocationScene, Void, List<Double>>() {

    override fun onPreExecute() {
        super.onPreExecute()
        activityWeakReference.get()!!.loadingDialog.show()
    }

    override fun doInBackground(vararg p0: LocationScene): List<Double> {
        var deviceLatitude: Double?
        var deviceLongitude: Double?
        do {
            deviceLatitude = p0[0].deviceLocation?.currentBestLocation?.latitude
            deviceLongitude = p0[0].deviceLocation?.currentBestLocation?.longitude
        } while (deviceLatitude == null || deviceLongitude == null)
        return listOf(deviceLatitude, deviceLongitude)
    }

    override fun onPostExecute(geolocation: List<Double>) {
        activityWeakReference.get()!!.fetchVenues(deviceLatitude = geolocation[0], deviceLongitude = geolocation[1])
        super.onPostExecute(geolocation)
    }
}
