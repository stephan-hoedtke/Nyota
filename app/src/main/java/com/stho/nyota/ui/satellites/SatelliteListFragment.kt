package com.stho.nyota.ui.satellites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.stho.nyota.*
import com.stho.nyota.sky.universe.Satellite
import com.stho.nyota.sky.universe.Universe
import com.stho.nyota.sky.utilities.Moment
import kotlinx.android.synthetic.main.fragment_satellite_list.view.*
import kotlinx.android.synthetic.main.time_overlay.view.*


/**
 * A fragment representing a list of Items.
 */
class SatelliteListFragment : AbstractFragment() {

    private lateinit var viewModel: SatelliteListViewModel
    private lateinit var adapter: SatelliteListRecyclerViewAdapter

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel(SatelliteListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_satellite_list, container, false)

        adapter = SatelliteListRecyclerViewAdapter()
        adapter.onItemClick = { satellite -> openSatellite(satellite) }

        root.list.layoutManager = LinearLayoutManager(requireContext())
        root.list.adapter = adapter
        root.list.addItemDecoration(RecyclerViewItemDivider(requireContext()))

        viewModel.universeLD.observe(viewLifecycleOwner, Observer { universe -> updateUniverse(universe) })
        return root
    }

    private fun updateUniverse(universe: Universe) {
        adapter.updateSatellites(universe.satellites)
        bind(universe.moment)
    }

    private fun bind(moment: Moment) {
        view?.also {
            it.currentTime.text = toLocalTimeString(moment)
        }
        updateActionBar(R.string.label_satellites, toLocalDateString(moment))
    }

    private fun openSatellite(satellite: Satellite) {
        val satelliteName: String = satellite.name
        val action = SatelliteListFragmentDirections.actionNavSatellitesToNavSatellite(satelliteName)
        findNavController().navigate(action)
    }
}