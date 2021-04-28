package com.xpyx.nokiahslvisualisation.fragments.analytics

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.api.StopTimesViewModel
import com.xpyx.nokiahslvisualisation.api.StopTimesViewModelFactory
import com.xpyx.nokiahslvisualisation.repository.StopTimesRepository
import java.util.*


class AnalyticsFragment : Fragment() {

    private lateinit var mStopTimesApiViewModel: StopTimesViewModel
    private lateinit var spinner: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_analytics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Init StopTimes API viewmodel
        val stopTimesRepository = StopTimesRepository()
        val stopTimesViewModelFactory = StopTimesViewModelFactory(stopTimesRepository)
        mStopTimesApiViewModel =
            ViewModelProvider(this, stopTimesViewModelFactory).get(StopTimesViewModel::class.java)


        // Init and show spinner
        spinner = view.findViewById(R.id.analytics_spinner)
        spinner.visibility = View.VISIBLE

        // HSL GraphQL query
        mStopTimesApiViewModel.getStopTimesData()
        mStopTimesApiViewModel.myStopTimesApiResponse.observe(
            viewLifecycleOwner,
            { response ->
                if (response != null) {

                    // Hide spinner
                    spinner.visibility = View.GONE

                    // Timestamp the query
                    val analytix = view.findViewById<TextView>(R.id.analytix)
                    analytix.text = "Query finished at ${Date()}"

                    // The stoptimes data is here, iterate over the whole response
                    response.data?.stops()?.forEach {

                        if (it.stoptimesForPatterns()?.isNotEmpty() == true) {

                            val routeId =
                                it.stoptimesForPatterns()?.get(0)?.stoptimes()?.get(0)
                                    ?.trip()?.route()
                                    ?.gtfsId()?.substring(
                                        4
                                    )

                            val transportMode =
                                it.stoptimesForPatterns()?.get(0)?.stoptimes()?.get(0)
                                    ?.trip()?.route()
                                    ?.mode()

                            val arrivalDelay =
                                it.stoptimesForPatterns()?.get(0)?.stoptimes()?.get(0)
                                    ?.arrivalDelay()

                            var directionId =
                                it.stoptimesForPatterns()?.get(0)?.stoptimes()?.get(0)
                                    ?.trip()
                                    ?.directionId()

                            // Change direction id according to instructions. Also note if null, then -> "+"
                            if (directionId.equals("0")) {
                                directionId = "1"
                            } else if (directionId.equals("1")) {
                                directionId = "2"
                            }

                            Log.d("DBG", """
                                
                                $routeId
                                $transportMode
                                $directionId
                                $arrivalDelay
                                - - - - - - - - - 
                            """.trimIndent())

                        }
                    }
                } else {
                    // Log.d("DBG", response.toString())
                }
            })





    }
}