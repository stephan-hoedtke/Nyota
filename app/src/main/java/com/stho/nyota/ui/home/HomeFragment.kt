package com.stho.nyota.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.stho.nyota.*
import com.stho.nyota.databinding.FragmentHomeBinding
import com.stho.nyota.sky.universe.*
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.ui.constellations.ChooseNextStepDialog

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
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentHomeBinding.inflate(inflater, container, false)

        adapter = ElementsRecyclerViewAdapter()
        adapter.onItemClick = { element -> openTarget(element) }
        adapter.onItemLongClick = { element -> showPopupMenuFor(element) }

        binding.targets.layoutManager = LinearLayoutManager(requireContext())
        binding.targets.adapter = adapter
        binding.targets.addItemDecoration(RecyclerViewItemDivider(requireContext()))
        binding.buttonSkyView.setOnClickListener { onSkyView() }
        binding.buttonShowOptions.setOnClickListener { displayHomeFragmentOptionsDialog() }
        binding.imageSun.setOnClickListener { onSun() }
        binding.imageSun.setOnLongClickListener { onSkyViewForElement(viewModel.sun); true }
        binding.imageMoon.setOnClickListener { onMoon() }
        binding.imageMoon.setOnLongClickListener { onSkyViewForElement(viewModel.moon); true }
        binding.imageIss.setOnClickListener { onIss() }
        binding.imageIss.setOnLongClickListener { onSkyViewForElement(viewModel.iss); true }
        binding.image.setOnClickListener { openConstellations() }
        binding.image.setOnLongClickListener { onSkyView(); true }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.universeLD.observe(viewLifecycleOwner, { universe -> updateUniverse(universe.moment) })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_view_options -> displayHomeFragmentOptionsDialog()
            R.id.action_info -> displayInfo()
        }
        return super.onOptionsItemSelected(item)
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

    private fun showPopupMenuFor(element: IElement) {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val tag = "choose_next_step_dialog"
        ChooseNextStepDialog(element).show(fragmentManager, tag)
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

    private fun displayHomeFragmentOptionsDialog() {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val tag = "fragment_home_options_dialog"
        HomeFragmentOptionsDialog().show(fragmentManager, tag)
    }

    private fun updateUniverse(moment: Moment) {
        adapter.updateElementsUseNewList(viewModel.visibleElements)
        bind(moment, viewModel.moon, viewModel.sun, viewModel.iss)
    }

    private fun bind(moment: Moment, moon: Moon, sun: Sun, iss: Satellite) {
        bindTime(binding.timeOverlay, moment)
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
            else -> showSnackbar("See: ${element.name} ...")
        }
    }

    private fun openConstellations() =
        findNavController().navigate(HomeFragmentDirections.actionGlobalNavConstellations())

    private fun openPlanet(planet: AbstractPlanet) =
        findNavController().navigate(HomeFragmentDirections.actionNavHomeToNavPlanet(planet.name))

    private fun openStar(star: Star) =
        findNavController().navigate(HomeFragmentDirections.actionNavHomeToNavStar(star.key))

    private fun onSun() =
        findNavController().navigate(R.id.action_global_nav_sun)

    private fun onMoon() =
        findNavController().navigate(R.id.action_global_nav_moon)

    private fun onIss() =
        findNavController().navigate(HomeFragmentDirections.actionNavHomeToNavSatellite(viewModel.iss.name))

    private fun onSkyView() =
        findNavController().navigate(HomeFragmentDirections.actionNavHomeToNavSky(null))

    private fun onSkyViewForElement(element: IElement) =
        findNavController().navigate(HomeFragmentDirections.actionNavHomeToNavSky(element.name))

    private fun displayInfo() =
        findNavController().navigate(R.id.action_global_nav_info)

    companion object {
        private const val STARS = "STARS"
        private const val PLANETS = "PLANETS"
        private const val SATELLITES = "SATELLITES"
        private const val TARGETS = "TARGETS"
        private const val INVISIBLE_ELEMENTS = "INVISIBLE"
    }
}
