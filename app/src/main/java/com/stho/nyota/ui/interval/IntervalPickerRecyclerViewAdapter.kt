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

    var selectedInterval: Interval = Interval.HOUR
        set(value) {
            if (field != value) {
                field = value
                notifyDataSetChanged()
            }
        }

    var onItemClick: ((Interval) -> Unit)? = null
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
            // TODO: check if absoluteAdapterPosition or bindingAdapterPosition would be correct!!
            binding.root.setOnClickListener { getIntervalByIndex(absoluteAdapterPosition)?.also { onItemClick?.invoke(it) }  }
            binding.radioButton.setOnClickListener { getIntervalByIndex(absoluteAdapterPosition)?.also {  onSelectionChanged?.invoke(it) } }
        }
    }

    private fun isSelected(interval: Interval): Boolean =
        interval == selectedInterval
}
