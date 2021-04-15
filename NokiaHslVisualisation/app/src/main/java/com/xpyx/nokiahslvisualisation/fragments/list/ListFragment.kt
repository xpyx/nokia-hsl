
package com.xpyx.nokiahslvisualisation.fragments.list

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.R.*
import com.xpyx.nokiahslvisualisation.data.TrafficItemViewModel
import kotlinx.android.synthetic.main.radio_group_traffic_type.*

class ListFragment : Fragment() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var mTrafficViewModel: TrafficItemViewModel
    private lateinit var viewHere: View
    private lateinit var filterSliderView: NavigationView
    private val listOfFilters = mutableMapOf<String, Boolean>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewHere = inflater.inflate(layout.fragment_list, container, false)
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

    @SuppressLint("LogNotTimber")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuItemCritical: MenuItem = filterSliderView.menu.findItem(R.id.critical_menu_item)
        Log.d("MENUITEM", "${menuItemCritical.title}")
        filterSliderView.setNavigationItemSelectedListener { menuItem ->
            if (menuItem.isChecked) {
                Log.d("MENUCHECKED", "${menuItem.title}: ${menuItem.isChecked}")
            }


            when (menuItem.itemId) {

                // This is how to clear radio buttons. Radio groups .clearCheck() not working.
                R.id.clear_button_type -> {
                    Log.d("RADIOMENU","${incident_menu_item.isChecked}")
                    Log.d("RADIOMENU","${event_menu_item.isChecked}")
                    incident_menu_item.isChecked = false
                    event_menu_item.isChecked = false
                    true
                }

                R.id.critical_menu_item -> {
                    Log.d("Menu", "${menuItem.title}")
                    true
                }

                R.id.apply_button_traffic_filters -> {
                    Log.d("BUTTON_TEST","TESTINGGG")

                    listOfFilters["Critical"] = menuItem.isChecked
                    Log.d("MENU", "Critical: ${menuItem.isChecked}")
                    Log.d("MENU", "FILTERLIST: $listOfFilters")


                    true
                }
                else -> true
            }
        }


    }
}

