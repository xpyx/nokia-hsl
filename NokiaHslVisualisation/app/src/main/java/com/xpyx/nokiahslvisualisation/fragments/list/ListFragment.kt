
package com.xpyx.nokiahslvisualisation.fragments.list

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.R.*
import com.xpyx.nokiahslvisualisation.data.TrafficItemViewModel
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : Fragment(){


    private lateinit var recyclerView: RecyclerView
    private lateinit var mTrafficViewModel: TrafficItemViewModel
    private lateinit var viewHere: View
    private val listOfFilters = mutableMapOf<String, Any>()


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


        criticality_switch.setOnCheckedChangeListener {_, isChecked ->

            listOfFilters["Criticality"] = isChecked
            Log.d("Criticality", "$isChecked")
        }
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

