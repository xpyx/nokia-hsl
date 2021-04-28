package com.xpyx.nokiahslvisualisation.fragments.analytics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.apollographql.apollo.api.Response
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.StopTimesListQuery
import com.xpyx.nokiahslvisualisation.api.StopTimesViewModel
import com.xpyx.nokiahslvisualisation.api.StopTimesViewModelFactory
import com.xpyx.nokiahslvisualisation.repository.StopTimesRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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

        var responseForChart: Response<StopTimesListQuery.Data>? = null

        // Init and hide chart
        val aaChartView = view.findViewById<AAChartView>(R.id.aa_chart_view)
        aaChartView.visibility = View.GONE

        // Init StopTimes API viewmodel
        val stopTimesRepository = StopTimesRepository()
        val stopTimesViewModelFactory = StopTimesViewModelFactory(stopTimesRepository)
        mStopTimesApiViewModel =
            ViewModelProvider(this, stopTimesViewModelFactory).get(StopTimesViewModel::class.java)


        // Init and show spinner
        spinner = view.findViewById(R.id.analytics_spinner)
        spinner.visibility = View.VISIBLE

        val startDate = Date()
        var arrivalTimes = arrayListOf<Double?>()
        var transportModes = arrayListOf<String?>()


        // HSL GraphQL query
        mStopTimesApiViewModel.getStopTimesData()
        mStopTimesApiViewModel.myStopTimesApiResponse.observe(
            viewLifecycleOwner,
            { response ->
                val endDate = Date()
                val current = LocalDateTime.now()
                val diffInMillisec: Long = endDate.time - startDate.time
                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss.SSS")
                val formatted = current.format(formatter)

                responseForChart = response
                if (response != null) {


                    // Hide spinner
                    spinner.visibility = View.GONE

                    // Timestamp the query
                    val analytix = view.findViewById<TextView>(R.id.analytix)
                    analytix.text = """
                        Query finished $formatted
                        Query elapsed $diffInMillisec milliseconds
                        
                        """.trimIndent()

                    var buses = 0
                    var trams = 0
                    var metros = 0

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
                                    ?.arrivalDelay()?.toDouble()

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


                            arrivalTimes.add(arrivalDelay)

                            when(transportMode.toString()) {
                                "BUS" -> buses++
                                "TRAM" -> trams++
                                "SUBWAY" -> metros++

                            }
                        }
                    }


                    // Put response values to chart format



                    // Do chart magic
                    val aaChartModel : AAChartModel = AAChartModel()
                        .chartType(AAChartType.Area)
                        .title("Arrival times at stops")
                        //.subtitle("subtitle")
                        .backgroundColor(R.color.design_default_color_background)
                        .dataLabelsEnabled(false)
                        .series(arrayOf(
                            AASeriesElement()
                                .name("Arrival Times")
                                .data(arrivalTimes.toArray()),
                            AASeriesElement()
                                .name("Buses")
                                .data(arrayOf(buses)),
                            AASeriesElement()
                                .name("Trams")
                                .data(arrayOf(trams)),
                            AASeriesElement()
                                .name("Metros")
                                .data(arrayOf(metros)),
                        )
                        )

                    //The chart view object calls the instance object of AAChartModel and draws the final graphic
                    aaChartView.aa_drawChartWithChartModel(aaChartModel)

                    // Show chart
                    aaChartView.visibility = View.VISIBLE




                } else {
                    // Log.d("DBG", response.toString())
                }
            })














    }
}