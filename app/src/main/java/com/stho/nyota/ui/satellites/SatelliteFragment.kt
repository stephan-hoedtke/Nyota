package com.stho.nyota.ui.satellites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.stho.nyota.AbstractElementFragment
import com.stho.nyota.AbstractViewModel
import com.stho.nyota.PropertiesRecyclerViewAdapter
import com.stho.nyota.R
import com.stho.nyota.databinding.FragmentSatelliteBinding
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.universe.Satellite
import com.stho.nyota.sky.utilities.Moment


class SatelliteFragment : AbstractElementFragment() {

    private lateinit var viewModel: SatelliteViewModel
    private var bindingReference: FragmentSatelliteBinding? = null
    private val binding: FragmentSatelliteBinding get() = bindingReference!!

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override val element: IElement
        get() = viewModel.satellite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val satelliteName: String? = getSatelliteNameFromArguments()
        viewModel = createSatelliteViewModel(satelliteName)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentSatelliteBinding.inflate(inflater, container, false)

        basicsAdapter = PropertiesRecyclerViewAdapter()
        detailsAdapter = PropertiesRecyclerViewAdapter()

        super.setupBasics(binding.basics)
        super.setupDetails(binding.details)

        binding.buttonSkyView.setOnClickListener { onSkyView() }
        binding.buttonEarthView.setOnClickListener { onEarthView() }
        binding.buttonDownloadTle.setOnClickListener { onUpdateTle() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.universeLD.observe(viewLifecycleOwner, { universe -> updateSatellite(universe.moment) })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
    }

    private fun updateSatellite(moment: Moment) {
        viewModel.satellite.also {
            basicsAdapter.updateProperties(it.getBasics(moment))
            detailsAdapter.updateProperties(it.getDetails(moment))
            bind(moment, it)
        }
    }

    private fun bind(moment: Moment, satellite: Satellite) {
        binding.timeVisibilityOverlay.currentTime.text = toLocalTimeString(moment)
        binding.timeVisibilityOverlay.currentVisibility.setImageResource(satellite.visibility)
        binding.image.setImageResource(satellite.largeImageId)
        binding.title.text = satellite.displayName
        if (satellite.tle.isOutdated) {
            binding.buttonDownloadTle.setImageResource(R.drawable.download_tle_red)
        } else {
            binding.buttonDownloadTle.setImageResource(R.drawable.download_tle_green)
        }
        updateActionBar(satellite.name, toLocalDateString(moment))
    }

    private fun getSatelliteNameFromArguments(): String? {
       return arguments?.getString("SATELLITE")
    }

    private fun onEarthView() {
        val satelliteName: String = viewModel.satellite.name
        val action = SatelliteFragmentDirections.actionNavSatelliteToNavSatelliteEarth(satelliteName)
        findNavController().navigate(action)
    }

    private fun onUpdateTle() {
        val satelliteName: String = viewModel.satellite.name
        val action = SatelliteFragmentDirections.actionNavSatelliteToNavSatelliteDownloadElements(satelliteName)
        findNavController().navigate(action)
    }
}
