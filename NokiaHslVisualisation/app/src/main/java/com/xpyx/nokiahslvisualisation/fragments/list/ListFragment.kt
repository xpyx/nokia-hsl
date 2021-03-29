package com.xpyx.nokiahslvisualisation.fragments.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.model.traffic.TRAFFIC_ITEM

class ListFragment : Fragment(){

    private val trafficInfoList = mutableListOf<TRAFFIC_ITEM>()

    private val bus1 = FakeBus("Line 200","Late 10 minutes because of traffic.")
    private val bus2 = FakeBus("Line 41 HKI","Missing one bus on line, 3 missed launches.")
    private val busList = mutableListOf<FakeBus>(bus1, bus2)
    private lateinit var recyclerView: RecyclerView
    private lateinit var refreshButton: Button
    private val hereTrafficApiKey = getString(R.string.here_maps_api_key)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list, container, false)


        // Recycler view
        recyclerView = view.findViewById(R.id.bus_recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = BusListAdapter(busList)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Button
        refreshButton = view.findViewById(R.id.traffic_refresh_button)



    }


}