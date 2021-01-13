package com.stho.nyota.ui.sky

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.stho.nyota.AbstractFragment
import com.stho.nyota.AbstractViewModel
import com.stho.nyota.ISkyViewListener
import com.stho.nyota.R
import com.stho.nyota.databinding.FragmentSkyBinding
import com.stho.nyota.sky.universe.*
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.sky.utilities.Topocentric
import java.text.FieldPosition


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
            override fun onChangeSkyCenter() {
                binding.direction.text = binding.sky.center.toString()
            }
            override fun onSingleTap(position: Topocentric) {
                displaySnackbarForPosition(position)
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.universeLD.observe(viewLifecycleOwner, { universe -> updateMoment(universe.moment) })
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
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateMoment(moment: Moment) {
        bind(moment)
    }

    private fun bind(moment: Moment) {
        binding.timeOverlay.currentTime.text = toLocalTimeString(moment)
        binding.direction.text = binding.sky.center.toString()
        binding.sky.notifyDataSetChanged()
        updateActionBar(R.string.label_nyota, toLocalDateString(moment))
    }

    private fun onZoomIn() =
        binding.sky.options.applyScale(1.1)

    private fun onZoomOut() =
        binding.sky.options.applyScale(1 / 1.1)

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
                else -> displaySnackbar("$position")
            }
        }
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

    private fun onPlanet(planet: AbstractPlanet) {
        findNavController().navigate(R.id.action_global_nav_planet, bundleOf("PLANET" to planet.uniqueName))
    }

    private fun onStar(star: Star) =
        findNavController().navigate(R.id.action_global_nav_star, bundleOf("STAR" to star.uniqueName))

    private fun onConstellation(constellation: Constellation) =
        findNavController().navigate(R.id.action_global_nav_constellation, bundleOf("CONSTELLATION" to constellation.uniqueName))
}


