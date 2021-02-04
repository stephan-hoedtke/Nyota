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
        val key = getElementKeyFromArguments()
        viewModel = createSkyViewModel(key)
        setHasOptionsMenu(true)
    }

    private fun onTouchOptions() {
        binding.sky.touch()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentSkyBinding.inflate(inflater, container, false)

        binding.sky.setOptions(viewModel.options)
        binding.sky.setUniverse(viewModel.universe)
        binding.sky.setReferenceElement(viewModel.element)
        binding.buttonZoomIn.setOnClickListener { onZoomIn() }
        binding.buttonZoomOut.setOnClickListener { onZoomOut() }
        binding.sky.registerListener(object: ISkyViewListener {
            override fun onChangeCenter() {
                updateDisplayZoom()
            }
            override fun onChangeZoom() {
                updateDisplayZoom()
            }
            override fun onDoubleTap() {
                binding.sky.setTippedElement(null)
                binding.sky.setTippedConstellation(null)
                binding.sky.resetTransformation()
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
        viewModel.options.touchLD.observe(viewLifecycleOwner, { _ -> binding.sky.touch() })
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
            R.id.action_view_luminosity -> displaySkyFragmentLuminosityDialog()
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
        binding.compass.rotation = orientation.centerAzimuth.toFloat() - 25f
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
        bindTime(binding.timeOverlay, moment)
        binding.sky.notifyDataSetChanged()
        updateActionBar(title, toLocalDateString(moment))
        updateDisplayZoom()
    }

    private val title: String
        get() = viewModel.element?.toString() ?: getString(R.string.label_nyota) // Mind: lateinit viewModel

    private fun onZoomIn() {
        binding.sky.applyScale(1.1)
        updateDisplayZoom()
    }

    private fun onZoomOut() {
        binding.sky.applyScale(1 / 1.1)
        updateDisplayZoom()
    }

    private fun updateDisplayZoom() {
        binding.direction.text = binding.sky.center.toString()
        binding.zoom.text = getString(R.string.label_zoom_angle, binding.sky.zoomAngle)
    }

    private fun getElementKeyFromArguments(): String? =
        arguments?.getString("ELEMENT")

    private fun displaySkyFragmentOptionsDialog() {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val tag = "fragment_sky_options_dialog"
        SkyFragmentOptionsDialog(binding.sky.options).show(fragmentManager, tag)
    }

    private fun displaySkyFragmentProjectionsDialog() {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val tag = "fragment_sky_projections_dialog"
        SkyFragmentProjectionDialog(binding.sky.options).show(fragmentManager, tag)
    }

    private fun displaySkyFragmentLiveModeDialog() {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val tag = "fragment_sky_live_mode_dialog"
        SkyFragmentLiveModeDialog(viewModel.settings).show(fragmentManager, tag)
    }

    private fun displaySkyFragmentLuminosityDialog() {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val tag = "fragment_sky_luminosity_dialog"
        SkyFragmentLuminosityDialog(viewModel.options).show(fragmentManager, tag)
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
     * etc...
     */


    private fun displaySnackbarForPosition(position: Topocentric) {
        viewModel.universe.findNearestElementByPosition(position, binding.sky.options.magnitude, binding.sky.sensitivityAngle)?.let {
            when (it) {
                is Star -> displaySnackbarForStarAtPosition(position, it)
                is AbstractPlanet -> displaySnackbarForPlanetAtPosition(position, it)
                is Satellite -> displaySnackbarForSatelliteAtPosition(position, it)
                is Moon -> displaySnackbarForMoonAtPosition(position, it)
                is Sun -> displaySnackbarForSunAtPosition(position, it)
                is Galaxy -> displaySnackbarForGalaxyAtPosition(position, it)
                else -> displaySnackbar("${it.name} at $position")
            }
        } ?: displaySnackbar("$position")
    }

    private fun displaySnackbarForStarAtPosition(position: Topocentric, star: Star) {
        binding.sky.setTippedElement(star)
        binding.sky.setTippedConstellation(star.referenceConstellation)
        val message: String = star.referenceConstellation?.let { messageTextForStarInConstellation(position, star, it) } ?: messageTextForStar(position, star)
        displaySnackBar(message, star.toString()) { onStar(star) }
    }

    private fun messageTextForStarInConstellation(position: Topocentric, star: Star, constellation: Constellation) =
        if (star.hasSymbol)
            "Star ${star.symbol.greekSymbol} ${star.magnAsString} at $position in ${constellation.name}"
        else
            "Star ${star.magnAsString} at $position in ${constellation.name}"

    private fun messageTextForStar(position: Topocentric, star: Star): String =
        if (star.hasSymbol)
            "Star ${star.symbol.greekSymbol} ${star.magnAsString} at $position"
        else
            "Star ${star.magnAsString} at $position "

    private fun displaySnackbarForPlanetAtPosition(position: Topocentric, planet: AbstractPlanet) {
        binding.sky.setTippedElement(planet)
        val message = "Planet at $position"
        displaySnackBar(message, planet.name) { onPlanet(planet) }
    }

    private fun displaySnackbarForMoonAtPosition(position: Topocentric, moon: Moon) {
        binding.sky.setTippedElement(moon)
        val message = "Moon at $position"
        displaySnackBar(message, moon.name) { onMoon() }
    }

    private fun displaySnackbarForSunAtPosition(position: Topocentric, sun: Sun) {
        binding.sky.setTippedElement(sun)
        val message = "Sun at $position"
        displaySnackBar(message, sun.name) { onSun() }
    }

    private fun displaySnackbarForSatelliteAtPosition(position: Topocentric, satellite: Satellite) {
        binding.sky.setTippedElement(satellite)
        val message = "Satellite at $position"
        displaySnackBar(message, satellite.name) { onSatellite(satellite) }
    }

    private fun displaySnackbarForGalaxyAtPosition(position: Topocentric, galaxy: Galaxy) {
        binding.sky.setTippedElement(galaxy)
        val message = "Galaxy ${galaxy.magnAsString} at $position"
        displaySnackBar(message, galaxy.name) { onGalaxy(galaxy) }
    }

    private fun displaySnackBar(message: String, name: String, action: View.OnClickListener ) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE)
            .setBackgroundTint(getColor(R.color.colorSignalBackground))
            .setTextColor(getColor(R.color.colorSecondaryText))
            .setDuration(3000)
            .setAction(name, action)
            .show()

    }

    private fun displaySnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE)
            .setBackgroundTint(getColor(R.color.colorSignalBackground))
            .setTextColor(getColor(R.color.colorSecondaryText))
            .setDuration(5000)
            .show()
    }

    private fun onMoon() =
        findNavController().navigate(R.id.action_global_nav_moon)

    private fun onSun() =
        findNavController().navigate(R.id.action_global_nav_sun)

    private fun onSatellite(satellite: Satellite) =
        findNavController().navigate(R.id.action_global_nav_satellite, bundleOf("SATELLITE" to satellite.name))

    private fun onPlanet(planet: AbstractPlanet) =
        findNavController().navigate(R.id.action_global_nav_planet, bundleOf("PLANET" to planet.key))

    private fun onStar(star: Star) =
        findNavController().navigate(R.id.action_global_nav_star, bundleOf("STAR" to star.key))

    private fun onGalaxy(galaxy: Galaxy) =
        onFinderView(galaxy.key)
}


