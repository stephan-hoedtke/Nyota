package com.stho.nyota.ui.constellations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stho.nyota.R
import com.stho.nyota.databinding.FragmentConstellationListEntryBinding
import com.stho.nyota.sky.universe.Constellation
import com.stho.nyota.sky.universe.Constellations
import com.stho.nyota.sky.universe.IElement


class ConstellationListRecyclerViewAdapter : RecyclerView.Adapter<ConstellationListRecyclerViewAdapter.ViewHolder>() {

    private var constellations: List<Constellation> = ArrayList<Constellation>()
    private var selectedItem: Constellation? = null

    var onItemClick: ((Constellation) -> Unit)? = null
    var onItemLongClick: ((Constellation) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_constellation_list_entry, parent, false)
        return ViewHolder(view)
    }

    private fun getConstellationByIndex(position: Int): Constellation? =
        if (position >= 0 && position < constellations.size) constellations[position] else null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getConstellationByIndex(position)?.also {
            holder.bind(it)
        }
    }

    override fun getItemCount(): Int = constellations.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding: FragmentConstellationListEntryBinding = FragmentConstellationListEntryBinding.bind(view)

        fun bind(constellation: Constellation) {
            binding.image.setImageResource(constellation.imageId)
            binding.name.text = constellation.name
            binding.azimuth.text = constellation.position?.azimuthAsString
            binding.altitude.text = constellation.position?.altitudeAsString
            binding.hints.text = constellation.author
            binding.root.isSelected = isItemSelected(constellation)
            binding.root.setOnClickListener { getConstellationByIndex(adapterPosition)?.also { onItemClick?.invoke(it) } }
            binding.root.setOnLongClickListener {  getConstellationByIndex(adapterPosition)?.also { onItemLongClick?.invoke(it) }; true }
        }
    }

    fun updateConstellations(constellations: List<Constellation>) {
        this.constellations = constellations
        notifyDataSetChanged()
    }

    fun selectItem(constellation: Constellation?) {
        selectedItem = constellation
        notifyDataSetChanged()
    }

    private fun isItemSelected(constellation: Constellation): Boolean =
        constellation == selectedItem
}

