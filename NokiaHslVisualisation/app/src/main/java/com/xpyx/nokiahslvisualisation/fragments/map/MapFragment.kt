package com.xpyx.nokiahslvisualisation.fragments.map

import android.location.Geocoder
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.ar.core.HitResult
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.data.DataTrafficItem
import com.xpyx.nokiahslvisualisation.data.TrafficItemViewModel
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
}