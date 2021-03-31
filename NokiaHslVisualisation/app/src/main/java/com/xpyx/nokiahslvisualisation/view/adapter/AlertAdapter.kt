package com.xpyx.nokiahslvisualisation.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xpyx.nokiahslvisualisation.AlertsListQuery
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.databinding.ItemAlertBinding


class AlertAdapter :
    ListAdapter<AlertsListQuery.Alert, AlertViewHolder>(AlertDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val binding: ItemAlertBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_alert,
            parent,
            false
        )
        return AlertViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        holder.binding.alert = getItem(position)
    }

}

class AlertViewHolder(val binding: ItemAlertBinding) : RecyclerView.ViewHolder(binding.root)

class AlertDiffUtil : DiffUtil.ItemCallback<AlertsListQuery.Alert>() {

    override fun areItemsTheSame(
        oldItem: AlertsListQuery.Alert,
        newItem: AlertsListQuery.Alert
    ): Boolean {
        return oldItem.alertUrl == newItem.alertUrl
    }

    override fun areContentsTheSame(
        oldItem: AlertsListQuery.Alert,
        newItem: AlertsListQuery.Alert
    ): Boolean {
        return oldItem == newItem
    }

}