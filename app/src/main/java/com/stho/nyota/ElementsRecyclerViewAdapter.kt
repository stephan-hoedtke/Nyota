package com.stho.nyota

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stho.nyota.databinding.FragmentElementListEntryBinding
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.universe.SolarSystem


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
        getElementByIndex(position)?.also {
            holder.bind(it)
        }
    }

    override fun getItemCount(): Int = entries.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding: FragmentElementListEntryBinding = FragmentElementListEntryBinding.bind(view)

        fun bind(element: IElement) {
            binding.image.setImageResource(element.imageId)
            binding.name.text = element.toString()
            binding.position.text = element.position.toString()
            binding.root.setOnClickListener { getElementByIndex(adapterPosition)?.also { onItemClick?.invoke(it) } }
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

