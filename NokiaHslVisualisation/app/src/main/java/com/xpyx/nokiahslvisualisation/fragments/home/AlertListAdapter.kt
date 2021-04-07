package com.xpyx.nokiahslvisualisation.fragments.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.xpyx.nokiahslvisualisation.AlertsListQuery
import com.xpyx.nokiahslvisualisation.R


class AlertListAdapter(private val alertList: MutableList<AlertsListQuery.Alert>) :
    RecyclerView.Adapter<AlertListAdapter.AlertViewHolder>() {


    class AlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        return AlertViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.alert_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alert: AlertsListQuery.Alert = alertList[position]
        val titleTextView = holder.itemView.findViewById<TextView>(R.id.alert_title)
        titleTextView.text = alert.alertHeaderText()
        val start = alert.effectiveStartDate().toString()
        val end = alert.effectiveEndDate().toString()
        val dateTextView = holder.itemView.findViewById<TextView>(R.id.alert_date)
        dateTextView.text = "$start - $end"
        holder.itemView.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_action_home_to_alertDetailsFragment))
    }

    override fun getItemCount(): Int {
        return alertList.size
    }
}