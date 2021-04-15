package com.xpyx.nokiahslvisualisation.fragments.home

import android.content.Intent
import android.net.Uri
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

            var filteredAlertList = mutableListOf<AlertItem>()
            if (constraint == null || constraint.length == 0) {
                filteredAlertList.addAll(alertListFull)

            } else {

                var filterPattern: String = constraint.toString().toLowerCase().trim()
                for (item in alertListFull) {
                    if (
                        item.alertHeaderText?.toLowerCase(Locale.ROOT)?.contains(filterPattern)!! ||
                        item.alertDescriptionText?.toLowerCase(Locale.ROOT)?.contains(filterPattern)!! ||
                        item.alertSeverityLevel?.toLowerCase(Locale.ROOT)?.contains(filterPattern)!! ||
                        item.alertCause?.toLowerCase(Locale.ROOT)?.contains(filterPattern)!! ||
                        item.alertEffect?.toLowerCase(Locale.ROOT)?.contains(filterPattern)!!
                    ) {
                        filteredAlertList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredAlertList
            return results
        }
    }
}

