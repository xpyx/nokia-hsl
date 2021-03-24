package com.xpyx.nokiahslvisualisation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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


class MapFragment : Fragment() {

    private var mapView: MapView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        context?.let { Mapbox.getInstance(
            it.applicationContext,
            getString(R.string.mapbox_access_token)
        ) }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       /* context?.let { Mapbox.getInstance(
            it.applicationContext,
            getString(R.string.mapbox_access_token)
        ) }*/


        mapView = view.findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync { mapboxMap ->
            mapboxMap.setStyle(Style.MAPBOX_STREETS) {
                // Adding a raster source layer
                val source = RasterSource(
                    "hsl-map",
                    TileSet("2.1.0", "https://cdn.digitransit.fi/map/v1/hsl-map" + "/{z}/{x}/{y}.png")
                )



                mapboxMap?.addMarker(
                    MarkerOptions()
                        .position(LatLng(60.444, 24.0, 1.0))
                )
                val position = CameraPosition.Builder()
                    .target(LatLng(60.444, 24.07520))
                    .zoom(10.0)
                    .tilt(20.0)
                    .build()
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1000)
                it.addSource(source)

            }
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

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

   /* override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState!!)
        mapView!!.onSaveInstanceState(outState)
    } */



}