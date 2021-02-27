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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentSkyBinding.inflate(inflater, container, false)

        binding.sky.setOptions(viewModel.options)
        binding.sky.setUniverse(viewModel.universe)
        binding.sky.setReferenceElement(viewModel.element)
        binding.buttonZoomIn.setOnClickListener { onZoomIn() }
        binding.buttonZoomOut.setOnClickListener { onZoomOut() }
        binding.buttonToggleStyle.setOnClickListener { onToggleStyle() }
        binding.orientation.setOnClickListener { onToggleShowZoom() }
        binding.orientation.setOnLongClickListener { onToggleShowZoom(); true }
        binding.compass.setOnLongClickListener { onToggleShowZoom(); true }
        binding.direction.setOnLongClickListener { onToggleShowZoom(); true }
        binding.sky.registerListener(object: ISkyViewListener {
            override fun onChangeCenter() {
                viewModel.setCenter(binding.sky.center)
                binding.direction.text = binding.sky.center.toString() // TODO: check if really required
            }
            override fun onChangeZoom() {
                viewModel.setZoomAngle(binding.sky.zoomAngle)
                binding.zoom.text = getString(R.string.label_zoom_angle, binding.sky.zoomAngle) // TODO: check if really required
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
        viewModel.options.versionLD.observe(viewLifecycleOwner, { _ -> binding.sky.touch() })
        viewModel.zoomAngleLD.observe(viewLifecycleOwner, { zoomAngle -> onObserveZoomAngle(zoomAngle) })
        viewModel.centerLD.observe(viewLifecycleOwner, { center -> onObserveCenter(center) })
        viewModel.showZoomLD.observe(viewLifecycleOwner, { showZoom -> onObserveShowZoom(showZoom) })
        viewModel.tipLD.observe(viewLifecycleOwner, { tip -> onObserveTip(tip) })
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

    private fun onObserveZoomAngle(zoomAngle: Double) {
        binding.sky.zoomAngle = zoomAngle
        binding.zoom.text = getString(R.string.label_zoom_angle, zoomAngle)
    }

    private fun onObserveCenter(center: Topocentric) {
        binding.sky.setCenter(center)
        binding.direction.text = center.toString()
    }

    private fun onObserveShowZoom(showZoom: Boolean) {
        binding.zoomOverlay.visibility = if (showZoom) View.VISIBLE else View.GONE
        binding.zoomOverlay.layoutParams?.height = if (showZoom) 0 else android.app.ActionBar.LayoutParams.WRAP_CONTENT
    }

    private fun onObserveTip(tip: SkyViewModel.Tip) {
        binding.sky.setTippedElement(tip.element)
        binding.sky.setTippedConstellation(tip.constellation)
    }

    private fun updateMoment(moment: Moment) {
        bind(moment)
    }

    private fun bind(moment: Moment) {
        bindTime(binding.timeOverlay, moment)
        binding.sky.notifyDataSetChanged()
        updateActionBar(title)
    }

    private val title: String
        get() = viewModel.element?.toString() ?: getString(R.string.label_nyota) // Mind: lateinit viewModel

    private fun onZoomIn() {
        viewModel.applyScale(1.1)
    }

    private fun onZoomOut() {
        viewModel.applyScale(1 / 1.1)
    }

    private fun onToggleShowZoom() =
        viewModel.onToggleShowZoom()

    private fun onToggleStyle() {
        viewModel.options.toggleStyle()
    }

    private fun getElementKeyFromArguments(): String? =
        arguments?.getString("ELEMENT")

    private fun displaySkyFragmentOptionsDialog() {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val tag = "DIALOG"
        SkyFragmentOptionsDialog(viewModel.options).show(fragmentManager, tag)
    }

    private fun displaySkyFragmentProjectionsDialog() {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val tag = "DIALOG"
        SkyFragmentProjectionDialog(viewModel.options).show(fragmentManager, tag)
    }

    private fun displaySkyFragmentLiveModeDialog() {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val tag = "DIALOG"
        SkyFragmentLiveModeDialog(viewModel.settings).show(fragmentManager, tag)
    }

    private fun displaySkyFragmentLuminosityDialog() {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val tag = "DIALOG"
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
                is Star -> displaySnackbarForStar(it)
                is AbstractPlanet -> displaySnackbarForPlanet(it)
                is Satellite -> displaySnackbarForSatellite(it)
                is Moon -> displaySnackbarForMoon(it)
                is Sun -> displaySnackbarForSun(it)
                is Galaxy -> displaySnackbarForGalaxy(it)
                else -> displaySnackbar(it.name)
            }
        } ?: displaySnackbar("$position")
    }

    private fun displaySnackbarForStar(star: Star) {
        viewModel.setTippedElement(star, star.referenceConstellation)
        val message: String = star.referenceConstellation?.let { messageTextForStarInConstellation(star, it) } ?: messageTextForStar(star)
        displaySnackBar(star, message, star.toString()) { onStar(star) }
    }

    private fun messageTextForStarInConstellation(star: Star, constellation: Constellation) =
        if (star.hasSymbol)
            "Star ${star.symbol.greekSymbol} ${star.magnAsString} in ${constellation.name}"
        else
            "Star ${star.magnAsString} in ${constellation.name}"

    private fun messageTextForStar(star: Star): String =
        if (star.hasSymbol)
            "Star ${star.symbol.greekSymbol} ${star.magnAsString}"
        else
            "Star ${star.magnAsString}"

    private fun displaySnackbarForPlanet(planet: AbstractPlanet) {
        viewModel.setTippedElement(planet)
        val message = "Planet"
        displaySnackBar(planet, message, planet.name) { onPlanet(planet) }
    }

    private fun displaySnackbarForMoon(moon: Moon) {
        viewModel.setTippedElement(moon)
        val message = "Moon"
        displaySnackBar(moon, message, moon.name) { onMoon() }
    }

    private fun displaySnackbarForSun(sun: Sun) {
        viewModel.setTippedElement(sun)
        val message = "Sun"
        displaySnackBar(sun, message, sun.name) { onSun() }
    }

    private fun displaySnackbarForSatellite(satellite: Satellite) {
        viewModel.setTippedElement(satellite)
        val message = "Satellite"
        displaySnackBar(satellite, message, satellite.name) { onSatellite(satellite) }
    }

    private fun displaySnackbarForGalaxy(galaxy: Galaxy) {
        viewModel.setTippedElement(galaxy)
        val message = "Galaxy ${galaxy.magnAsString}"
        displaySnackBar(galaxy, message, galaxy.name) { onGalaxy(galaxy) }
    }

    private fun displaySnackBar(element: IElement, message: String, name: String, action: View.OnClickListener ) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE)
            .setBackgroundTint(getColor(R.color.colorSignalBackground))
            .setTextColor(getColor(R.color.colorSecondaryText))
            .setDuration(13000)
            .setAction(name, action)
            .addCallback(object: Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    viewModel.undoTip(element)
                }
            })
            .show()

    }

    private fun displaySnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE)
            .setBackgroundTint(getColor(R.color.colorSignalBackground))
            .setTextColor(getColor(R.color.colorSecondaryText))
            .setDuration(13000)
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


