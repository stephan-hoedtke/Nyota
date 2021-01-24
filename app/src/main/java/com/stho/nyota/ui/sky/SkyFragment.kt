package com.stho.nyota.ui.sky

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.stho.nyota.AbstractFragment
import com.stho.nyota.AbstractViewModel
import com.stho.nyota.ISkyViewListener
import com.stho.nyota.R
import com.stho.nyota.databinding.FragmentSkyBinding
import com.stho.nyota.sky.universe.*
import com.stho.nyota.sky.utilities.*


// TODO: implement options in Sky view: show names, letters, lines, change colors, ...
// TODO: implement styles
// TODO: change updateMoment(...) into onObserveMoment(...) for all observers




class SkyFragment : AbstractFragment() {

    private lateinit var viewModel: SkyViewModel
    private var bindingReference: FragmentSkyBinding? = null
    private val binding: FragmentSkyBinding get() = bindingReference!!

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val elementName = getElementNameFromArguments()
        viewModel = createSkyViewModel(elementName)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentSkyBinding.inflate(inflater, container, false)

        binding.sky.setOptions(viewModel.repository.settings)
        binding.sky.setUniverse(viewModel.universe)
        binding.sky.setElement(viewModel.element)
        binding.buttonZoomIn.setOnClickListener { onZoomIn() }
        binding.buttonZoomOut.setOnClickListener { onZoomOut() }
        binding.sky.registerListener(object: ISkyViewListener {
            override fun onChangeCenter() {
                updateDisplayZoom()
                viewModel.isLive = false
            }
            override fun onChangeZoom() {
                updateDisplayZoom()
                viewModel.isLive = false
            }
            override fun onSingleTap(position: Topocentric) {
                displaySnackbarForPosition(position)
            }
        })
        binding.buttonLiveMode.setOnClickListener { viewModel.onToggleLiveMode() }
        binding.arrowDown.apply{ isVisible = true; alpha = 0f }
        binding.arrowUp.apply{ isVisible = true; alpha = 0f }
        binding.arrowLeft.apply{ isVisible = true; alpha = 0f }
        binding.arrowRight.apply{ isVisible = true; alpha = 0f }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.universeLD.observe(viewLifecycleOwner, { universe -> updateMoment(universe.moment) }) // TODO: rename live data observer in onObserve...
        viewModel.currentOrientationLD.observe(viewLifecycleOwner, { orientation -> onObserveOrientation(orientation) })
        viewModel.skyOrientationLD.observe(viewLifecycleOwner, { arrow -> onObserveSkyOrientation(arrow) })
        viewModel.isLiveLD.observe(viewLifecycleOwner, { isLive -> onObserveIsLive(isLive) })
        viewModel.liveModeLD.observe(viewLifecycleOwner, { liveMode -> onObserveLiveMode(liveMode) })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sky, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_view_options -> displaySkyFragmentOptionsDialog()
            R.id.action_view_projections -> displaySkyFragmentProjectionsDialog()
            R.id.action_view_live_mode -> displaySkyFragmentLiveModeDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    @Suppress("NON_EXHAUSTIVE_WHEN")
    private fun onObserveOrientation(orientation: Orientation) {
        when (viewModel.liveMode) {
            LiveMode.Hints -> {
                viewModel.onUpdateOrientation(orientation, binding.sky.center)
            }
            LiveMode.MoveCenter -> {
                binding.sky.setCenter(orientation.centerAzimuth, orientation.centerAltitude)
            }
        }
        binding.orientation.text = Angle.toString(orientation.centerAzimuth, orientation.centerAltitude, Angle.AngleType.ORIENTATION)
    }

    private fun onObserveSkyOrientation(skyOrientation: SkyViewModel.SkyOrientation) {
        binding.arrowLeft.alpha = skyOrientation.left
        binding.arrowRight.alpha = skyOrientation.right
        binding.arrowDown.alpha = skyOrientation.down
        binding.arrowUp.alpha = skyOrientation.up
    }

