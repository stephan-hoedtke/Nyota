package com.stho.nyota.ui.planets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.stho.nyota.*
import com.stho.nyota.databinding.FragmentPlanetListBinding
import com.stho.nyota.sky.universe.*
import com.stho.nyota.sky.utilities.Moment


/**
 * A fragment representing a list of Items.
 */
class PlanetListFragment : AbstractFragment() {

    private lateinit var viewModel: PlanetListViewModel
    private lateinit var adapter: ElementsRecyclerViewAdapter
    private var bindingReference: FragmentPlanetListBinding? = null
    private val binding: FragmentPlanetListBinding get() = bindingReference!!

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel(PlanetListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentPlanetListBinding.inflate(inflater, container, false)

        adapter = ElementsRecyclerViewAdapter()
        adapter.onItemClick = { element -> openElement(element)}

        binding.planets.layoutManager = LinearLayoutManager(requireContext())
        binding.planets.adapter = adapter
        binding.planets.addItemDecoration(RecyclerViewItemDivider(requireContext()))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.universeLD.observe(viewLifecycleOwner, { universe -> updateUniverse(universe) })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
    }

    private fun updateUniverse(universe: Universe) {
        adapter.updateSolarSystem(universe.solarSystem) // include sun and moon ...
        bind(universe.moment)
    }

    private fun bind(moment: Moment) {
        bindTime(binding.timeOverlay, moment)
        updateActionBar(R.string.label_planets)
    }

    private fun openElement(element: IElement) {
        when (element) {
            is AbstractPlanet -> openPlanet(element)
            is Moon -> openMoon()
            is Sun -> openSun()
        }
    }

    private fun openPlanet(planet: AbstractPlanet) {
        val action = PlanetListFragmentDirections.actionNavPlanetsToNavPlanet(planet.name)
        findNavController().navigate(action)
    }

    private fun openMoon() {
        findNavController().navigate(R.id.action_global_nav_moon)
    }

    private fun openSun() {
        findNavController().navigate(R.id.action_global_nav_sun)
    }
}