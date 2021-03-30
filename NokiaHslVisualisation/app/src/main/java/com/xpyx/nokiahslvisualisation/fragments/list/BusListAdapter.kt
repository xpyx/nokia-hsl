package com.xpyx.nokiahslvisualisation.fragments.list


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.data.TrafficItem
import com.xpyx.nokiahslvisualisation.model.traffic.TRAFFIC_ITEM
import org.w3c.dom.Text

class BusListAdapter() : RecyclerView.Adapter<BusListAdapter.BusViewHolder>() {

    private var busList = emptyList<TrafficItem>()

    class BusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusViewHolder {
        return BusViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_view_item, parent, false))
    }

    override fun onBindViewHolder(holder: BusViewHolder, position: Int) {
        val bus = busList[position]
        val titleTextView = holder.itemView.findViewById<TextView>(R.id.title_text_view)
        titleTextView.text = bus.traffic_item_id.toString()
        val problemTextView = holder.itemView.findViewById<TextView>(R.id.problem_text_view)
        problemTextView.text = "Start: ${bus.start_time}, End: ${bus.end_time}, Type: ${bus.traffic_item_type_desc}"
        holder.itemView.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_action_list_to_action_bus))

    }

    override fun getItemCount(): Int {
        return busList.size
    }

    fun setData(traffic: List<TrafficItem>) {
        this.busList = traffic
        notifyDataSetChanged()
    }
}