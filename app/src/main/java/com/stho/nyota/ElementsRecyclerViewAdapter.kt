package com.stho.nyota

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stho.nyota.sky.universe.AbstractElement
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.universe.SolarSystem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_element_list_entry.view.*
import kotlinx.android.synthetic.main.fragment_satellite.view.image
import kotlinx.android.synthetic.main.property_list_entry.view.name

/**
 * [RecyclerView.Adapter] that can display a [Satellite].
 */
class ElementsRecyclerViewAdapter : RecyclerView.Adapter<ElementsRecyclerViewAdapter.ViewHolder>() {

    private var entries: List<IElement> = ArrayList()

    var onItemClick: ((IElement) -> Unit)? = null

    private fun getElementByIndex(position: Int): IElement? =
        if (position >= 0 && position < entries.size) entries[position] else null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_element_list_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getElementByIndex(position)?.also { holder.bind(it) }
    }

    override fun getItemCount(): Int = entries.size

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        init {
            containerView.setOnClickListener {
                getElementByIndex(adapterPosition)?.also { onItemClick?.invoke(it) }
            }
        }
        fun bind(element: IElement) {
            containerView.image.setImageResource(element.imageId)
            containerView.name.text = element.name
            containerView.position.text = element.position.toString()
        }
    }

    fun updateSolarSystem(solarSystem: SolarSystem) {
        entries = solarSystem.elements
        notifyDataSetChanged()
    }

    fun updateElementsUseNewList(elements: List<IElement>) {
        entries = elements
        notifyDataSetChanged()
    }

    fun updateElementsUseExistingList() {
        notifyDataSetChanged()
    }
}