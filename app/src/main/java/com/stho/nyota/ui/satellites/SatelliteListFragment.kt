package com.stho.nyota.ui.satellites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.stho.nyota.*
import com.stho.nyota.databinding.FragmentSatelliteListBinding
import com.stho.nyota.sky.universe.Satellite
import com.stho.nyota.sky.universe.Universe
import com.stho.nyota.sky.utilities.Moment


/**
 * A fragment representing a list of Items.
 */
class SatelliteListFragment : AbstractFragment() {

    private lateinit var viewModel: SatelliteListViewModel
    private lateinit var adapter: SatelliteListRecyclerViewAdapter
    private var bindingReference: FragmentSatelliteListBinding? =  null
    private val binding: FragmentSatelliteListBinding get() = bindingReference!!

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel(SatelliteListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentSatelliteListBinding.inflate(inflater, container,false)

        adapter = SatelliteListRecyclerViewAdapter()
        adapter.onItemClick = { satellite -> openSatellite(satellite) }

        binding.list.layoutManager = LinearLayoutManager(requireContext())
        binding.list.adapter = adapter
        binding.list.addItemDecoration(RecyclerViewItemDivider(requireContext()))

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
        adapter.updateSatellites(universe.satellites)
        bind(universe.moment)
    }

    private fun bind(moment: Moment) {
        bindTime(binding.timeOverlay, moment)
        updateActionBar(R.string.label_satellites, toLocalDateString(moment))
    }

    private fun openSatellite(satellite: Satellite) {
        val action = SatelliteListFragmentDirections.actionNavSatellitesToNavSatellite(satellite.key)
        findNavController().navigate(action)
    }
}