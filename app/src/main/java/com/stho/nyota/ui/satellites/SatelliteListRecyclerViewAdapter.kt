package com.stho.nyota.ui.satellites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stho.nyota.R
import com.stho.nyota.sky.universe.Satellite
import com.stho.nyota.sky.universe.Satellites
import com.stho.nyota.sky.utilities.City
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_satellite.view.image
import kotlinx.android.synthetic.main.fragment_satellite_list_entry.view.*
import kotlinx.android.synthetic.main.property_list_entry.view.name


/**
 * [RecyclerView.Adapter] that can display a [Satellite].
 */
class SatelliteListRecyclerViewAdapter : RecyclerView.Adapter<SatelliteListRecyclerViewAdapter.ViewHolder>() {

    private var satellites: Satellites = Satellites()
    var onItemClick: ((Satellite) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_satellite_list_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        satellites.findSatelliteByIndex(position)?.also { holder.bind(it) }
    }

    override fun getItemCount(): Int = satellites.size

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        init {
            containerView.setOnClickListener {
                satellites.findSatelliteByIndex(adapterPosition)?.also { select(it) }
            }
        }
        fun bind(satellite: Satellite) {
            containerView.image.setImageResource(satellite.imageId)
            containerView.name.text = satellite.name
            containerView.noradSatelliteNumber.text = satellite.noradSatelliteNumber.toString()
            containerView.displayName.text = satellite.displayName
            containerView.position.text = "${satellite.position}"
        }
    }

    fun updateSatellites(satellites: Satellites) {
        this.satellites = satellites
        notifyDataSetChanged()
    }

    fun select(satellite: Satellite) {
        onItemClick?.invoke(satellite)
    }
}
