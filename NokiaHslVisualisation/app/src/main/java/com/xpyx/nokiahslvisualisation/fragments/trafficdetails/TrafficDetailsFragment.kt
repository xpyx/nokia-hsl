package com.xpyx.nokiahslvisualisation.fragments.trafficdetails

import android.location.Geocoder
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.data.DataTrafficItem
import com.xpyx.nokiahslvisualisation.data.TrafficItemViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class TrafficDetailsFragment : Fragment() {

    private lateinit var trafficItem: DataTrafficItem
    private lateinit var trafficItemViewModel: TrafficItemViewModel
    private lateinit var trafficTitleTV : TextView
    private lateinit var trafficDescriptionTV : TextView
    private lateinit var trafficTimeTV : TextView

    private lateinit var map: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_traffic_details, container, false)

        // Set up map
        map = view.findViewById(R.id.map_view_traffic)



        trafficItemViewModel = ViewModelProvider(this).get(TrafficItemViewModel::class.java)
        // Set toolbar back button listener
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    requireView().findNavController().navigate(R.id.action_action_bus_to_action_list)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        trafficDescriptionTV = view.findViewById(R.id.traffic_description)
        trafficTitleTV = view.findViewById(R.id.traffic_title)
        trafficTimeTV = view.findViewById(R.id.traffic_time)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ctx = context
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller.setZoom(12.5)
        map.controller.setCenter(GeoPoint(60.2, 25.0))


        val toolbar = (activity as AppCompatActivity).supportActionBar
        toolbar?.title = getString(R.string.details_view)

        GlobalScope.launch(context = Dispatchers.IO) {
            val trafficID = arguments?.getLong("TRAFFIC") ?: 0
            trafficItem = trafficItemViewModel.getTrafficItem(trafficID)



            runOnUiThread {

                // Set map location
                val trafficItemLatitude = trafficItem.location?.locationGeoloc?.geolocOrigin?.geolocLocationLatitude!!
                val trafficItemLongitude = trafficItem.location?.locationGeoloc?.geolocOrigin?.geolocLocationLongitude!!
                map.controller.setCenter(GeoPoint(trafficItemLatitude,trafficItemLongitude))
                addMarker(trafficItemLatitude, trafficItemLongitude)

                // Set data into text views
                trafficTitleTV.text = trafficItem.traffic_item_type_desc
                trafficTimeTV.text = getString(R.string.traffic_time, trafficItem.criticality?.ityDescription, trafficItem.start_time, trafficItem.end_time)
                val defined = trafficItem.location?.locationDefined

                val locationText: String = if (defined != null) {
                    if (defined.definedOrigin?.definedLocationDirection != null) {
                        "From: ${defined.definedOrigin?.definedLocationRoadway?.directionClassDescription?.get(0)?.trafficItemDescriptionElementValue} towards ${defined.definedOrigin?.definedLocationDirection?.directionClassDescription?.get(0)?.trafficItemDescriptionElementValue} from ${defined.definedOrigin?.definedLocationPoint?.directionClassDescription?.get(0)?.trafficItemDescriptionElementValue} to ${defined.definedTo?.definedLocationPoint?.directionClassDescription?.get(0)?.trafficItemDescriptionElementValue}"
                    } else {
                        "From: ${defined.definedOrigin?.definedLocationRoadway?.directionClassDescription?.get(0)?.trafficItemDescriptionElementValue} from ${defined.definedOrigin?.definedLocationPoint?.directionClassDescription?.get(0)?.trafficItemDescriptionElementValue} to ${defined.definedTo?.definedLocationPoint?.directionClassDescription?.get(0)?.trafficItemDescriptionElementValue}"
                    }
                } else {
                    val address = getAddress(trafficItemLatitude, trafficItemLongitude)
                    "Problem: ${trafficItem.trafficItemDescriptionElement?.get(0)?.trafficItemDescriptionElementValue} Location: $address"
                }

                trafficDescriptionTV.text = locationText
            }
        }

    }

    private fun addMarker(lat: Double,  lon: Double) {

        // Custom markers if needed

        val marker = Marker(map)
        marker.position = GeoPoint(lat, lon)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        // Icon made by Freepik from www.flaticon.com
        marker.icon = requireContext().resources.getDrawable(R.drawable.map_warning_icon)
        marker.setInfoWindow(null)
        map.overlays.add(marker)

    }

    fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return // Fragment not attached to an Activity
        activity?.runOnUiThread(action)
    }

    private fun getAddress(lat: Double?, lng: Double?): String {
        val geoCoder = Geocoder(context)
        val list = geoCoder.getFromLocation(lat ?: 0.0, lng ?: 0.0, 1)
        return list[0].getAddressLine(0)
    }

}