    private fun onObserveIsLive(isLive: Boolean) {
        if (isLive) {
            binding.buttonLiveMode.setImageResource(R.drawable.live_yes)
        } else {
            binding.buttonLiveMode.setImageResource(R.drawable.live_no)
            binding.arrowLeft.alpha = 0f
            binding.arrowRight.alpha = 0f
            binding.arrowDown.alpha = 0f
            binding.arrowUp.alpha = 0f
        }
    }

    private fun onObserveLiveMode(liveMode: LiveMode) {
        when (liveMode) {
            LiveMode.Off -> {
                viewModel.isLive = false
                binding.arrowLeft.alpha = 0f
                binding.arrowRight.alpha = 0f
                binding.arrowDown.alpha = 0f
                binding.arrowUp.alpha = 0f
                binding.buttonLiveMode.isEnabled = false
            }
            LiveMode.Hints -> {
                binding.buttonLiveMode.isEnabled = true
            }
            LiveMode.MoveCenter -> {
                binding.arrowLeft.alpha = 0f
                binding.arrowRight.alpha = 0f
                binding.arrowDown.alpha = 0f
                binding.arrowUp.alpha = 0f
                binding.buttonLiveMode.isEnabled = true
            }
        }
    }

    private fun updateMoment(moment: Moment) {
        bind(moment)
    }

    private fun bind(moment: Moment) {
        binding.timeOverlay.currentTime.text = toLocalTimeString(moment)
        binding.sky.notifyDataSetChanged()
        updateActionBar(R.string.label_nyota, toLocalDateString(moment))
        updateDisplayZoom()
    }

    private fun onZoomIn() {
        binding.sky.options.applyScale(1.1)
        updateDisplayZoom()
    }

    private fun onZoomOut() {
        binding.sky.options.applyScale(1 / 1.1)
        updateDisplayZoom()
    }

    private fun updateDisplayZoom() {
        binding.direction.text = binding.sky.center.toString()
        binding.zoom.text = getString(R.string.label_zoom_angle, binding.sky.options.zoomAngle)
    }

    private fun getElementNameFromArguments(): String? =
        arguments?.getString("ELEMENT")

    private fun displaySkyFragmentOptionsDialog() {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val tag = "fragment_sky_options_dialog"
        SkyFragmentOptionsDialog(binding.sky.options).show(fragmentManager, tag)
    }

    private fun displaySkyFragmentProjectionsDialog() {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val tag = "fragment_sky_projections_dialog"
        SkyFragmentChooseProjectionDialog(binding.sky.options).show(fragmentManager, tag)
    }

    private fun displaySkyFragmentLiveModeDialog() {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val tag = "fragment_sky_live_mode_dialog"
        SkyFragmentLiveModeDialog(viewModel.settings).show(fragmentManager, tag)
    }

    /**
     * Display a snackbar with the position where the user clicked to, and the name of the element
     *
     * case A) star in a constellation
     *          - highlight the constellation as referenced element in the view
     *          - show the name of the constellation in the snackbar
     *          - open command will open the star: "{name of the star}"
     *
     * case B) star without constellation
     *          - highlight the star as referenced element in the view
     *          - show the name of the constellation in the snackbar
     *          - open command will open the star: "-> Star"
     *
     * case C) constellation without a star
     *          - highlight the constellation as referenced element in the view
     *          - show the name of the constellation in the snackbar
     *          - open command will open the constellation: "Open"
     *
     * case D) planet
     *          - highlight the planet as referenced element in the view
     *          - show the name of the planet in the snackbar
     *          - open command will open the planet: "Open"
     *
     *
     */

    /* Many lines, make the textView clickable ?...
    Snackbar.make(button, warning, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok) { button.isEnabled = true }
                    .setActionTextColor(resources.getColor(android.R.color.holo_red_light, null))
                    .apply { view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).maxLines = 5 }
                    .show()
     */


    private fun displaySnackbarForPosition(position: Topocentric) {
        viewModel.universe.findNearestElementByPosition(position)?.let {
            when (it) {
                is Star -> displaySnackbarForStarAtPosition(position, it)
                is AbstractPlanet -> displaySnackbarForPlanetAtPosition(position, it)
                is Constellation -> displaySnackbarForConstellationAtPosition(position, it)
                is Satellite -> displaySnackbarForSatelliteAtPosition(position, it)
                is Moon -> displaySnackbarForMoonAtPosition(position, it)
                is Sun -> displaySnackbarForSunAtPosition(position, it)
                else -> displaySnackbar("$position ${it.name}")
            }
        } ?: displaySnackbar("$position")
    }

