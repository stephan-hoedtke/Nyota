package com.stho.nyota

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stho.nyota.sky.universe.Satellite
import com.stho.nyota.sky.utilities.IProperty
import com.stho.nyota.sky.utilities.PropertyList

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

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(entry: IProperty) {
            view.image?.setImageResource(entry.imageId)
            view.name?.text = entry.name
            view.value?.text = entry.value
        }

        init {
            view.setOnClickListener {
                onItemClick?.invoke(entries[adapterPosition])
            }
        }
    }

    fun updateProperties(properties: PropertyList) {
        entries = properties
        notifyDataSetChanged()
    }
}

private val View.image: ImageView?
    get() = findViewById<ImageView>(R.id.image)

private val View.name: TextView?
    get() = findViewById<TextView>(R.id.name)

private val View.value: TextView?
    get() = findViewById<TextView>(R.id.value)