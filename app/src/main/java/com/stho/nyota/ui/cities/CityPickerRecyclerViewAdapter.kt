package com.stho.nyota.ui.cities

import android.content.Context
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.stho.nyota.R
import com.stho.nyota.databinding.FragmentCityPickerListItemBinding
import com.stho.nyota.sky.utilities.Cities
import com.stho.nyota.sky.utilities.City
import com.stho.nyota.sky.utilities.Formatter
import com.stho.nyota.sky.utilities.createDefaultBerlinBuch

// TODO: onTouchLister without PerformClick

class CityPickerRecyclerViewAdapter(fragment: CityPickerFragment) : RecyclerView.Adapter<CityPickerRecyclerViewAdapter.ViewHolder>(), ISwipeToDeleteAdapter {

    private val context: Context = fragment.requireContext()
    private var gestureDetector: GestureDetector
    private var cities: Cities = Cities()

    var selectedCity: City = City.createDefaultBerlinBuch()
        set(value) {
            if (field != value) {
                field = value
                notifyDataSetChanged()
            }
        }

    var onSelectionChanged: ((City) -> Unit)? = null
    var onEdit: ((City) -> Unit)? = null
    var onDelete: ((Int, City) -> Unit)? = null

    init {
        setHasStableIds(true) // to avoid flicker on update during swipe: See also getItemId
        gestureDetector = GestureDetector(context, SimpleOnGestureListener())
    }

    override fun getItemId(position: Int): Long =
        cities.findCityByIndex(position)?.uniqueTransientId ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.fragment_city_picker_list_item,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        cities.findCityByIndex(position)?.also {
            holder.bind(it)
        }
    }

    override fun getItemCount(): Int
        = cities.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding: FragmentCityPickerListItemBinding = FragmentCityPickerListItemBinding.bind(view)

        fun bind(city: City) {
            val isSelected = isSelected(city)
            binding.radioButton.isChecked = isSelected
            binding.radioButton.setOnClickListener { cities.findCityByIndex(adapterPosition)?.also { onSelectionChanged?.invoke(it) } }
            binding.textViewName.text = city.nameEx
            binding.textViewDistance.text = Formatter.toDistanceString(city.distanceInKm)
            binding.root.isSelected = isSelected
            binding.root.setOnLongClickListener {
                onEdit?.invoke(city)
                true
            }
            binding.root.setOnTouchListener { view: View, motionEvent: MotionEvent? ->
                gestureDetector.onTouchEvent(motionEvent)
                view.performClick()
                false
            }
            gestureDetector.setIsLongpressEnabled(true)
            gestureDetector.setOnDoubleTapListener(object : GestureDetector.OnDoubleTapListener {
                override fun onSingleTapConfirmed(motionEvent: MotionEvent): Boolean {
                    return false
                }
                override fun onDoubleTap(motionEvent: MotionEvent): Boolean {
                    onEdit?.invoke(city)
                    return false
                }
                override fun onDoubleTapEvent(motionEvent: MotionEvent): Boolean {
                    return false
                }
            })
        }

        internal val foregroundLayer: View = binding.foregroundLayer
    }

    override fun getForegroundLayer(viewHolder: RecyclerView.ViewHolder): View =
        (viewHolder as CityPickerRecyclerViewAdapter.ViewHolder).foregroundLayer

    override fun getBackgroundColor(isCurrentlyActive: Boolean): Int =
        ContextCompat.getColor(context, if (isCurrentlyActive) R.color.colorSelectedBackground else R.color.colorBackground)

    fun select(city: City) {
        if (selectedCity != city) {
            selectedCity = city
            notifyDataSetChanged()
            onSelectionChanged?.invoke(city)
        }
    }

    fun updateCities(cities: Cities) {
        this.cities = cities
        notifyDataSetChanged()
    }

    private fun isSelected(city: City): Boolean {
        return city.matches(selectedCity)
    }

    override fun delete(position: Int) {
        val city = cities[position]
        onDelete?.invoke(position, city)
    }

    fun updateDelete(position: Int) {
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    fun updateUndoDelete(position: Int) {
        notifyItemInserted(position)
        notifyDataSetChanged()
    }
}
