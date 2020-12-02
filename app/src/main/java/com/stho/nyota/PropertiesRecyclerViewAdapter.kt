package com.stho.nyota

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stho.nyota.sky.universe.Satellite
import com.stho.nyota.sky.utilities.IIconNameValue
import com.stho.nyota.sky.utilities.IProperty
import com.stho.nyota.sky.utilities.PropertyList
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_satellite.view.image
import kotlinx.android.synthetic.main.property_list_entry.view.*


/**
 * [RecyclerView.Adapter] that can display a [Satellite].
 */
class PropertiesRecyclerViewAdapter : RecyclerView.Adapter<PropertiesRecyclerViewAdapter.ViewHolder>() {

    private var entries: PropertyList = PropertyList()
    var onItemClick: ((IProperty) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.property_list_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry: IProperty = entries[position]
        holder.bind(entry)
    }

    override fun getItemCount(): Int = entries.size

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(entry: IProperty) {
            containerView.image.setImageResource(entry.imageId)
            containerView.name.text = entry.name
            containerView.value.text = entry.value
        }

        init {
            containerView.setOnClickListener {
                onItemClick?.invoke(entries[adapterPosition])
            }
        }
    }

    fun updateProperties(properties: PropertyList) {
        entries = properties
        notifyDataSetChanged()
    }
}