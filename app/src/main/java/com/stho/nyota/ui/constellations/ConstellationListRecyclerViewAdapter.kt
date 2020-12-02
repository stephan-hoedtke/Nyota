package com.stho.nyota.ui.constellations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stho.nyota.R
import com.stho.nyota.sky.universe.Constellation
import com.stho.nyota.sky.universe.Constellations
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_constellation_list_entry.view.*

/**
 * [RecyclerView.Adapter] that can display a [Constallation].
 */
class ConstellationListRecyclerViewAdapter : RecyclerView.Adapter<ConstellationListRecyclerViewAdapter.ViewHolder>() {

    private var entries: List<Constellation> = ArrayList<Constellation>()
    var onItemClick: ((Constellation) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_constellation_list_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val constellation = entries[position]
        holder.bind(constellation)
    }

    override fun getItemCount(): Int = entries.size

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        init {
            containerView.setOnClickListener {
                onItemClick?.invoke(entries[adapterPosition])
            }
        }
        fun bind(constellation: Constellation) {
            containerView.image.setImageResource(constellation.imageId)
            containerView.name.text = constellation.name
            containerView.position.text = constellation.position.toString()
        }
    }

    fun updateConstellations(constellations: Constellations) {
        entries = constellations.values.toList()
        notifyDataSetChanged()
    }
}
