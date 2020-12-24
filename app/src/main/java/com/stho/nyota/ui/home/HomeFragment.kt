package com.stho.nyota.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.stho.nyota.*
import com.stho.nyota.databinding.FragmentHomeBinding
import com.stho.nyota.sky.universe.*
import com.stho.nyota.sky.utilities.Moment

// TODO: display list more proper

class HomeFragment : AbstractFragment() {

    // HomeFragment and HomeFragmentOptionsDialog share the view model instance, which is created with the activity as owner.
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: ElementsRecyclerViewAdapter
    private var bindingReference: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding get() = bindingReference!!

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel(HomeViewModel::class.java)
        loadOptionsFromBundle(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentHomeBinding.inflate(inflater, container, false)

        adapter = ElementsRecyclerViewAdapter()
        adapter.onItemClick = { element -> openTarget(element) }

        binding.targets.layoutManager = LinearLayoutManager(requireContext())
        binding.targets.adapter = adapter
        binding.targets.addItemDecoration(RecyclerViewItemDivider(requireContext()))
        binding.buttonSkyView.setOnClickListener { onSkyView() }
        binding.buttonShowOptions.setOnClickListener { onShowOptions() }
        binding.imageSun.setOnClickListener { onSun() }
        binding.imageSun.setOnLongClickListener { onSkyViewForElement(viewModel.sun) }
        binding.imageMoon.setOnClickListener { onMoon() }
        binding.imageMoon.setOnLongClickListener { onSkyViewForElement(viewModel.moon) }
        binding.imageIss.setOnClickListener { onIss() }
        binding.imageIss.setOnLongClickListener { onSkyViewForElement(viewModel.iss) }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.universeLD.observe(viewLifecycleOwner, { universe -> updateUniverse(universe.moment) })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
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
        binding.timeOverlay.currentTime.text = toLocalTimeString(moment)
        binding.imageMoon.setImageResource(moon.imageId)
        binding.imageMoon.setPhase(moon)
        binding.imageSun.setImageResource(sun.imageId)
        binding.imageIss.setImageResource(iss.imageId)
        updateActionBar(R.string.label_nyota, toLocalDateString(moment))
    }

    private fun openTarget(element: IElement) {
        when (element) {
            is AbstractPlanet -> openPlanet(element)
            is Star -> openStar(element)
        }
        findNavController()
        showSnackbar("See: ${element.name}")
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
