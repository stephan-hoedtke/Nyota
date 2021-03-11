package com.stho.nyota.ui.cities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.navigation.fragment.findNavController
import com.stho.nyota.AbstractFragment
import com.stho.nyota.MainActivity
import com.stho.nyota.R
import com.stho.nyota.databinding.FragmentCityBinding
import com.stho.nyota.sky.utilities.*
import com.stho.nyota.sky.utilities.Formatter
import java.util.*

// TODO: get location on button click
// TODO: choose location using a map
// TODO: helper to select a time zone for the city
// TODO: fading action bar:
//      --> https://material.io/components/app-bars-top/android#regular-top-app-bar
//      --> https://cyrilmottier.com/2013/05/24/pushing-the-actionbar-to-the-next-level/
//      --> https://guides.codepath.com/android/handling-scrolls-with-coordinatorlayout

class CityFragment : AbstractFragment() {

    private lateinit var viewModel: CityViewModel
    private var bindingReference: FragmentCityBinding? = null
    private val binding: FragmentCityBinding get() = bindingReference!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cityName = getCityNameFromArguments()
        viewModel = createCityViewModel(cityName)
        setHasOptionsMenu(true)
    }

    override val abstractViewModel: IAbstractViewModel
        get() = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentCityBinding.inflate(inflater, container, false)

        binding.buttonUseCurrentLocation.setOnClickListener { onUseCurrentLocation() }
        binding.buttonSave.setOnClickListener { onSave() }
        binding.buttonEarthView.setOnClickListener { onEarthView() }
        binding.buttonFinderView.setOnClickListener { onFinderView() }
        binding.buttonReset.setOnClickListener { onReset() }
        binding.buttonChooseTimezone.setOnClickListener { onChooseTimezone() }

        registerForContextMenu(binding.buttonReset)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.repository.updateCityDistances()
        viewModel.repository.currentAutomaticLocationLD.observe(viewLifecycleOwner, { utc -> updateAutomaticLocation(utc) })
        updateCity(viewModel.city)
        setActionBarTitle(viewModel.city.name)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        when (v.id) {
            R.id.buttonReset -> requireActivity().menuInflater.inflate(R.menu.context_menu_city_reset, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_reset -> onReset()
            R.id.action_default -> onDefault()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateCity(city: City) {
        if (city.isAutomatic) {
            binding.radioButtonFixedLocation.isChecked = false
            binding.radioButtonFixedLocation.isEnabled = false
            binding.radioButtonUseDeviceLocation.isChecked = true
            binding.radioButtonUseDeviceLocation.isEnabled = true
        } else {
            binding.radioButtonFixedLocation.isChecked = true
            binding.radioButtonFixedLocation.isEnabled = true
            binding.radioButtonUseDeviceLocation.isChecked = false
            binding.radioButtonUseDeviceLocation.isEnabled = false
        }
        binding.checkBoxAutomaticLocation.isChecked = viewModel.repository.updateLocationAutomatically
        binding.checkBoxAutomaticLocation.isEnabled = false
        binding.editName.setText(city.name)
        binding.editLatitude.setText(Formatter.toString(city.latitude))
        binding.editLongitude.setText(Formatter.toString(city.longitude))
        binding.editAltitude.setText(Formatter.toString(city.altitude))
        binding.textViewDistance.text =  Formatter.toDistanceKmString(city.distanceInKm)
        binding.editTimeZone.setText(city.timeZone.id)
        binding.image.setImageResource(city.imageId)
        binding.copyright.text = city.copyright.text
        binding.copyright.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(city.copyright.link))
            startActivity(browserIntent)
        }
    }

    private fun updateAutomaticLocation(location: Location) {
        if (binding.radioButtonUseDeviceLocation.isChecked) {
            bindToLocation(location)
            bindToTimeZone(TimeZone.getDefault())
        }
    }

    private fun onChooseTimezone() {
        // TODO: implement dialog to select a timezone
        showSnackbar("Choose a timezone: not yet implemented")
    }

    private fun onUseCurrentLocation() {
        enableLocationServiceListener()
        bindToCurrentLocation()
    }

    private fun enableLocationServiceListener() =
        (requireActivity() as MainActivity).enableLocationServiceListener()

    private fun bindToCurrentLocation() =
        bindToLocation(viewModel.repository.currentAutomaticLocation)

    /*
        don't use Angle.toString(..., LATITUDE) below, as we want to be able editing numbers...
     */
    private fun bindToLocation(location: Location) {
        binding.editLatitude.setText(Formatter.toString(location.latitude))
        binding.editLongitude.setText(Formatter.toString(location.longitude))
        binding.editAltitude.setText(Formatter.toString(location.altitude))
        binding.textViewDistance.text = getString(R.string.label_empty)
    }

    private fun bindToTimeZone(timeZone: TimeZone) {
        binding.editTimeZone.setText(timeZone.id)
    }

    /*
        don't change if the city location is updated automatically
     */
    private fun onSave() {
        val city = viewModel.city
        try {
            city.name = binding.editName.text.toString()
            city.location = com.stho.nyota.sky.utilities.Location(
                binding.editLatitude.text.toString().toDouble(),
                binding.editLongitude.text.toString().toDouble(),
                binding.editAltitude.text.toString().toDouble()
            )
            binding.editTimeZone.text.toString().also {
                when {
                    it.isBlank() -> city.timeZone = TimeZone.getDefault()
                    it != city.timeZone.id -> city.timeZone = TimeZone.getTimeZone(it)
                }
            }
            viewModel.updateCity(city)
            findNavController().popBackStack()
        }
        catch (ex: Exception) {
            showSnackbar("Error: ${ex.message}")
        }
    }

    private fun getCityNameFromArguments(): String? =
        arguments?.getString("CITY")

    private fun onReset() {
        updateCity(viewModel.city)
    }

    private fun onDefault() {
        updateCity(viewModel.city.createDefault())
    }

    private fun onFinderView() {
        // TODO
    }

    private fun onEarthView() {
        // TODO
    }
}