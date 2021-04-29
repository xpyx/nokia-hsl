package com.xpyx.nokiahslvisualisation.fragments.ar

import android.app.Activity
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.ar.core.TrackingState
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.core.exceptions.UnavailableException
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.xpyx.nokiahslvisualisation.R
import kotlinx.android.synthetic.main.fragment_ar.*
import uk.co.appoly.arcorelocation.LocationMarker
import uk.co.appoly.arcorelocation.LocationScene
import java.lang.ref.WeakReference
import java.util.concurrent.CompletableFuture


class ARFragment : Fragment() {



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

    private var vehicleSet: MutableSet<Vehicle> = mutableSetOf()
    private var areAllMarkersLoaded = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ar, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupLoadingDialog()

        vehicleSet.add(Vehicle("1", "2", 60.2700, 24.400, 90.0))
        vehicleSet.add(Vehicle("1", "203", 60.2833, 24.4607, 0.0))
        renderVenues()

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
            return
        }

        if (fragment.session == null) {
            try {
                val session = AugmentedRealityLocationUtils.setupSession(
                    requireActivity(),
                    arCoreInstallRequested
                )
                if (session == null) {
                    arCoreInstallRequested = true
                    return
                } else {
                    fragment.setupSession(session)
                }
            } catch (e: UnavailableException) {
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
        val uri = Uri.parse("file:///android_asset/bus4.gltf")
        vehicleSet.forEach { vehicle ->
            val completableFutureViewRenderable = ModelRenderable.builder()
                .setSource(
                    context, RenderableSource.builder().setSource(
                        context,
                        uri,
                        RenderableSource.SourceType.GLTF2
                    )
                        .setScale(0.1f)
                        .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                        .build()
                )
                .setRegistryId(("bus4"))
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
                                vehicle.heading.toFloat()
                            )
                            if (vehicleSet.indexOf(vehicle) == vehicleSet.size - 1) {
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
        heading: Float
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
                locationMarker.node.localRotation = Quaternion.axisAngle(Vector3(0f,1f,0f),heading)

            }
        }
    }
    private fun checkAndRequestPermissions() {
        if (!PermissionUtils.hasLocationAndCameraPermissions(requireContext())) {
            PermissionUtils.requestCameraAndLocationPermissions(requireActivity())
        } else {
            setupSession()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        results: IntArray
    ) {
        if (!PermissionUtils.hasLocationAndCameraPermissions(requireContext())) {
            Toast.makeText(
                context,
                R.string.camera_and_location_permission_request,
                Toast.LENGTH_LONG
            ).show()
            if (!PermissionUtils.shouldShowRequestPermissionRationale(Activity())) {
                // Permission denied with checking "Do not ask again".
                PermissionUtils.launchPermissionSettings(Activity())
            }
           // finish()
        }
    }

    private fun computeNewScaleModifierBasedOnDistance(
        locationMarker: LocationMarker,
        distance: Int
    ) {
        val scaleModifier = AugmentedRealityLocationUtils.getScaleModifierBasedOnRealDistance(
            distance
        )
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
        node.localRotation = Quaternion.axisAngle(Vector3(0f,1f,0f),venue.heading.toFloat())
        node.setOnTouchListener { _, _ ->
            Toast.makeText(context, "Bussi no: ${venue.veh}", Toast.LENGTH_SHORT).show()


            false
        }

        Glide.with(this)
            .load(venue.veh)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)


        return node
    }


    override fun onResume() {
        super.onResume()
        checkAndRequestPermissions()

    }

    override fun onPause() {
        super.onPause()
        fragment.session?.let {
            locationScene?.pause()
            fragment?.pause()
        }

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
        activityWeakReference.get()!!.fetchVenues(
            deviceLatitude = geolocation[0],
            deviceLongitude = geolocation[1]
        )
        super.onPostExecute(geolocation)
    }
}
