
package com.xpyx.nokiahslvisualisation.fragments.list

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.RangeSlider
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.R.*
import com.xpyx.nokiahslvisualisation.data.TrafficItemViewModel
import com.xpyx.nokiahslvisualisation.utils.Constants
import kotlinx.android.synthetic.main.fragment_list.*
import java.util.*

class ListFragment : Fragment(){


    private lateinit var recyclerView: RecyclerView
    private lateinit var mTrafficViewModel: TrafficItemViewModel
    private lateinit var viewHere: View
    private val listOfFilters = mutableMapOf<String, Any>()
    private val listOfBooleanFilterNames = listOf(
            "critical",
            "major",
            "minor",
            "road_closed",
            "response_vehicles",
            "incident",
            "event"
    )

    private val listOfDistanceFilterNames = listOf(
            "min_lat_difference",
            "max_lat_difference",
            "min_lon_difference",
            "max_lon_difference"
    )



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewHere = inflater.inflate(layout.fragment_list, container, false)

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
        val listOfCheckBoxes = listOf<CheckBox>(
                critical_checkbox,
                major_checkbox,
                minor_checkbox,
                road_closed_checkbox,
                response_vehicles_checkbox
        )
        val listOfRadioButtons = listOf<RadioButton>(
                incident_radio_button,
                event_radio_button
        )
        loadData()


        distance_slider.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener{
            override fun onStartTrackingTouch(slider: RangeSlider) {
                Log.d("RangeSlider", "Started tracking touch")
            }
            override fun onStopTrackingTouch(slider: RangeSlider) {
                Log.d("RangeSlider", "Stopped tracking touch")
                // Kilometer values to coordinate differences
                listOfFilters["min_lat_difference"] = convertToCoordinates()[0]
                listOfFilters["max_lat_difference"] = convertToCoordinates()[1]
                listOfFilters["min_lon_difference"] = convertToCoordinates()[0]
                listOfFilters["max_lon_difference"] = convertToCoordinates()[1]

                Log.d("FILTERLIST", "$listOfFilters")
            }
        })

        clear_all_button.setOnClickListener{

                radio_group_criticality.clearCheck()
                distance_slider.valueFrom = 0F
                distance_slider.valueTo = 150F
                critical_checkbox.isChecked = false
                major_checkbox.isChecked = false
                minor_checkbox.isChecked = false
                road_closed_checkbox.isChecked = false
                response_vehicles_checkbox.isChecked = false

                distance_slider.setValues(0F, 150F)

            for (item in listOfBooleanFilterNames) {
                listOfFilters[item] = false
            }
            listOfFilters["min_lat_difference"] = convertToCoordinates()[0]
            listOfFilters["max_lat_difference"] = convertToCoordinates()[1]
            listOfFilters["min_lon_difference"] = convertToCoordinates()[0]
            listOfFilters["max_lon_difference"] = convertToCoordinates()[1]
            Log.d("FILTERLIST", "$listOfFilters")
        }


        listOfCheckBoxes.forEach {

            val name = it.text.toString().toLowerCase(Locale.ROOT).replace("\\s+".toRegex(), "_")
            it.setOnCheckedChangeListener  {_, isChecked ->
                listOfFilters[name] = isChecked
                Log.d("FILTERLIST", "$listOfFilters")
            }
            Log.d("filternames", name)
            val value = listOfFilters[name] as Boolean
            if (value) {
                it.isChecked = true
                it.jumpDrawablesToCurrentState()
            } else {
                it.isChecked = false
                it.jumpDrawablesToCurrentState()
            }
        }
        listOfRadioButtons.forEach {
            val name = it.text.toString().toLowerCase(Locale.ROOT).replace("\\s+".toRegex(), "_")
            it.setOnCheckedChangeListener {_, isChecked ->
                listOfFilters[name] = isChecked
                Log.d("FILTERLIST", "$listOfFilters")
            }
            val value = listOfFilters[name] as Boolean
            if (value) {
                it.isChecked = true
                it.jumpDrawablesToCurrentState()
            } else {
                it.isChecked = false
                it.jumpDrawablesToCurrentState()
            }
        }
    }

    private fun convertToCoordinates() : List<Double> {
        val differenceMin = distance_slider.values[0]/110.574
        val differenceMax = distance_slider.values[1]/110.574
        return listOf(differenceMin, differenceMax)
    }
    private fun convertFromCoordinates(coordinateList: List<Double>) : List<Float> {
        val differenceMin = coordinateList[0]*110.574
        val differenceMax = coordinateList[1]*110.574
        return listOf(differenceMin.toFloat(), differenceMax.toFloat())
    }


    override fun onPause() {
        super.onPause()
        saveData()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun saveData() {
        val sPreferences = activity?.getSharedPreferences(Constants.TRAFFIC_FILTERS, MODE_PRIVATE)
        val editor = sPreferences?.edit()
        for (item in listOfBooleanFilterNames) {
            editor?.putBoolean(item, listOfFilters[item] as Boolean)
            editor?.apply()
        }

        for (item in listOfDistanceFilterNames) {
            val value = listOfFilters[item] as Double
            editor?.putFloat(item, value.toFloat())
            editor?.apply()
            Log.d("FILTERSAVED", "$item: $value")
        }
    }

    private fun loadData() {
        val sPreferences = activity?.getSharedPreferences(Constants.TRAFFIC_FILTERS, MODE_PRIVATE)
        for (item in listOfBooleanFilterNames) {
            listOfFilters[item] = sPreferences?.getBoolean(item, false) as Boolean
        }
        for (item in listOfDistanceFilterNames) {
            listOfFilters[item] = sPreferences?.getFloat(item, 0.0F)?.toDouble() as Double
        }
        val list = listOf(listOfFilters["min_lat_difference"] as Double, listOfFilters["max_lat_difference"] as Double)
        distance_slider.setValues(convertFromCoordinates(list)[0], convertFromCoordinates(list)[1])
    }




    // For hiding the soft keyboard
    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

