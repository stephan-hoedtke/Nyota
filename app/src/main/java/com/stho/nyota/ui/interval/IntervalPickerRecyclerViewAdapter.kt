package com.stho.nyota.ui.interval

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stho.nyota.Interval
import com.stho.nyota.R
import com.stho.nyota.databinding.FragmentIntervalPickerListItemBinding


class IntervalPickerRecyclerViewAdapter : RecyclerView.Adapter<IntervalPickerRecyclerViewAdapter.ViewHolder>() {

    private var entries: Array<Interval> = Interval.values()
    private var selectedInterval: Interval = Interval.HOUR

    var onSelectionChanged: ((Interval) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_interval_picker_list_item, parent, false)
        return ViewHolder(view)
    }

    private fun getIntervalByIndex(position: Int): Interval? =
        if (position >= 0 && position < entries.size) entries[position] else null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getIntervalByIndex(position)?.also {
            holder.bind(it)
        }
    }

    override fun getItemCount(): Int
        = entries.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding: FragmentIntervalPickerListItemBinding = FragmentIntervalPickerListItemBinding.bind(view)

        fun bind(interval: Interval) {
            val isSelected = isSelected(interval)
            binding.radioButton.isChecked = isSelected
            binding.textView.text = interval.toString()
            binding.root.isSelected = isSelected
            binding.root.setOnClickListener { select(entries[adapterPosition]) }
            binding.radioButton.setOnClickListener { select(entries[adapterPosition]) }
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
