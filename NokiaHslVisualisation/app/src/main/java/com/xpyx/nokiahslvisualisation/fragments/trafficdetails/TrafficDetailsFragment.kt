package com.xpyx.nokiahslvisualisation.fragments.trafficdetails

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.data.DataTrafficItem
import com.xpyx.nokiahslvisualisation.data.TrafficItemViewModel
import com.xpyx.nokiahslvisualisation.model.traffic.Defined
import com.xpyx.nokiahslvisualisation.model.traffic.TrafficItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.Text

class TrafficDetailsFragment : Fragment() {
    private lateinit var trafficItem: DataTrafficItem
    private lateinit var trafficItemViewModel: TrafficItemViewModel
    private lateinit var trafficTitleTV : TextView
    private lateinit var trafficDescriptionTV : TextView
    private lateinit var trafficTimeTV : TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_traffic_details, container, false)

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

        val toolbar = (activity as AppCompatActivity).supportActionBar
        toolbar?.title = getString(R.string.details_view)

        GlobalScope.launch(context = Dispatchers.IO) {
            val trafficID = arguments?.getLong("TRAFFIC") ?: 0
            trafficItem = trafficItemViewModel.getTrafficItem(trafficID)

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
                ""
            }

            trafficDescriptionTV.text = locationText

        }

    }

}