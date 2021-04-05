package com.xpyx.nokiahslvisualisation.fragments.alertdetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.xpyx.nokiahslvisualisation.R

class AlertDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
<<<<<<< HEAD
        val view = inflater.inflate(R.layout.fragment_alert_details, container, false)
=======
        val view = inflater.inflate(R.layout.fragment_bus_details, container, false)
>>>>>>> efc87f3... add recyclerview to homefragment xml and refactor homefragment

        // Set toolbar back button listener
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
<<<<<<< HEAD
                    requireView().findNavController().navigate(R.id.action_alertDetailsFragment_to_action_home)
=======
                    requireView().findNavController().navigate(R.id.action_action_bus_to_action_list)
>>>>>>> efc87f3... add recyclerview to homefragment xml and refactor homefragment
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = (activity as AppCompatActivity).supportActionBar
<<<<<<< HEAD
        toolbar?.title = getString(R.string.alert_details_view)
=======
        toolbar?.title = getString(R.string.details_view)
>>>>>>> efc87f3... add recyclerview to homefragment xml and refactor homefragment
    }

}