    private fun displaySnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE)
            .setBackgroundTint(getColor(R.color.colorSignalBackground))
            .setTextColor(getColor(R.color.colorSecondaryText))
            .setDuration(3000)
            .show()
    }

    private fun displaySnackbarForStarAtPosition(position: Topocentric, star: Star) {
        val message: String = star.referenceConstellation?.let {
            binding.sky.setElement(it, false)
            "$position -> ${it.name}"
        } ?: star.let {
            binding.sky.setElement(it, false)
            "$position"
        }

        Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE)
            .setBackgroundTint(getColor(R.color.colorSignalBackground))
            .setTextColor(getColor(R.color.colorSecondaryText))
            .setDuration(3000)
            .setAction(star.name) { onStar(star) }
            .show()
    }

    private fun displaySnackbarForPlanetAtPosition(position: Topocentric, planet: AbstractPlanet) {
        val message: String = planet.let {
            binding.sky.setElement(it, false)
            "$position -> ${it.name}"
        }

        Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE)
            .setBackgroundTint(getColor(R.color.colorSignalBackground))
            .setTextColor(getColor(R.color.colorSecondaryText))
            .setDuration(3000)
            .setAction(planet.name) { onPlanet(planet) }
            .show()
    }

    private fun displaySnackbarForConstellationAtPosition(position: Topocentric, constellation: Constellation) {
        val message: String = constellation.let {
            binding.sky.setElement(it, false)
            "$position -> ${it.name}"
        }

        Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE)
            .setBackgroundTint(getColor(R.color.colorSignalBackground))
            .setTextColor(getColor(R.color.colorSecondaryText))
            .setDuration(3000)
            .setAction(constellation.name) { onConstellation(constellation) }
            .show()
    }

    private fun displaySnackbarForMoonAtPosition(position: Topocentric, moon: Moon) {
        val message: String = moon.let {
            binding.sky.setElement(it, false)
            "$position -> ${it.name}"
        }

        Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE)
            .setBackgroundTint(getColor(R.color.colorSignalBackground))
            .setTextColor(getColor(R.color.colorSecondaryText))
            .setDuration(3000)
            .setAction(moon.name) { onMoon() }
            .show()
    }

    private fun displaySnackbarForSunAtPosition(position: Topocentric, sun: Sun) {
        val message: String = sun.let {
            binding.sky.setElement(it, false)
            "$position -> ${it.name}"
        }

        Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE)
            .setBackgroundTint(getColor(R.color.colorSignalBackground))
            .setTextColor(getColor(R.color.colorSecondaryText))
            .setDuration(3000)
            .setAction(sun.name) { onSun() }
            .show()
    }

    private fun displaySnackbarForSatelliteAtPosition(position: Topocentric, satellite: Satellite) {
        val message: String = satellite.let {
            binding.sky.setElement(it, false)
            "$position -> ${it.name}"
        }

        Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE)
            .setBackgroundTint(getColor(R.color.colorSignalBackground))
            .setTextColor(getColor(R.color.colorSecondaryText))
            .setDuration(3000)
            .setAction(satellite.name) { onSatellite(satellite) }
            .show()
    }


    private fun onMoon() =
        findNavController().navigate(R.id.action_global_nav_moon)

    private fun onSun() =
        findNavController().navigate(R.id.action_global_nav_sun)

    private fun onSatellite(satellite: Satellite) =
        findNavController().navigate(R.id.action_global_nav_satellite, bundleOf("SATELLITE" to satellite.name))

    private fun onPlanet(planet: AbstractPlanet) =
        findNavController().navigate(R.id.action_global_nav_planet, bundleOf("PLANET" to planet.uniqueName))

    private fun onStar(star: Star) =
        findNavController().navigate(R.id.action_global_nav_star, bundleOf("STAR" to star.uniqueName))

    private fun onConstellation(constellation: Constellation) =
        findNavController().navigate(R.id.action_global_nav_constellation, bundleOf("CONSTELLATION" to constellation.uniqueName))
}


