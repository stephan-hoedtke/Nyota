package com.stho.nyota.ui.moment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.stho.nyota.AbstractFragment
import com.stho.nyota.R
import com.stho.nyota.createViewModel
import com.stho.nyota.sky.utilities.Formatter
import com.stho.nyota.sky.utilities.Moment
import kotlinx.android.synthetic.main.fragment_moment.view.*
import kotlinx.android.synthetic.main.time_interval_footer.view.*
import java.util.*


class MomentFragment : AbstractFragment(),  DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var viewModel: MomentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel(MomentViewModel::class.java)
        setHasOptionsMenu(true)
    }

    override val abstractViewModel: IAbstractViewModel
        get() = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_moment, container, false)

        viewModel.currentAutomaticMomentLD.observe(viewLifecycleOwner, { moment -> updateAutomaticMoment(moment) })
        viewModel.momentLD.observe(viewLifecycleOwner, { moment -> updateMoment(moment) })
        viewModel.updateTimeAutomaticallyLD.observe(viewLifecycleOwner, { moment -> updateTimeAutomatically(moment) })
        viewModel.intervalLD.observe(viewLifecycleOwner, { interval -> view?.interval?.text = interval.name })

        root.checkBoxAutomatic.setOnClickListener { viewModel.toggleAutomatic() }
        root.buttonNext.setOnClickListener { viewModel.onNext() }
        root.buttonPrevious.setOnClickListener { viewModel.onPrevious() }
        root.interval.setOnClickListener { onPickInterval() }
        root.editDate.setOnClickListener { showDatePickerDialog(viewModel.moment.localTime, this) }
        root.editTime.setOnClickListener { showTimePickerDialog(viewModel.moment.localTime, this) }
        root.editCity.setOnClickListener { onPickCity() }

        updateActionBar(getString(R.string.title_choose_time), "")
        return root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()
        //  menu.setGroupVisible(R.id.menu_overflow, false);
        //  menu.getItem(R.id.action_licenses).setVisible(false);
    }

    private fun showDatePickerDialog(calendar: Calendar, listener: DatePickerDialog.OnDateSetListener) {
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
        DatePickerDialog(requireContext(), listener, year, month, dayOfMonth).show()
    }

    private fun showTimePickerDialog(calendar: Calendar, listener: TimePickerDialog.OnTimeSetListener) {
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]
        TimePickerDialog(requireContext(), listener, hour, minute, true).show()
    }

    private fun updateAutomaticMoment(moment: Moment) {
        view?.also {
            it.currentCity.text = moment.city.name
            it.currentGeoLocation.text = moment.city.location.toString()
            it.currentDateTime.text = Formatter.toString(moment.utc, moment.city.timeZone, Formatter.TimeFormat.DATETIME_SEC_TIMEZONE)
        }
    }

    private fun updateMoment(moment: Moment) {
        view?.also {
            it.editCity.text = moment.city.name
            it.editCity.paint.isUnderlineText = true
            it.editGeoLocation.text = moment.city.location.toString()
            it.editDate.text = Formatter.toString(moment.utc, moment.timeZone, Formatter.TimeFormat.DATE)
            it.editDate.paint.isUnderlineText = true
            it.editTime.text = Formatter.toString(moment.utc, moment.timeZone, Formatter.TimeFormat.TIME_SEC)
            it.editTime.paint.isUnderlineText = true
            it.editTimeZone.text = Formatter.toString(moment.utc, moment.timeZone, Formatter.TimeFormat.TIMEZONE)
            updateClock(moment.localTime)
         }
    }

    private fun updateClock(calendar: Calendar) {
        view?.also {
            val hour = calendar[Calendar.HOUR_OF_DAY] % 12
            val minute = calendar[Calendar.MINUTE]
            val seconds = calendar[Calendar.SECOND]
            val minuteHour = minute / 60.0f
            val secondsMinute = seconds / 60.0f
            it.clockMinutes.rotation = 6.0f * (minute + secondsMinute)
            it.clockHours.rotation = 30.0f * (hour + minuteHour)
        }
    }

    private fun updateTimeAutomatically(auto: Boolean) {
        view?.also {
            it.checkBoxAutomatic.isChecked = auto
        }
    }

    private fun onPickInterval() {
        val bundle = bundleOf("INTERVAL" to viewModel.interval.toString())
        findNavController().navigate(R.id.action_global_nav_interval_picker, bundle)
    }

    private fun onPickCity() =
        findNavController().navigate(R.id.action_global_nav_city_picker)

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) =
        viewModel.setDate(year, month, dayOfMonth)

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) =
        viewModel.setTime(hourOfDay, minute)
}