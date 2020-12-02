package com.stho.nyota.ui.planets

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.stho.nyota.*
import com.stho.nyota.sky.universe.*
import com.stho.nyota.sky.utilities.Moment
import kotlinx.android.synthetic.main.fragment_planet_list.view.*
import kotlinx.android.synthetic.main.time_overlay.view.*


/**
 * A fragment representing a list of Items.
 */
class PlanetListFragment : AbstractFragment() {

    private lateinit var viewModel: PlanetListViewModel
    private lateinit var adapter: ElementsRecyclerViewAdapter

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel(PlanetListViewModel::class.java);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_planet_list, container, false)

        adapter = ElementsRecyclerViewAdapter()
        adapter.onItemClick = { element -> openElement(element)}

        root.planets.layoutManager = LinearLayoutManager(requireContext())
        root.planets.adapter = adapter
        root.planets.addItemDecoration(RecyclerViewItemDivider(requireContext()))

        viewModel.universeLD.observe(viewLifecycleOwner, Observer { universe -> updateUniverse(universe) })
        return root
    }

    private fun updateUniverse(universe: Universe) {
        adapter.updateSolarSystem(universe.solarSystem) // include sun and moon ...
        bind(universe.moment)
    }

    private fun bind(moment: Moment) {
        view?.also {
            it.currentTime.text = toLocalTimeString(moment)
        }
        updateActionBar(R.string.label_planets, toLocalDateString(moment))
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