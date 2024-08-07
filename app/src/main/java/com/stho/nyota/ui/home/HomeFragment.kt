package com.stho.nyota.ui.home

import android.content.Context
import android.os.Bundle
import android.view.*
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
        setHasOptionsMenu(true)
        loadOptions()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentHomeBinding.inflate(inflater, container, false)

        adapter = ElementsRecyclerViewAdapter()
        adapter.onItemClick = { element -> onElement(element) }
        adapter.onItemLongClick = { element -> showNextStepDialogForElement(element) }

        binding.targets.layoutManager = LinearLayoutManager(requireContext())
        binding.targets.adapter = adapter
        binding.targets.addItemDecoration(RecyclerViewItemDivider(requireContext()))
        binding.buttonSkyView.setOnClickListener { onSkyView() }
        binding.buttonShowOptions.setOnClickListener { displayHomeFragmentOptionsDialog() }
        binding.imageSun.setOnClickListener { viewModel.unselect(); onSun() }
        binding.imageSun.setOnLongClickListener { viewModel.unselect(); onSkyView(viewModel.sun); true }
        binding.imageMoon.setOnClickListener { viewModel.unselect(); onMoon() }
        binding.imageMoon.setOnLongClickListener { viewModel.unselect(); onSkyView(viewModel.moon); true }
        binding.imageIss.setOnClickListener { viewModel.unselect(); onIss() }
        binding.imageIss.setOnLongClickListener { viewModel.unselect(); onSkyView(viewModel.iss); true }
        binding.image.setOnClickListener { viewModel.unselect(); openConstellations() }
        binding.image.setOnLongClickListener { viewModel.unselect(); onSkyView(); true }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.universeLD.observe(viewLifecycleOwner, { universe -> updateUniverse(universe.moment) })
        viewModel.elementsLD.observe(viewLifecycleOwner, { elements -> onObserveElements(elements) })
        viewModel.selectedItemLD.observe(viewLifecycleOwner, { item -> adapter.selectItem(item) })
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

    override fun onPause() {
        super.onPause()
        saveOptions()
    }

    private fun loadOptions() {
        requireContext().getSharedPreferences(KEY, Context.MODE_PRIVATE).apply {
            viewModel.updateOptions(
                getBoolean(STARS, viewModel.options.showStars),
                getBoolean(PLANETS, viewModel.options.showPlanets),
                getBoolean(SATELLITES, viewModel.options.showSatellites),
                getBoolean(TARGETS, viewModel.options.showTargets),
                getBoolean(INVISIBLE_ELEMENTS, viewModel.options.showInvisibleElements)
            )
        }
    }

    private fun saveOptions() {
        requireContext().getSharedPreferences(KEY, Context.MODE_PRIVATE).edit().apply {
            putBoolean(STARS, viewModel.options.showStars)
            putBoolean(PLANETS, viewModel.options.showPlanets)
            putBoolean(SATELLITES, viewModel.options.showSatellites)
            putBoolean(TARGETS, viewModel.options.showTargets)
            putBoolean(INVISIBLE_ELEMENTS, viewModel.options.showInvisibleElements)
            apply()
        }
    }

    override fun showNextStepDialogForElement(element: IElement) {
        viewModel.unselect()
        super.showNextStepDialogForElement(element)
    }

    private fun displayHomeFragmentOptionsDialog() {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val tag = "DIALOG"
        HomeFragmentOptionsDialog(viewModel).show(fragmentManager, tag)
    }

    private fun updateUniverse(moment: Moment) {
        adapter.notifyDataSetChanged() // Do not change the list. Only attributes of list elements have changed
        bind(moment, viewModel.moon, viewModel.sun, viewModel.iss)
    }

    private fun onObserveElements(elements: List<IElement>) {
        adapter.updateElementsUseNewList(elements)
    }

    private fun bind(moment: Moment, moon: Moon, sun: Sun, iss: Satellite) {
        bindTime(binding.timeOverlay, moment)
        binding.imageMoon.setImageResource(moon.imageId)
        binding.imageMoon.setPhase(moon)
        binding.imageSun.setImageResource(sun.imageId)
        binding.imageIss.setImageResource(iss.imageId)
        setActionBarTitle(R.string.label_nyota)
    }

    private fun onElement(element: IElement) {
        when (element) {
            is Star -> onStar(element)
            is AbstractPlanet -> onPlanet(element)
            is Constellation -> onConstellation(element)
            else -> showSnackbar("See: ${element.name} ...")
        }
    }

    private fun openConstellations() =
        findNavController().navigate(HomeFragmentDirections.actionGlobalNavConstellations())

    override fun onSun() {
        viewModel.select(viewModel.sun)
        super.onSun()
    }

    override fun onMoon() {
        viewModel.select(viewModel.moon)
        super.onMoon()
    }

    override fun onStar(star: Star) {
        viewModel.select(star)
        super.onStar(star)
    }

    override fun onPlanet(planet: AbstractPlanet) {
        viewModel.select(planet)
        super.onPlanet(planet)
    }

    override fun onConstellation(constellation: Constellation) {
        viewModel.select(constellation)
        super.onConstellation(constellation)
    }

    override fun onSatellite(satellite: Satellite) {
        viewModel.select(satellite)
        super.onSatellite(satellite)
    }

    override fun onSkyView(element: IElement) {
        viewModel.select(element)
        super.onSkyView(element)
    }

    override fun onFinderView(element: IElement) {
        viewModel.select(element)
        super.onFinderView(element)
    }

    private fun onIss() =
        onSatellite(viewModel.iss.name)

    private fun onSkyView() =
        findNavController().navigate(HomeFragmentDirections.actionNavHomeToNavSky(null))

    private fun displayInfo() =
        findNavController().navigate(R.id.action_global_nav_info)

    companion object {
        private const val KEY = "HomeFragment"
        private const val STARS = "Stars"
        private const val PLANETS = "Planets"
        private const val SATELLITES = "Satellites"
        private const val TARGETS = "Targets"
        private const val INVISIBLE_ELEMENTS = "Invisible"
    }
}
