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
import com.stho.nyota.databinding.FragmentMomentTimeBinding
import com.stho.nyota.sky.utilities.Formatter
import com.stho.nyota.sky.utilities.Location
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.sky.utilities.UTC
import java.util.*


class MomentTimeFragment : AbstractFragment(),  DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var viewModel: MomentViewModel
    private var bindingReference: FragmentMomentTimeBinding? = null
    private val binding: FragmentMomentTimeBinding get() = bindingReference!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel(MomentViewModel::class.java)
        setHasOptionsMenu(true)
    }

    override val abstractViewModel: IAbstractViewModel
        get() = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        bindingReference = FragmentMomentTimeBinding.inflate(inflater, container, false)

        binding.checkBoxAutomaticTime.setOnClickListener { viewModel.toggleAutomaticTime() }
        binding.timeIntervalFooter.buttonNext.setOnClickListener { viewModel.onNext() }
        binding.timeIntervalFooter.buttonPrevious.setOnClickListener { viewModel.onPrevious() }
        binding.timeIntervalFooter.interval.setOnClickListener { onPickInterval() }
        binding.editDate.setOnClickListener { showDatePickerDialog(viewModel.moment.localTime, this) }
        binding.editTime.setOnClickListener { showTimePickerDialog(viewModel.moment.localTime, this) }
        binding.buttonDone.setOnClickListener { onDone() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.repository.updateCityDistances()
        viewModel.momentLD.observe(viewLifecycleOwner, { moment -> updateMoment(moment) })
        viewModel.updateTimeAutomaticallyLD.observe(viewLifecycleOwner, { moment -> updateTimeAutomatically(moment) })
        viewModel.intervalLD.observe(viewLifecycleOwner, { interval -> binding.timeIntervalFooter.interval.text = interval.name })
        viewModel.repository.currentAutomaticTimeLD.observe(viewLifecycleOwner, { utc -> updateAutomaticTime(utc) })
        viewModel.repository.currentAutomaticLocationLD.observe(viewLifecycleOwner, { utc -> updateAutomaticLocation(utc) })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
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

    private fun updateAutomaticTime(utc: UTC) {
        binding.currentDateTime.text = Formatter.toString(utc, TimeZone.getDefault(), Formatter.TimeFormat.DATETIME_SEC_TIMEZONE)
    }

    private fun updateAutomaticLocation(location: Location) {
        binding.currentGeoLocation.text = location.toString()
    }

    private fun updateMoment(moment: Moment) {
        binding.editDate.text = Formatter.toString(moment.utc, moment.timeZone, Formatter.TimeFormat.DATE)
        binding.editDate.paint.isUnderlineText = true
        binding.editTime.text = Formatter.toString(moment.utc, moment.timeZone, Formatter.TimeFormat.TIME_SEC)
        binding.editTime.paint.isUnderlineText = true
        binding.editTimeZone.text = Formatter.toString(moment.utc, moment.timeZone, Formatter.TimeFormat.TIMEZONE)
        binding.textViewDistance.text = Formatter.toDistanceKmString(moment.city.distanceInKm)
        updateClock(moment.localTime)
        setActionBarTitle(moment.city.name)
    }

    private fun updateClock(calendar: Calendar) {
        val hour = calendar[Calendar.HOUR_OF_DAY] % 12
        val minute = calendar[Calendar.MINUTE]
        val seconds = calendar[Calendar.SECOND]
        val minuteHour = minute / 60.0f
        val secondsMinute = seconds / 60.0f
        binding.clockMinutes.rotation = 6.0f * (minute + secondsMinute)
        binding.clockHours.rotation = 30.0f * (hour + minuteHour)
    }

    private fun updateTimeAutomatically(auto: Boolean) {
        binding.checkBoxAutomaticTime.isChecked = auto
    }

    private fun onPickInterval() {
        val bundle = bundleOf("INTERVAL" to viewModel.interval.toString())
        findNavController().navigate(R.id.action_global_nav_interval_picker, bundle)
    }

    private fun onDone() =
        findNavController().popBackStack()

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) =
        viewModel.setDate(year, month, dayOfMonth)

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) =
        viewModel.setTime(hourOfDay, minute)
}