package com.stho.nyota.ui.planets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        viewModel.selectedItemLD.observe(viewLifecycleOwner, { item -> adapter.selectItem(item) })
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
        setActionBarTitle(R.string.label_planets)
    }

    private fun openElement(element: IElement) {
        when (element) {
            is AbstractPlanet -> onPlanet(element)
            is Moon -> onMoon()
            is Sun -> onSun()
        }
    }

    override fun onPlanet(planet: AbstractPlanet) {
        viewModel.select(planet)
        super.onPlanet(planet)
    }

    override fun onMoon() {
        viewModel.select(viewModel.moon)
        super.onMoon()
    }

    override fun onSun() {
        viewModel.select(viewModel.sun)
        super.onSun()
    }
}