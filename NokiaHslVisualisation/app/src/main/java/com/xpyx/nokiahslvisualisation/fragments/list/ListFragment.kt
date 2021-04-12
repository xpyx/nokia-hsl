
package com.xpyx.nokiahslvisualisation.fragments.list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.data.TrafficItemViewModel
import kotlinx.android.synthetic.main.radio_group_traffic_type.*

class ListFragment : Fragment() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var mTrafficViewModel: TrafficItemViewModel
    private lateinit var viewHere: View
    private lateinit var filterSliderView: NavigationView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewHere = inflater.inflate(R.layout.fragment_list, container, false)
        filterSliderView = viewHere.findViewById(R.id.filter_drawer_view)

        val adapter = context?.let { TrafficListAdapter(requireContext()) }

        // Recycler view
        recyclerView = viewHere.findViewById(R.id.bus_recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter


        mTrafficViewModel = ViewModelProvider(this).get(TrafficItemViewModel::class.java)
        mTrafficViewModel.readAllData.observe(viewLifecycleOwner, { traffic ->
            adapter?.setData(traffic)
        })

        return viewHere
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filterSliderView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                // This is how to clear radio buttons. Radio groups .clearCheck() not working.
                R.id.clear_button_type -> {
                    incident_menu_item.isChecked = false
                    event_menu_item.isChecked = false
                    true
                }
                R.id.apply_button_traffic_filters -> {
                    Log.d("BUTTON_TEST","TESTINGGG")
                    true
                }
                else -> true
            }
        }


    }
}