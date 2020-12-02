package com.stho.nyota.ui.planets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stho.nyota.R
import com.stho.nyota.sky.universe.AbstractSolarSystemElement
import com.stho.nyota.sky.universe.SolarSystem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_planets_list_entry.view.*
import kotlinx.android.synthetic.main.fragment_satellite.view.image
import kotlinx.android.synthetic.main.property_list_entry.view.name

/**
 * [RecyclerView.Adapter] that can display a [Satellite].
 */
class PlanetListRecyclerViewAdapter : RecyclerView.Adapter<PlanetListRecyclerViewAdapter.ViewHolder>() {

    private var entries: List<AbstractSolarSystemElement> = ArrayList<AbstractSolarSystemElement>()
    var onItemClick: ((AbstractSolarSystemElement) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_planets_list_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val element = entries[position]
        holder.bind(element)
    }

    override fun getItemCount(): Int = entries.size

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        init {
            containerView.setOnClickListener {
                onItemClick?.invoke(entries[adapterPosition])
            }
        }
        fun bind(element: AbstractSolarSystemElement) {
            containerView.image.setImageResource(element.imageId)
            containerView.name.text = element.name
            containerView.position.text = element.position.toString()
        }
    }

    fun updateSolarSystem(solarSystem: SolarSystem) {
        entries = solarSystem.elements
        notifyDataSetChanged()
    }
}