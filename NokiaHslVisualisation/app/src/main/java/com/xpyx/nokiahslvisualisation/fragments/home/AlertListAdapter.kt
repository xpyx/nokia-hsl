/**
 * Description:
 *
 * Adapter for alert recycler view to set the data,
 * apply filters used in HomeFragment and
 * apply navigation to HSL web pages
 *
 * Course: Mobiiliprojekti TX00CK67-3008
 * Name: Ville Pystynen
 */

package com.xpyx.nokiahslvisualisation.fragments.home

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.data.AlertItem
import java.text.SimpleDateFormat
import java.util.*

class AlertListAdapter : RecyclerView.Adapter<AlertListAdapter.AlertViewHolder>(), Filterable {

    private var alertList = mutableListOf<AlertItem>()
    private var alertListFull = mutableListOf<AlertItem>()

    class AlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        return AlertViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.alert_list_item, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {

        val alert = alertList[position]
        val titleTextView = holder.itemView.findViewById<TextView>(R.id.alert_title)
        val descTextView = holder.itemView.findViewById<TextView>(R.id.alert_desc)
        val dateTextView = holder.itemView.findViewById<TextView>(R.id.alert_date)
        val alertSeverity = holder.itemView.findViewById<TextView>(R.id.alert_severity)
        val alertCause = holder.itemView.findViewById<TextView>(R.id.alert_cause)
        val alertEffect = holder.itemView.findViewById<TextView>(R.id.alert_effect)

        val startStampLong =
            alert.effectiveStartDate?.toLong()?.times(1000L)?.let { java.util.Date(it) }
        val endStampLong =
            alert.effectiveEndDate?.toLong()?.times(1000L)?.let { java.util.Date(it) }

        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy")
        val start = simpleDateFormat.format(startStampLong!!)
        val end = simpleDateFormat.format(endStampLong!!)

        "Alert effective: $start - $end".also { dateTextView.text = it }
        "${alert.alertHeaderText}".also { titleTextView.text = it }
        "${alert.alertDescriptionText}".also { descTextView.text = it }
        "Severity: ${alert.alertSeverityLevel}".also { alertSeverity.text = it }
        "Cause: ${alert.alertCause}".also { alertCause.text = it }
        "Effect: ${alert.alertEffect}".also { alertEffect.text = it }

        // Make the holder clickable and show the alertUrl web page, if not null
        if (alert.alertUrl !== null) {
            holder.itemView.setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(alert.alertUrl))
                holder.itemView.context.startActivity(i)
            }
        }
    }

    override fun getItemCount(): Int {
        return alertList.size
    }

    fun setData(alerts: List<AlertItem>) {
        this.alertList = alerts as MutableList<AlertItem>
        this.alertListFull = alerts.toMutableList()
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return searchFilter
    }

    private val searchFilter: Filter = object : Filter() {
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            alertList = results?.values as MutableList<AlertItem>
            notifyDataSetChanged()
        }

        override fun performFiltering(constraint: CharSequence?): FilterResults {

            val listOfAlertItemIds = mutableListOf<String>()
            val filteredAlertList = mutableListOf<AlertItem>()
            if (constraint.isNullOrEmpty()) {
                filteredAlertList.addAll(alertListFull)

            } else {
                val buffer = constraint.toString().toLowerCase().trim()
                val filterPattern: MutableList<String> = buffer.split(";").toMutableList()

                for (alertItem in alertListFull) {

                    for (filter in filterPattern) {
                        if (!filter.isNullOrEmpty()) {
                            Log.d("filteringadapter", filter.toLowerCase())
                            if (
                                    alertItem.alertHeaderText?.toLowerCase()?.contains(filter)!! ||
                                    alertItem.alertDescriptionText?.toLowerCase()?.contains(filter)!! ||
                                    alertItem.alertSeverityLevel?.toLowerCase()?.contains(filter)!! ||
                                    alertItem.alertCause?.toLowerCase()?.contains(filter)!! ||
                                    alertItem.alertEffect?.toLowerCase()?.contains(filter)!!
                            ) {
                                if (!listOfAlertItemIds.contains(alertItem.alertId)) {
                                    filteredAlertList.add(alertItem)
                                    listOfAlertItemIds.add(alertItem.alertId!!)
                                    Log.d("FILTERADAPTER", "added with ${filterPattern.indexOf(filter)} $filter: ${alertItem.alertId}")
                                }
                            } else if (listOfAlertItemIds.contains(alertItem.alertId)) {
                                if (filteredAlertList.contains(alertItem)) {
                                    filteredAlertList.remove(alertItem)
                                    Log.d("FILTERADAPTER", "Removed with ${filterPattern.indexOf(filter)} $filter:  ${alertItem.alertId}")
                                }
                            } else {
                                listOfAlertItemIds.add(alertItem.alertId!!)
                                Log.d("FILTERADAPTER", "Excluded with ${filterPattern.indexOf(filter)} $filter: ${alertItem.alertId}")
                            }
                        }
                    }
                }

                Log.d("filteringsize", filterPattern.size.toString())
            }
            val results = FilterResults()
            results.values = filteredAlertList
            Log.d("filteringvaluesid", listOfAlertItemIds.size.toString())
            Log.d("filteringvalues", filteredAlertList.size.toString())
            return results
        }
    }
}

