package com.xpyx.nokiahslvisualisation.fragments.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.fragments.list.FakeAlert
import org.w3c.dom.Text

class AlertListAdapter(private val alertList: MutableList<FakeAlert>) : RecyclerView.Adapter<AlertListAdapter.BusViewHolder>() {


    class BusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusViewHolder {
        return BusViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.alert_list_item, parent, false))

    }

    override fun onBindViewHolder(holder: BusViewHolder, position: Int) {
        val alert: FakeAlert = alertList[position]
        val titleTextView = holder.itemView.findViewById<TextView>(R.id.alert_title)
        titleTextView.text = alert.title
        val problemTextView = holder.itemView.findViewById<TextView>(R.id.alert_desc)
        problemTextView.text = alert.problem
        holder.itemView.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_action_home_to_alertDetailsFragment))

    }

    override fun getItemCount(): Int {
        return alertList.size
    }
}