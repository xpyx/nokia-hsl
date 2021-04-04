package com.xpyx.nokiahslvisualisation.fragments.map

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import com.google.ar.core.HitResult
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
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
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint


class MapFragment : Fragment(),SeekBar.OnSeekBarChangeListener {

    private var mapView: MapView? = null

    private lateinit var arFrag: ArFragment
    private var viewRenderable: ViewRenderable? = null
    private var mTransparencyBar: SeekBar? = null
    private var mHeightBar: SeekBar? = null
    private var mWidthBar: SeekBar? = null

    private lateinit var map: org.osmdroid.views.MapView
    private var transparency = 0f
    private var height = 900
    private var width = 900
    private lateinit var apa: LinearLayout



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ctx = requireActivity().applicationContext
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
         

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


        arFrag = childFragmentManager.findFragmentById(
            R.id.sceneform_fragment
        ) as ArFragment

        ViewRenderable.builder()
            .setView(requireContext(), R.layout.mymap)
            .build()
            .thenAcceptAsync {
                //load apa as container of map to get size controll
                apa = it.view as LinearLayout
                map = it.view.findViewById<org.osmdroid.views.MapView>(R.id.map)
                map.setTileSource(TileSourceFactory.MAPNIK)
                map.setBuiltInZoomControls(true)
                map.setMultiTouchControls(true)
                map.controller.setZoom(17.0)
                map.controller.setCenter(GeoPoint(60.17, 24.95))
                viewRenderable = it
            }


        arFrag.setOnTapArPlaneListener { hitResult: HitResult?, _, _ ->
            if (viewRenderable == null) {
                return@setOnTapArPlaneListener
            }
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
            Log.e("aaa", "$mTransparencyBar")
        }

        mTransparencyBar?.setOnSeekBarChangeListener(this)
        mHeightBar?.setOnSeekBarChangeListener(this)
        mWidthBar?.setOnSeekBarChangeListener(this)

    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        when (seekBar){
            mTransparencyBar -> {
                Log.e("AAA", "${seekBar}")
                transparency = progress.toFloat() / TRANSPARENCY_MAX.toFloat()
                map.alpha = transparency
                Log.e("aaa", "$transparency")
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



}