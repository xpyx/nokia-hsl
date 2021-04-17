package com.xpyx.nokiahslvisualisation.fragments.list


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.data.DataTrafficItem

class TrafficListAdapter(private val context: Context) : RecyclerView.Adapter<TrafficListAdapter.TrafficViewHolder>() {

    private var trafficList = emptyList<DataTrafficItem>()

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
        this.trafficList = traffic
        notifyDataSetChanged()
    }
}