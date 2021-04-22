package com.xpyx.nokiahslvisualisation.fragments.list


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.data.DataTrafficItem
import java.util.*

class TrafficListAdapter(private val context: Context) : RecyclerView.Adapter<TrafficListAdapter.TrafficViewHolder>(), Filterable {

    private var trafficList = mutableListOf<DataTrafficItem>()
    private var trafficListFull = mutableListOf<DataTrafficItem>()

    class TrafficViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrafficViewHolder {
        return TrafficViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.traffic_list_view_item, parent, false))
    }

    override fun onBindViewHolder(holder: TrafficViewHolder, position: Int) {
        val traffic = trafficList[position]
        val titleTextView = holder.itemView.findViewById<TextView>(R.id.title_text_view)
        titleTextView.text = traffic.trafficItemDescriptionElement?.get(0)!!.trafficItemDescriptionElementValue
        val problemTextView = holder.itemView.findViewById<TextView>(R.id.description_text_view)
        problemTextView.text = context.getString(R.string.warning_description, traffic.start_time, traffic.end_time, traffic.traffic_item_type_desc)
        val trafficId = traffic.traffic_item_id
        val positionArgument: Bundle = bundleOf("TRAFFIC" to trafficId)

        holder.itemView.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_action_list_to_action_bus, positionArgument))

    }

    override fun getItemCount(): Int {
        return trafficList.size
    }

    fun setData(traffic: List<DataTrafficItem>) {
        this.trafficList = traffic as MutableList<DataTrafficItem>
        this.trafficListFull = traffic.toMutableList()
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return searchFilter
    }



    private val searchFilter: Filter = object : Filter() {
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            trafficList = results?.values as MutableList<DataTrafficItem>
            notifyDataSetChanged()
        }


        @SuppressLint("LogNotTimber")
        override fun performFiltering(constraint: CharSequence?): FilterResults {

            val listOfTrafficItemId = mutableListOf<Long>()
            val filteredTrafficList = mutableListOf<DataTrafficItem>()
            if (constraint.isNullOrEmpty()) {
                filteredTrafficList.addAll(trafficListFull)

            } else {
                val buffer = constraint.toString().toLowerCase(Locale.ROOT).trim()
                val filterPattern = buffer.split(";")
                for (filter in filterPattern){
                    Log.d("FILTERADAPTERLIST", filter)
                }
                for (item in trafficListFull) {

                    // Check criticality filter and..
                    if (filterPattern.contains(item.criticality?.ityDescription.toString())) {
                        // ..add if found
                        filteredTrafficList.add(item)
                        // Add so later filters don't add duplicates
                        listOfTrafficItemId.add(item.traffic_item_id!!)
                        Log.d("FILTERADAPTERcriticality", "Added: ${item.traffic_item_id}")
                    } else if (filterPattern.contains("major") && item.criticality?.ityDescription.toString() != "major") {
                        // If filter not matching, add to id-list so later filters don't add item
                        listOfTrafficItemId.add(item.traffic_item_id!!)
                    } else if (filterPattern.contains("critical") && item.criticality?.ityDescription.toString() != "critical") {
                        // If filter not matching, add to id-list so later filters don't add item
                        listOfTrafficItemId.add(item.traffic_item_id!!)
                    } else if (filterPattern.contains("minor") && item.criticality?.ityDescription.toString() != "minor") {
                        // If filter not matching, add to id-list so later filters don't add item
                        listOfTrafficItemId.add(item.traffic_item_id!!)
                    }
                    if (filterPattern.contains("response_vehicles")) {
                        if (!listOfTrafficItemId.contains(item.traffic_item_id)  && item.traffic_item_detail?.trafficItemDetailIncident != null && item.traffic_item_detail.trafficItemDetailIncident.responseVehicles == true) {
                            filteredTrafficList.add(item)
                            listOfTrafficItemId.add(item.traffic_item_id!!)
                            Log.d("FILTERADAPTERresponse_vehicles", "Added: ${item.traffic_item_id}")
                        } else if (listOfTrafficItemId.contains(item.traffic_item_id) && (item.traffic_item_detail?.trafficItemDetailIncident == null || item.traffic_item_detail.trafficItemDetailIncident.responseVehicles == false)) {
                            // Check if item already on filtered list and not matching new filters, remove it.
                            if (filteredTrafficList.contains(item)) {
                                filteredTrafficList.remove(item)
                                Log.d("FILTERADAPTERresponse_vehicles", "Removed: ${item.traffic_item_id}")
                            }
                        } else if (!listOfTrafficItemId.contains(item.traffic_item_id) && (item.traffic_item_detail?.trafficItemDetailIncident == null || item.traffic_item_detail.trafficItemDetailIncident.responseVehicles == false)) {
                            listOfTrafficItemId.add(item.traffic_item_id!!)
                        } else if (filteredTrafficList.contains(item)) {
                            Log.d("FILTERADAPTERresponse_vehicles", "Kept: ${item.traffic_item_id}")
                        }
                    }
                    if (filterPattern.contains("road_closed")) {
                        if (!listOfTrafficItemId.contains(item.traffic_item_id) && item.traffic_item_detail?.trafficItemDetailRoadClosed == true) {
                            filteredTrafficList.add(item)
                            listOfTrafficItemId.add(item.traffic_item_id!!)
                            Log.d("FILTERADAPTERroad_closed", "Added: ${item.traffic_item_id}")
                        } else if (listOfTrafficItemId.contains(item.traffic_item_id) && item.traffic_item_detail?.trafficItemDetailRoadClosed == false) {
                            // Check if item already on filtered list and not matching new filters, remove it.
                            if (filteredTrafficList.contains(item)) {
                                filteredTrafficList.remove(item)
                                Log.d("FILTERADAPTERroad_closed", "Removed: ${item.traffic_item_id}")
                            }
                            // If filter not matching, add to id-list so later filters don't add item
                        } else if (!listOfTrafficItemId.contains(item.traffic_item_id) && item.traffic_item_detail?.trafficItemDetailRoadClosed == false) {
                            listOfTrafficItemId.add(item.traffic_item_id!!)
                        } else if (filteredTrafficList.contains(item)) {
                            Log.d("FILTERADAPTERroad_closed", "Kept: ${item.traffic_item_id}")
                        }
                    }
                    if (filterPattern.contains("incident")) {
                        if (!listOfTrafficItemId.contains(item.traffic_item_id) && item.traffic_item_detail?.trafficItemDetailIncident != null && !item.traffic_item_detail.trafficItemDetailIncident.equals("")) {
                            filteredTrafficList.add(item)
                            listOfTrafficItemId.add(item.traffic_item_id!!)
                            Log.d("FILTERADAPTERincident", "Added: ${item.traffic_item_id}")
                        } else if (listOfTrafficItemId.contains(item.traffic_item_id) && (item.traffic_item_detail?.trafficItemDetailIncident == null || item.traffic_item_detail.trafficItemDetailIncident.equals(""))) {
                            if (filteredTrafficList.contains(item)) {
                                filteredTrafficList.remove(item)
                                Log.d("FILTERADAPTERincident", "Removed: ${item.traffic_item_id}")
                            }
                        } else if (!listOfTrafficItemId.contains(item.traffic_item_id) && (item.traffic_item_detail?.trafficItemDetailIncident == null || item.traffic_item_detail.trafficItemDetailIncident.equals(""))) {
                            listOfTrafficItemId.add(item.traffic_item_id!!)
                        } else if (filteredTrafficList.contains(item)) {
                            Log.d("FILTERADAPTERincident", "Kept: ${item.traffic_item_id}")
                        }
                    }
                    if (filterPattern.contains("event")){
                        if (!listOfTrafficItemId.contains(item.traffic_item_id) && item.traffic_item_detail?.trafficItemDetailEvent != null && !item.traffic_item_detail.trafficItemDetailEvent.equals("")) {
                            filteredTrafficList.add(item)
                            listOfTrafficItemId.add(item.traffic_item_id!!)
                            Log.d("FILTERADAPTERevent", "Added: ${item.traffic_item_id}")
                        } else if (listOfTrafficItemId.contains(item.traffic_item_id) && (item.traffic_item_detail?.trafficItemDetailEvent == null || item.traffic_item_detail.trafficItemDetailEvent.equals(""))) {
                            if (filteredTrafficList.contains(item)) {
                                filteredTrafficList.remove(item)
                                Log.d("FILTERADAPTERevent", "Removed: ${item.traffic_item_id}")
                            }
                        } else if (!listOfTrafficItemId.contains(item.traffic_item_id) && (item.traffic_item_detail?.trafficItemDetailEvent == null || item.traffic_item_detail.trafficItemDetailEvent.equals(""))) {
                            listOfTrafficItemId.add(item.traffic_item_id!!)
                        } else if (filteredTrafficList.contains(item))  {
                            Log.d("FILTERADAPTERevent", "Kept: ${item.traffic_item_id}")
                        }
                    }
                    val distanceFilters = mutableListOf<Double>()
                    for (filter in filterPattern) {
                        if (filter.contains(".")){
                            val value = filter.toDouble()
                            Log.d("FILTERDOUBLE", value.toString())
                            distanceFilters.add(value)
                        }
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredTrafficList
            return results
        }
    }
}