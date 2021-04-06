package com.xpyx.nokiahslvisualisation.fragments.list


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.xpyx.nokiahslvisualisation.R
import org.w3c.dom.Text

class BusListAdapter(private val busList: MutableList<FakeBus>) : RecyclerView.Adapter<BusListAdapter.BusViewHolder>() {


    class BusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusViewHolder {
        return BusViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_view_item, parent, false))
    }

    override fun onBindViewHolder(holder: BusViewHolder, position: Int) {
        val bus: FakeBus = busList[position]
        val titleTextView = holder.itemView.findViewById<TextView>(R.id.title_text_view)
        titleTextView.text = bus.title
        val problemTextView = holder.itemView.findViewById<TextView>(R.id.problem_text_view)
        problemTextView.text = bus.problem
        holder.itemView.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_action_list_to_action_bus))
    }

    override fun getItemCount(): Int {
        return busList.size
    }
}