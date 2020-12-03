package com.stho.nyota.ui.cities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.stho.nyota.AbstractFragment
import com.stho.nyota.R
import com.stho.nyota.databinding.FragmentCityBinding
import com.stho.nyota.sky.utilities.City
import com.stho.nyota.sky.utilities.Formatter
import com.stho.nyota.sky.utilities.Moment

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

        binding.buttonSave.setOnClickListener { onSave() }
        binding.buttonEarthView.setOnClickListener { onEarthView() }
        binding.buttonFinderView.setOnClickListener { onFinderView() }
        binding.buttonReset.setOnClickListener { onReset() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateCity(viewModel.city)
        updateActionBar(getString(R.string.title_city), "")
        viewModel.repository.currentAutomaticMomentLD.observe(viewLifecycleOwner, { moment -> updateAutomaticMoment(moment) })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()
    }

    private fun updateCity(city: City) {
        binding.checkBoxAutomatic.isChecked = city.isAutomatic
        binding.editName.setText(city.name)
        binding.editLatitude.setText(Formatter.toString(city.latitude))
        binding.editLongitude.setText(Formatter.toString(city.longitude))
        binding.editAltitude.setText(Formatter.toString(city.altitude))
        binding.image.setImageResource(city.imageId)
        binding.copyright.text = city.copyright.text
        binding.copyright.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(city.copyright.link))
            startActivity(browserIntent)
        }
    }

    private fun updateAutomaticMoment(moment: Moment) {
        if (binding.checkBoxAutomatic.isChecked) {
            binding.editLatitude.setText(Formatter.toString(moment.city.latitude))
            binding.editLongitude.setText(Formatter.toString(moment.city.longitude))
            binding.editAltitude.setText(Formatter.toString(moment.city.altitude))
        }
    }

    private fun onSave() {
        val city = viewModel.city
        try {
            city.name = binding.editName.text.toString()
            city.location = com.stho.nyota.sky.utilities.Location(
                binding.editLatitude.text.toString().toDouble(),
                binding.editLongitude.text.toString().toDouble(),
                binding.editAltitude.text.toString().toDouble()
            )
            viewModel.repository.updateCity(requireContext(), city)
            findNavController().popBackStack()
        }
        catch (ex: Exception) {
            // IGNORE
        }
    }

    private fun getCityNameFromArguments(): String? =
        arguments?.getString("CITY")

    private fun onReset() {
        updateCity(viewModel.city)
    }

    private fun onFinderView() {
        // TODO
    }

    private fun onEarthView() {
        // TODO
    }
}