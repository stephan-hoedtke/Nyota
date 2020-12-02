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
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.sky.universe.IElement
import kotlinx.android.synthetic.main.fragment_satellite.view.*
import kotlinx.android.synthetic.main.fragment_satellite.view.basics
import kotlinx.android.synthetic.main.fragment_satellite.view.buttonSkyView
import kotlinx.android.synthetic.main.fragment_satellite.view.details
import kotlinx.android.synthetic.main.fragment_satellite.view.image
import kotlinx.android.synthetic.main.fragment_satellite.view.title
import kotlinx.android.synthetic.main.time_visibility_overlay.view.*

class SatelliteFragment : AbstractElementFragment() {

    private lateinit var viewModel: SatelliteViewModel

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override val element: IElement
        get() = viewModel.satellite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val satelliteName: String? = getSatelliteNameFromArguments()
        viewModel = createSatelliteViewModel(satelliteName)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_satellite, container, false)

        basicsAdapter = PropertiesRecyclerViewAdapter()
        detailsAdapter = PropertiesRecyclerViewAdapter()

        with (root.basics) {
            layoutManager = LinearLayoutManager(context)
            adapter = basicsAdapter
            addItemDecoration(RecyclerViewItemDivider(context))
        }
        with (root.details) {
            layoutManager = LinearLayoutManager(context)
            adapter = detailsAdapter
            addItemDecoration(RecyclerViewItemDivider(context))
        }

        basicsAdapter.onItemClick = { p -> openProperty(p) }
        detailsAdapter.onItemClick = { p -> openProperty(p) }

        viewModel.universeLD.observe(viewLifecycleOwner, Observer { universe -> updateSatellite(universe.moment) })
        root.buttonSkyView.setOnClickListener { onSkyView() }
        root.buttonEarthView.setOnClickListener { onEarthView() }
        root.buttonDownloadTle.setOnClickListener { onUpdateTle() }
        return root
    }

    private fun updateSatellite(moment: Moment) {
        viewModel.satellite.also {
            basicsAdapter.updateProperties(it.getBasics(moment))
            detailsAdapter.updateProperties(it.getDetails(moment))
            bind(moment, it)
        }
    }

    private fun bind(moment: Moment, satellite: Satellite) {
        view?.also {
            it.currentTime.text = toLocalTimeString(moment)
            it.currentVisibility.setImageResource(satellite.visibility)
            it.image.setImageResource(satellite.largeImageId)
            it.title.text = satellite.displayName
            if (satellite.tle.isOutdated) {
                it.buttonDownloadTle.setImageResource(R.drawable.download_tle_red)
            }
            else {
                it.buttonDownloadTle.setImageResource(R.drawable.download_tle_green)
            }
        }
        updateActionBar(satellite.name, toLocalDateString(moment))
    }

    private fun getSatelliteNameFromArguments(): String? {
       return arguments?.getString("SATELLITE")
    }

    private fun onEarthView() {
        val satelliteName: String = viewModel.satellite.name
        val action =
            SatelliteFragmentDirections.actionNavSatelliteToNavSatelliteEarth(satelliteName)
        findNavController().navigate(action)
    }

    private fun onUpdateTle() {
        val satelliteName: String = viewModel.satellite.name
        val action = SatelliteFragmentDirections.actionNavSatelliteToNavSatelliteDownloadElements(
            satelliteName
        )
        findNavController().navigate(action)
    }
}