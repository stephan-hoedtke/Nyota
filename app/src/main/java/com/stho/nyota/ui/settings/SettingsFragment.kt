package com.stho.nyota.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.stho.nyota.AbstractFragment
import com.stho.nyota.R
import com.stho.nyota.SeekBarAdapter
import com.stho.nyota.createViewModel
import com.stho.nyota.databinding.FragmentSettingsBinding
import com.stho.nyota.settings.Settings
import com.stho.nyota.sky.utilities.Formatter
import com.stho.nyota.sky.utilities.LiveMode
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.sky.utilities.projections.Projection


class SettingsFragment : AbstractFragment() {

    private lateinit var viewModel: SettingsViewModel
    private var bindingReference: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding get() = bindingReference!!


    private val projections: Array<Projection> = arrayOf(
        Projection.Gnomonic,
        Projection.Sphere,
        Projection.Stereographic,
        Projection.Archimedes,
        Projection.Mercator)

    private val liveModes: Array<LiveMode> = arrayOf(
        LiveMode.Off,
        LiveMode.Hints,
        LiveMode.MoveCenter)

    override val abstractViewModel: IAbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel(SettingsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.switchDisplaySymbols.setOnCheckedChangeListener { _, isChecked -> settings.displaySymbols = isChecked }
        binding.switchDisplayConstellations.setOnCheckedChangeListener { _, isChecked -> settings.displayConstellations = isChecked }
        binding.switchDisplayConstellationNames.setOnCheckedChangeListener { _, isChecked -> settings.displayConstellationNames = isChecked }
        binding.switchDisplayPlanetNames.setOnCheckedChangeListener { _, isChecked -> settings.displayPlanetNames = isChecked }
        binding.switchDisplayStarNames.setOnCheckedChangeListener { _, isChecked -> settings.displayStarNames = isChecked }
        binding.switchDisplayTargets.setOnCheckedChangeListener { _, isChecked -> settings.displayTargets = isChecked }
        binding.switchDisplaySatellites.setOnCheckedChangeListener { _, isChecked -> settings.displaySatellites = isChecked }
        binding.switchDisplayGrid.setOnCheckedChangeListener { _, isChecked -> settings.displayGrid = isChecked }
        binding.switchDisplayEcliptic.setOnCheckedChangeListener { _, isChecked -> settings.displayEcliptic = isChecked }
        binding.switchDisplayHints.setOnCheckedChangeListener { _, isChecked -> settings.displayHints = isChecked }

        // TODO: use a spinnerAdapter (like Seekbar adapter)
        binding.spinnerSphereProjection.adapter = ArrayAdapter<Projection>(requireContext(), android.R.layout.simple_spinner_item, projections).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }
        binding.spinnerSphereProjection.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                settings.sphereProjection = projections[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                settings.sphereProjection = Projection.Stereographic
            }
        }
        binding.spinnerLiveMode.adapter = ArrayAdapter<LiveMode>(requireContext(), android.R.layout.simple_spinner_item, liveModes).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }
        binding.spinnerLiveMode.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                settings.liveMode = liveModes[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                settings.liveMode = LiveMode.Off
            }
        }
        binding.seekBarMagnitudeFilter.setOnSeekBarChangeListener(SeekBarAdapter() { progress ->
            settings.magnitude = Settings.percentToBrightness(progress)
            binding.textViewMagnitudeFilter.text = Formatter.df2.format(settings.magnitude)
        })
        binding.seekBarRadius.setOnSeekBarChangeListener(SeekBarAdapter() { progress ->
            settings.radius = Settings.percentToRadius(progress)
            binding.textViewRadius.text = Formatter.df2.format(settings.radius)
        })
        binding.seekBarGamma.setOnSeekBarChangeListener(SeekBarAdapter() { progress ->
            settings.gamma = Settings.percentToGamma(progress)
            binding.textViewGamma.text = Formatter.df2.format(settings.gamma)
        })
        binding.seekBarLambda.setOnSeekBarChangeListener(SeekBarAdapter() { progress ->
            settings.lambda = Settings.percentToLambda(progress)
            binding.textViewLambda.text = Formatter.df2.format(settings.lambda)
        })
        binding.switchUpdateTimeAutomatically.setOnCheckedChangeListener { _, isChecked -> settings.updateTimeAutomatically = isChecked }
        binding.switchUpdateLocationAutomatically.setOnCheckedChangeListener { _, isChecked -> settings.updateLocationAutomatically = isChecked }
        binding.switchUpdateOrientationAutomatically.setOnCheckedChangeListener { _, isChecked -> settings.updateOrientationAutomatically = isChecked }
        binding.currentDateTime.setOnClickListener { openMomentTime() }
        binding.currentLocation.setOnClickListener { openMomentLocation() }
        binding.currentCity.setOnClickListener { openCities() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.repository.momentLD.observe(viewLifecycleOwner, { moment -> observeMoment(moment) })
        viewModel.repository.settings.versionLD.observe(viewLifecycleOwner, { _ -> bindSettings() })
        bindSettings()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
    }

    private fun bindSettings() {
        binding.switchDisplaySymbols.isChecked = viewModel.settings.displaySymbols
        binding.switchDisplayConstellations.isChecked = viewModel.settings.displayConstellations
        binding.switchDisplayConstellationNames.isChecked = viewModel.settings.displayConstellationNames
        binding.switchDisplayPlanetNames.isChecked = viewModel.settings.displayPlanetNames
        binding.switchDisplayStarNames.isChecked = viewModel.settings.displayStarNames
        binding.switchDisplayTargets.isChecked = viewModel.settings.displayTargets
        binding.switchDisplaySatellites.isChecked = viewModel.settings.displaySatellites
        binding.switchDisplayGrid.isChecked = viewModel.settings.displayGrid
        binding.switchDisplayEcliptic.isChecked = viewModel.settings.displayEcliptic
        binding.switchDisplayHints.isChecked = viewModel.settings.displayHints
        binding.spinnerSphereProjection.setSelection(projections.indexOf(viewModel.settings.sphereProjection))
        binding.spinnerLiveMode.setSelection(liveModes.indexOf(viewModel.settings.liveMode))
        binding.seekBarMagnitudeFilter.progress = Settings.brightnessToPercent(settings.magnitude)
        binding.textViewMagnitudeFilter.text = Formatter.df2.format(settings.magnitude)
        binding.seekBarRadius.progress = Settings.radiusToPercent(settings.radius)
        binding.textViewRadius.text = Formatter.df2.format(settings.radius)
        binding.seekBarGamma.progress = Settings.gammaToPercent(settings.gamma)
        binding.textViewGamma.text = Formatter.df2.format(settings.gamma)
        binding.seekBarLambda.progress = Settings.lambdaToPercent(settings.lambda)
        binding.textViewLambda.text = Formatter.df2.format(settings.lambda)
        binding.switchUpdateTimeAutomatically.isChecked = settings.updateTimeAutomatically
        binding.switchUpdateLocationAutomatically.isChecked = settings.updateLocationAutomatically
        binding.switchUpdateOrientationAutomatically.isChecked = settings.updateOrientationAutomatically
    }

    private fun observeMoment(moment: Moment) {
        binding.currentLocation.text = moment.location.toString()
        binding.currentCity.text = moment.city.toString()
        binding.currentDateTime.text = moment.toString()
    }

    private val settings: Settings
        get() = viewModel.settings

    private fun openMomentTime() =
        findNavController().navigate(R.id.action_global_nav_moment_time)

    private fun openMomentLocation() =
        findNavController().navigate(R.id.action_global_nav_moment_location)

    private fun openCities() =
        findNavController().navigate(R.id.action_global_nav_city_picker)

}

