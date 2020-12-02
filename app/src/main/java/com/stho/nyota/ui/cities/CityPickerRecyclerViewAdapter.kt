package com.stho.nyota.ui.cities

import android.content.Context
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.stho.nyota.R
import com.stho.nyota.sky.utilities.Cities
import com.stho.nyota.sky.utilities.City
import com.stho.nyota.sky.utilities.createDefaultBerlin
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_city_picker_list_item.view.*
import kotlinx.android.synthetic.main.fragment_interval_picker_list_item.view.radioButton
import kotlinx.android.synthetic.main.fragment_interval_picker_list_item.view.textView


/**
 * [RecyclerView.Adapter] that can display a [Satellite].
 */
class CityPickerRecyclerViewAdapter(fragment: CityPickerFragment) : RecyclerView.Adapter<CityPickerRecyclerViewAdapter.ViewHolder>(), ISwipeToDeleteAdapter {

    private val context: Context = fragment.requireContext()
    private var cities: Cities = Cities()
    private var selectedCity: City = City.createDefaultBerlin()
    private var gestureDetector: GestureDetector

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
        cities.findCityByIndex(position)?.also { holder.bind(it) }
    }

    override fun getItemCount(): Int
        = cities.size

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(city: City) {
            val isSelected = isSelected(city)
            containerView.radioButton.isChecked = isSelected
            containerView.radioButton.setOnClickListener {
                cities.findCityByIndex(adapterPosition)?.also { select(it) }
            }
            containerView.isSelected = isSelected
            containerView.textView.text = city.nameEx
            containerView.setOnLongClickListener {
                edit(city)
                true
            }
            containerView.setOnTouchListener { view: View, motionEvent: MotionEvent? ->
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
                    edit(city)
                    return false
                }
                override fun onDoubleTapEvent(motionEvent: MotionEvent): Boolean {
                    return false
                }
            })
        }
    }

    override fun getForegroundLayer(viewHolder: RecyclerView.ViewHolder): View =
        (viewHolder as CityPickerRecyclerViewAdapter.ViewHolder).containerView.foreground_layer

    override fun getBackgroundColor(isCurrentlyActive: Boolean): Int =
        ContextCompat.getColor(context, if (isCurrentlyActive) R.color.colorSelectedBackground else R.color.colorBackground)

    fun select(city: City) {
        if (selectedCity != city) {
            selectedCity = city
            notifyDataSetChanged()
            onSelectionChanged?.invoke(city)
        }
    }

    fun edit(city: City) {
        onEdit?.invoke(city)
    }

    fun getSelectedCity(): City {
        return selectedCity
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