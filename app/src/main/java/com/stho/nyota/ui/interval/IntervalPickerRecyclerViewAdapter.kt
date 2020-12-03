package com.stho.nyota.ui.interval

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stho.nyota.Interval
import com.stho.nyota.R


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

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        init {
            view.setOnClickListener { select(entries[adapterPosition]) }
            view.radioButton.setOnClickListener { select(entries[adapterPosition]) }
        }

        fun bind(interval: Interval) {
            val isSelected = isSelected(interval)
            view.radioButton.isChecked = isSelected
            view.isSelected = isSelected
            view.textView.text = interval.toString()
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

private val View.radioButton: RadioButton
    get() = findViewById(R.id.radioButton)

private val View.textView: TextView
    get() = findViewById(R.id.textView)

// TODO: use view binding correctly in recycler view adapter