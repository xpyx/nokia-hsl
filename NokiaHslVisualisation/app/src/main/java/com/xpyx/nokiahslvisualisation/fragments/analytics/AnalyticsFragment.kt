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

                            Log.d(
                                "DBG", """
                                
                                $routeId
                                $transportMode
                                $directionId
                                $arrivalDelay
                                - - - - - - - - - 
                            """.trimIndent()
                            )

                        }
                    }



                    // Do chart magic
                    val aaChartModel : AAChartModel = AAChartModel()
                        .chartType(AAChartType.Area)
                        .title("title")
                        .subtitle("subtitle")
                        .backgroundColor(R.color.design_default_color_background)
                        .dataLabelsEnabled(true)
                        .series(arrayOf(
                            AASeriesElement()
                                .name("Tokyo")
                                .data(arrayOf(7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6)),
                            AASeriesElement()
                                .name("NewYork")
                                .data(arrayOf(0.2, 0.8, 5.7, 11.3, 17.0, 22.0, 24.8, 24.1, 20.1, 14.1, 8.6, 2.5)),
                            AASeriesElement()
                                .name("London")
                                .data(arrayOf(0.9, 0.6, 3.5, 8.4, 13.5, 17.0, 18.6, 17.9, 14.3, 9.0, 3.9, 1.0)),
                            AASeriesElement()
                                .name("Berlin")
                                .data(arrayOf(3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8))
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