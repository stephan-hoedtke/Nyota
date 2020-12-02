package com.stho.nyota.ui.constellations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.stho.nyota.*
import com.stho.nyota.sky.universe.AbstractElement
import com.stho.nyota.sky.universe.Constellation
import com.stho.nyota.sky.universe.Universe
import com.stho.nyota.sky.utilities.Moment
import kotlinx.android.synthetic.main.fragment_constellation_list.view.*
import kotlinx.android.synthetic.main.time_overlay.view.*

/**
 * A fragment representing a list of Items.
 */
class ConstellationListFragment : AbstractFragment() {

    private lateinit var viewModel: ConstellationListViewModel
    private lateinit var adapter: ConstellationListRecyclerViewAdapter

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel(ConstellationListViewModel::class.java);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_constellation_list, container, false)

        adapter = ConstellationListRecyclerViewAdapter()
        adapter.onItemClick = { element -> openElement(element)}

        root.constellations.layoutManager = LinearLayoutManager(requireContext())
        root.constellations.adapter = adapter
        root.constellations.addItemDecoration(RecyclerViewItemDivider(requireContext()))

        viewModel.universeLD.observe(viewLifecycleOwner, Observer { universe -> updateUniverse(universe) })
        return root
    }

    private fun openElement(element: AbstractElement) {
        when (element) {
            is Constellation -> openConstellation(element)
        }
    }

    private fun openConstellation(constellation: Constellation) {
        val action = ConstellationListFragmentDirections.actionNavConstellationsToNavConstellation(constellation.name)
        findNavController().navigate(action)
    }

    private fun updateUniverse(universe: Universe) {
        adapter.updateConstellations(universe.constellations)
        bind(universe.moment)
    }

    private fun bind(moment: Moment) {
        view?.also {
            it.currentTime.text = toLocalTimeString(moment)
        }
        updateActionBar(R.string.label_constellations, toLocalDateString(moment))
    }
}