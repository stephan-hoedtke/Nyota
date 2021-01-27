package com.stho.nyota.ui.satellites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stho.nyota.R
import com.stho.nyota.databinding.FragmentSatelliteListEntryBinding
import com.stho.nyota.sky.universe.Satellite
import com.stho.nyota.sky.universe.Satellites


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

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding: FragmentSatelliteListEntryBinding = FragmentSatelliteListEntryBinding.bind(view)

        fun bind(satellite: Satellite) {
            binding.image.setImageResource(satellite.imageId)
            binding.name.text = satellite.name
            binding.noradSatelliteNumber.text = satellite.noradSatelliteNumber.toString()
            binding.displayName.text = satellite.friendlyName
            binding.position.text = "${satellite.position}"
            binding.root.setOnClickListener { satellites.findSatelliteByIndex(adapterPosition)?.also { select(it) } }
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
