package com.xpyx.nokiahslvisualisation.fragments.home

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.data.AlertItem

class AlertListAdapter :
    RecyclerView.Adapter<AlertListAdapter.AlertViewHolder>() {

    private var alertList = emptyList<AlertItem>()

    class AlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        return AlertViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.alert_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alert = alertList[position]
        val titleTextView = holder.itemView.findViewById<TextView>(R.id.alert_title)
        val descTextView = holder.itemView.findViewById<TextView>(R.id.alert_desc)

        titleTextView.text = alert.alertHeaderText
        descTextView.text = alert.alertDescriptionText
        val start = alert.effectiveStartDate.toString()
        val end = alert.effectiveEndDate.toString()
        val dateTextView = holder.itemView.findViewById<TextView>(R.id.alert_date)
        "$start - $end".also { dateTextView.text = it }
        holder.itemView.setOnClickListener(
            Navigation.createNavigateOnClickListener(
                R.id.action_action_home_to_alertDetailsFragment))

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
        this.alertList = alerts
        notifyDataSetChanged()
    }
}