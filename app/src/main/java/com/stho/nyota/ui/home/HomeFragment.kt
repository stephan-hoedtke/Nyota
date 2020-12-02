package com.stho.nyota.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.stho.nyota.*
import com.stho.nyota.sky.universe.*
import com.stho.nyota.sky.utilities.Moment
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_home.view.buttonSkyView
import kotlinx.android.synthetic.main.fragment_moon.view.*
import kotlinx.android.synthetic.main.time_overlay.view.*

class HomeFragment : AbstractFragment() {

    // HomeFragment and HomeFragmentOptionsDialog share the view model instance, which is created with the activity as owner.
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: ElementsRecyclerViewAdapter

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel(HomeViewModel::class.java)
        loadOptionsFromBundle(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        adapter = ElementsRecyclerViewAdapter()
        adapter.onItemClick = { element -> openTarget(element) }

        root.targets.layoutManager = LinearLayoutManager(requireContext())
        root.targets.adapter = adapter
        root.targets.addItemDecoration(RecyclerViewItemDivider(requireContext()))

        with(root) {
            buttonSkyView.setOnClickListener { onSkyView() }
            buttonShowOptions.setOnClickListener { onShowOptions() }
            imageSun.setOnClickListener { onSun() }
            imageSun.setOnLongClickListener { onSkyViewForElement(viewModel.sun) }
            imageMoon.setOnClickListener { onMoon() }
            imageMoon.setOnLongClickListener { onSkyViewForElement(viewModel.moon) }
            imageIss.setOnClickListener { onIss() }
            imageIss.setOnLongClickListener { onSkyViewForElement(viewModel.iss) }
        }

        viewModel.universeLD.observe(viewLifecycleOwner, Observer { universe -> updateUniverse(universe.moment) })
        return root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        writeOptionsToBundle(outState)
        super.onSaveInstanceState(outState)
    }

    private fun loadOptionsFromBundle(bundle: Bundle?) {
        bundle?.also {
            viewModel.updateOptions(
                it.getBoolean(STARS, viewModel.options.showStars),
                it.getBoolean(PLANETS, viewModel.options.showPlanets),
                it.getBoolean(SATELLITES, viewModel.options.showSatellites),
                it.getBoolean(TARGETS, viewModel.options.showTargets),
                it.getBoolean(INVISIBLE_ELEMENTS, viewModel.options.showInvisibleElements))
        }
    }

    private fun writeOptionsToBundle(bundle: Bundle) {
        bundle.also {
            it.putBoolean(STARS, viewModel.options.showStars)
            it.putBoolean(PLANETS, viewModel.options.showPlanets)
            it.putBoolean(SATELLITES, viewModel.options.showSatellites)
            it.putBoolean(TARGETS, viewModel.options.showTargets)
            it.putBoolean(INVISIBLE_ELEMENTS, viewModel.options.showInvisibleElements)
        }
    }

    private fun onShowOptions() {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val tag = "fragment_home_options_dialog"
        HomeFragmentOptionsDialog().show(fragmentManager, tag)
    }

    private fun updateUniverse(moment: Moment) {
        adapter.updateElementsUseNewList(viewModel.visibleElements)
        bind(moment, viewModel.moon, viewModel.sun, viewModel.iss)
    }

    private fun bind(moment: Moment, moon: Moon, sun: Sun, iss: Satellite) {
        view?.also {
            it.currentTime.text = toLocalTimeString(moment)
            it.imageMoon.setImageResource(moon.imageId)
            it.imageMoon.setPhase(moon)
            it.imageSun.setImageResource(sun.imageId)
            it.imageIss.setImageResource(iss.imageId)
        }
        updateActionBar(R.string.label_nyota, toLocalDateString(moment))
    }

    private fun openTarget(element: IElement) {
        when (element) {
            is AbstractPlanet -> openPlanet(element)
            is Star -> openStar(element)
        }
        findNavController()
        snack(view, "See: ${element.name}")
    }

    private fun openPlanet(planet: AbstractPlanet) =
        findNavController().navigate(HomeFragmentDirections.actionNavHomeToNavPlanet(planet.name))

    private fun openStar(star: Star) =
        findNavController().navigate(HomeFragmentDirections.actionNavHomeToNavStar(star.name))

    private fun onSun() =
        findNavController().navigate(R.id.action_global_nav_sun)

    private fun onMoon() =
        findNavController().navigate(R.id.action_global_nav_moon)

    private fun onIss() =
        findNavController().navigate(HomeFragmentDirections.actionNavHomeToNavSatellite(viewModel.iss.name))

    private fun onSkyView() =
        findNavController().navigate(HomeFragmentDirections.actionNavHomeToNavSky(null))

    private fun onSkyViewForElement(element: IElement): Boolean {
        findNavController().navigate(HomeFragmentDirections.actionNavHomeToNavSky(element.name))
        return true
    }

    companion object {
        private const val STARS = "STARS"
        private const val PLANETS = "PLANETS"
        private const val SATELLITES = "SATELLITES"
        private const val TARGETS = "TARGETS"
        private const val INVISIBLE_ELEMENTS = "INVISIBLE"
    }
}
