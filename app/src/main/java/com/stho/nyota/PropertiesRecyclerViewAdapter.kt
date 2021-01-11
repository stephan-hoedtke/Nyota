package com.stho.nyota

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stho.nyota.databinding.PropertyListEntryBinding
import com.stho.nyota.sky.universe.Satellite
import com.stho.nyota.sky.utilities.IProperty
import com.stho.nyota.sky.utilities.PropertyList

/**
 * [RecyclerView.Adapter] that can display a [Satellite].
 */
class PropertiesRecyclerViewAdapter : RecyclerView.Adapter<PropertiesRecyclerViewAdapter.ViewHolder>() {

    private var entries: PropertyList = PropertyList()
    var onItemClick: ((IProperty) -> Unit)? = null
    var onItemLongClick: ((IProperty) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.property_list_entry, parent, false)
        return ViewHolder(view)
    }

    private fun getPropertyByIndex(position: Int): IProperty? =
        if (position >= 0 && position < entries.size) entries[position] else null


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getPropertyByIndex(position)?.also {
            holder.bind(it)
        }
    }

    override fun getItemCount(): Int = entries.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding: PropertyListEntryBinding = PropertyListEntryBinding.bind(view)

        fun bind(entry: IProperty) {
            binding.image.setImageResource(entry.imageId)
            binding.name.text = entry.name
            binding.value.text = entry.value
            binding.root.setOnClickListener { onPropertyClick(adapterPosition) }
            binding.root.setOnLongClickListener { onPropertyLongClick(adapterPosition); true }
        }
    }

    fun updateProperties(properties: PropertyList) {
        entries = properties
        notifyDataSetChanged()
    }

    private fun onPropertyClick(position: Int) =
        getPropertyByIndex(position)?.let { onItemClick?.invoke(it) }

    private fun onPropertyLongClick(position: Int) =
        getPropertyByIndex(position)?.let { onItemLongClick?.invoke(it) }

}
