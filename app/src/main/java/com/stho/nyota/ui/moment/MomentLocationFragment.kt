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
import com.stho.nyota.PermissionManager
import com.stho.nyota.R
import com.stho.nyota.createViewModel
import com.stho.nyota.databinding.FragmentMomentLocationBinding
import com.stho.nyota.sky.utilities.*
import com.stho.nyota.sky.utilities.Formatter
import java.util.*


class MomentLocationFragment : AbstractFragment(),  DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var viewModel: MomentViewModel
    private var bindingReference: FragmentMomentLocationBinding? = null
    private val binding: FragmentMomentLocationBinding get() = bindingReference!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel(MomentViewModel::class.java)
        setHasOptionsMenu(true)
    }

    override val abstractViewModel: IAbstractViewModel
        get() = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        bindingReference = FragmentMomentLocationBinding.inflate(inflater, container, false)

        binding.checkBoxAutomatic.setOnClickListener { viewModel.toggleAutomaticLocation() }
        binding.timeIntervalFooter.buttonNext.setOnClickListener { viewModel.onNext() }
        binding.timeIntervalFooter.buttonPrevious.setOnClickListener { viewModel.onPrevious() }
        binding.timeIntervalFooter.interval.setOnClickListener { onPickInterval() }
        binding.editCity.setOnClickListener { onEditCity() }
        binding.editLatitude.setOnClickListener { onEditLocation() }
        binding.editLongitude.setOnClickListener { onEditLocation() }
        binding.buttonDone.setOnClickListener { onDone() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.repository.updateCityDistances()
        viewModel.momentLD.observe(viewLifecycleOwner, { moment -> updateMoment(moment) })
        viewModel.updateLocationAutomaticallyLD.observe(viewLifecycleOwner, { auto -> updateLocationAutomatically(auto) })
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
        binding.editCity.text = moment.city.name
        binding.editCity.paint.isUnderlineText = true
        binding.editLatitude.text =  Angle.toString(moment.city.location.latitude, Angle.AngleType.LATITUDE)
        binding.editLatitude.paint.isUnderlineText = true
        binding.editLongitude.text = Angle.toString(moment.city.location.longitude, Angle.AngleType.LONGITUDE)
        binding.editLongitude.paint.isUnderlineText = true
        binding.textViewDistance.text = Formatter.toDistanceString(moment.city.distanceInKm)
        binding.image.setImageResource(moment.city.imageId)
        updateActionBar(moment.city.name, Formatter.toString(moment.localTime, Formatter.TimeFormat.DATETIME_TIMEZONE))
    }

    private fun updateLocationAutomatically(auto: Boolean) {
        binding.checkBoxAutomatic.isChecked = auto
        if (auto) {
            checkPermissionToObtainLocation()
        }
    }

    private fun checkPermissionToObtainLocation() {
        val permissionManager = PermissionManager(requireActivity())
        permissionManager.onMessage = { message -> showSnackBar(message) }
        permissionManager.checkPermissionToObtainLocation()
    }

    private fun onPickInterval() {
        val bundle = bundleOf("INTERVAL" to viewModel.interval.toString())
        findNavController().navigate(R.id.action_global_nav_interval_picker, bundle)
    }

    private fun onEditCity() {
        val action = MomentLocationFragmentDirections.actionNavMomentLocationToNavCity(viewModel.moment.city.name)
        findNavController().navigate(action)
    }

    private fun onEditLocation() {
        val action = MomentLocationFragmentDirections.actionNavMomentLocationToNavCity(viewModel.moment.city.name)
        findNavController().navigate(action)
    }

    private fun onDone() =
        findNavController().popBackStack()

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) =
        viewModel.setDate(year, month, dayOfMonth)

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) =
        viewModel.setTime(hourOfDay, minute)
}