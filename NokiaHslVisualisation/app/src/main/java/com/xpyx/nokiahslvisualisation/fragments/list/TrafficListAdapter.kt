package com.xpyx.nokiahslvisualisation.fragments.list


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
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

        override fun performFiltering(constraint: CharSequence?): FilterResults {

            val filteredTrafficList = mutableListOf<DataTrafficItem>()
            if ((constraint == null || constraint.isEmpty())) {
                filteredTrafficList.addAll(trafficListFull)

            } else {
                val filterPattern: String = constraint.toString().toLowerCase(Locale.ROOT).trim()
                for (item in trafficListFull) {


                    when (filterPattern) {
                        "response_vehicles" -> if (item.traffic_item_detail?.trafficItemDetailIncident != null && item.traffic_item_detail.trafficItemDetailIncident.responseVehicles!!) filteredTrafficList.add(item)
                        "road_closed" -> if (item.traffic_item_detail?.trafficItemDetailRoadClosed!!) filteredTrafficList.add(item)
                        "incident" -> if (item.traffic_item_detail?.trafficItemDetailIncident != null && !item.traffic_item_detail.trafficItemDetailIncident.equals("")) filteredTrafficList.add(item)
                        "event" -> if (item.traffic_item_detail?.trafficItemDetailEvent != null && !item.traffic_item_detail.trafficItemDetailEvent.equals("")) filteredTrafficList.add(item)
                        "major" -> if (item.criticality?.ityDescription?.contains("major")!!) filteredTrafficList.add(item)
                        "critical" -> if (item.criticality?.ityDescription?.contains("critical")!!) filteredTrafficList.add(item)
                        "minor" -> if (item.criticality?.ityDescription?.contains("minor")!!) filteredTrafficList.add(item)
                    }
                    Log.d("FILTER", filterPattern)
                }
            }
            val results = FilterResults()
            results.values = filteredTrafficList
            return results
        }
    }
}