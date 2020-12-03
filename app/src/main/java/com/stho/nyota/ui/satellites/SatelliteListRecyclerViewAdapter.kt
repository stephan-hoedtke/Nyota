package com.stho.nyota.ui.satellites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stho.nyota.R
import com.stho.nyota.sky.universe.Satellite
import com.stho.nyota.sky.universe.Satellites


/**
 * [RecyclerView.Adapter] that can display a [Satellite].
 */
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

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener { satellites.findSatelliteByIndex(adapterPosition)?.also { select(it) } }
        }
        fun bind(satellite: Satellite) {
            view.image.setImageResource(satellite.imageId)
            view.name.text = satellite.name
            view.noradSatelliteNumber.text = satellite.noradSatelliteNumber.toString()
            view.displayName.text = satellite.displayName
            view.position.text = "${satellite.position}"
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

private val View.image: ImageView
    get() = findViewById<ImageView>(R.id.image)

private val View.name: TextView
    get() = findViewById<TextView>(R.id.name)

private val View.noradSatelliteNumber: TextView
    get() = findViewById<TextView>(R.id.noradSatelliteNumber)

private val View.displayName: TextView
    get() = findViewById<TextView>(R.id.displayName)

private val View.position: TextView
    get() = findViewById<TextView>(R.id.position)
