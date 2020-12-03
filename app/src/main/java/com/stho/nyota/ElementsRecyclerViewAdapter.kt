package com.stho.nyota

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener {
                getElementByIndex(adapterPosition)?.also { onItemClick?.invoke(it) }
            }
        }
        fun bind(element: IElement) {
            view.image?.setImageResource(element.imageId)
            view.name?.text = element.name
            view.position?.text = element.position.toString()
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

private val View.image: ImageView?
    get() = findViewById<ImageView>(R.id.image)

private val View.name: TextView?
    get() = findViewById<TextView>(R.id.name)

private val View.position: TextView?
    get() = findViewById<TextView>(R.id.position)