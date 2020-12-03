package com.stho.nyota.ui.constellations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.stho.nyota.*
import com.stho.nyota.databinding.FragmentConstellationListBinding
import com.stho.nyota.sky.universe.AbstractElement
import com.stho.nyota.sky.universe.Constellation
import com.stho.nyota.sky.universe.Universe
import com.stho.nyota.sky.utilities.Moment


/**
 * A fragment representing a list of Items.
 */
class ConstellationListFragment : AbstractFragment() {

    private lateinit var viewModel: ConstellationListViewModel
    private lateinit var adapter: ConstellationListRecyclerViewAdapter
    private var bindingReference: FragmentConstellationListBinding? = null
    private val binding: FragmentConstellationListBinding get() = bindingReference!!

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel(ConstellationListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentConstellationListBinding.inflate(inflater, container, false)

        adapter = ConstellationListRecyclerViewAdapter()
        adapter.onItemClick = { element -> openElement(element)}

        binding.constellations.layoutManager = LinearLayoutManager(requireContext())
        binding.constellations.adapter = adapter
        binding.constellations.addItemDecoration(RecyclerViewItemDivider(requireContext()))

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
        binding.timeOverlay.currentTime.text = toLocalTimeString(moment)
        updateActionBar(R.string.label_constellations, toLocalDateString(moment))
    }
}

