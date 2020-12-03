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
import com.stho.nyota.databinding.FragmentMomentBinding
import com.stho.nyota.sky.utilities.Formatter
import com.stho.nyota.sky.utilities.Moment
import java.util.*


class MomentFragment : AbstractFragment(),  DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var viewModel: MomentViewModel
    private var bindingReference: FragmentMomentBinding? = null
    private val binding: FragmentMomentBinding get() = bindingReference!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel(MomentViewModel::class.java)
        setHasOptionsMenu(true)
    }

    override val abstractViewModel: IAbstractViewModel
        get() = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        bindingReference = FragmentMomentBinding.inflate(inflater, container, false)

        binding.checkBoxAutomatic.setOnClickListener { viewModel.toggleAutomatic() }
        binding.timeIntervalFooter.buttonNext.setOnClickListener { viewModel.onNext() }
        binding.timeIntervalFooter.buttonPrevious.setOnClickListener { viewModel.onPrevious() }
        binding.timeIntervalFooter.interval.setOnClickListener { onPickInterval() }
        binding.editDate.setOnClickListener { showDatePickerDialog(viewModel.moment.localTime, this) }
        binding.editTime.setOnClickListener { showTimePickerDialog(viewModel.moment.localTime, this) }
        binding.editCity.setOnClickListener { onPickCity() }
        binding.buttonDone.setOnClickListener { onDone() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.currentAutomaticMomentLD.observe(viewLifecycleOwner, { moment -> updateAutomaticMoment(moment) })
        viewModel.momentLD.observe(viewLifecycleOwner, { moment -> updateMoment(moment) })
        viewModel.updateTimeAutomaticallyLD.observe(viewLifecycleOwner, { moment -> updateTimeAutomatically(moment) })
        viewModel.intervalLD.observe(viewLifecycleOwner, { interval -> binding.timeIntervalFooter.interval.text = interval.name })
        updateActionBar(getString(R.string.title_choose_time), "")
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

    private fun updateAutomaticMoment(moment: Moment) {
        binding.currentCity.text = moment.city.name
        binding.currentGeoLocation.text = moment.city.location.toString()
        binding.currentDateTime.text = Formatter.toString(moment.utc, moment.city.timeZone, Formatter.TimeFormat.DATETIME_SEC_TIMEZONE)
    }

    private fun updateMoment(moment: Moment) {
        binding.editCity.text = moment.city.name
        binding.editCity.paint.isUnderlineText = true
        binding.editGeoLocation.text = moment.city.location.toString()
        binding.editDate.text = Formatter.toString(moment.utc, moment.timeZone, Formatter.TimeFormat.DATE)
        binding.editDate.paint.isUnderlineText = true
        binding.editTime.text = Formatter.toString(moment.utc, moment.timeZone, Formatter.TimeFormat.TIME_SEC)
        binding.editTime.paint.isUnderlineText = true
        binding.editTimeZone.text = Formatter.toString(moment.utc, moment.timeZone, Formatter.TimeFormat.TIMEZONE)
        updateClock(moment.localTime)
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
        binding.checkBoxAutomatic.isChecked = auto
    }

    private fun onPickInterval() {
        val bundle = bundleOf("INTERVAL" to viewModel.interval.toString())
        findNavController().navigate(R.id.action_global_nav_interval_picker, bundle)
    }

    private fun onPickCity() =
        findNavController().navigate(R.id.action_global_nav_city_picker)

    private fun onDone() =
        findNavController().popBackStack()

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) =
        viewModel.setDate(year, month, dayOfMonth)

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) =
        viewModel.setTime(hourOfDay, minute)
}