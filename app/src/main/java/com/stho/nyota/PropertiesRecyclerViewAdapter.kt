package com.stho.nyota

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stho.nyota.databinding.PropertyListEntryBinding
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.universe.Satellite
import com.stho.nyota.sky.utilities.IProperty
import com.stho.nyota.sky.utilities.Property
import com.stho.nyota.sky.utilities.PropertyKeyType
import com.stho.nyota.sky.utilities.PropertyList

/**
 * [RecyclerView.Adapter] that can display a [Satellite].F
 */
class PropertiesRecyclerViewAdapter : RecyclerView.Adapter<PropertiesRecyclerViewAdapter.ViewHolder>() {

    private var entries: PropertyList = PropertyList()
    private var selectedItem: IElement? = null

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
            binding.hints.text = entry.hints
            binding.hints.visibility = if (entry.hasHints) View.VISIBLE else View.GONE
            binding.root.isSelected = isItemSelected(entry)
            binding.root.setOnClickListener { onPropertyClick(absoluteAdapterPosition) }
            binding.root.setOnLongClickListener { onPropertyLongClick(absoluteAdapterPosition); true }
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

    fun selectItem(element: IElement?) {
        selectedItem = element
        notifyDataSetChanged()
    }

    private fun isItemSelected(entry: IProperty): Boolean =
        entry.keyType == PropertyKeyType.STAR && entry.key == selectedItem?.key

}
