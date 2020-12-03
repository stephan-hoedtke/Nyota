package com.stho.nyota.ui.constellations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stho.nyota.R
import com.stho.nyota.sky.universe.Constellation
import com.stho.nyota.sky.universe.Constellations


class ConstellationListRecyclerViewAdapter : RecyclerView.Adapter<ConstellationListRecyclerViewAdapter.ViewHolder>() {

    private var entries: List<Constellation> = ArrayList<Constellation>()
    var onItemClick: ((Constellation) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_constellation_list_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val constellation = entries[position]
        holder.bind(constellation)
    }

    override fun getItemCount(): Int = entries.size

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener {
                onItemClick?.invoke(entries[adapterPosition])
            }
        }
        fun bind(constellation: Constellation) {
            view.image?.setImageResource(constellation.imageId)
            view.name?.text = constellation.name
            view.position?.text = constellation.position.toString()
        }
    }

    fun updateConstellations(constellations: Constellations) {
        entries = constellations.values.toList()
        notifyDataSetChanged()
    }
}

private val View.image: ImageView?
    get() = findViewById<ImageView>(R.id.image)

private val View.name: TextView?
    get() = findViewById<TextView>(R.id.name)

private val View.position: TextView?
    get() = findViewById<TextView>(R.id.position)