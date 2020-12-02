package com.stho.nyota.ui.interval

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stho.nyota.Interval
import com.stho.nyota.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_interval_picker_list_item.view.*


/**
 * [RecyclerView.Adapter] that can display a [Satellite].
 */
class IntervalPickerRecyclerViewAdapter : RecyclerView.Adapter<IntervalPickerRecyclerViewAdapter.ViewHolder>() {

    private var entries: Array<Interval> = Interval.values()
    private var selectedInterval: Interval = Interval.HOUR

    var onSelectionChanged: ((Interval) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_interval_picker_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val interval = entries[position]
        holder.bind(interval)
    }

    override fun getItemCount(): Int
        = entries.size

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        init {
            containerView.setOnClickListener {
                select(entries[adapterPosition])
             }
            containerView.radioButton.setOnClickListener {
                select(entries[adapterPosition])
            }
        }
        fun bind(interval: Interval) {
            val isSelected = isSelected(interval)
            containerView.radioButton.isChecked = isSelected
            containerView.isSelected = isSelected
            containerView.textView.text = interval.toString()
        }
    }

    fun select(interval: Interval) {
        if (selectedInterval != interval) {
            selectedInterval = interval
            notifyDataSetChanged()
            onSelectionChanged?.invoke(interval)
        }
    }

    fun getSelectedInterval(): Interval {
        return selectedInterval
    }

    private fun isSelected(interval: Interval): Boolean {
        return interval == selectedInterval
    }
}