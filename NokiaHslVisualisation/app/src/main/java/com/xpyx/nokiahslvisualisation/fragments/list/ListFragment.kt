package com.xpyx.nokiahslvisualisation.fragments.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xpyx.nokiahslvisualisation.R

class ListFragment : Fragment(){

    private val bus1 = FakeBus("Line 200","Late 10 minutes because of traffic.")
    private val bus2 = FakeBus("Line 41 HKI","Missing one bus on line, 3 missed launches.")
    private val busList = mutableListOf<FakeBus>(bus1, bus2)
    private lateinit var recyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        recyclerView = view.findViewById(R.id.bus_recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = BusListAdapter( busList)

        return view
    }


}