package com.stho.nyota.ui.satellites

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.stho.nyota.AbstractFragment
import com.stho.nyota.AbstractViewModel
import com.stho.nyota.R
import com.stho.nyota.TLELoaderTask
import com.stho.nyota.databinding.FragmentSatelliteDownloadElementsBinding
import com.stho.nyota.sky.universe.Satellite
import com.stho.nyota.sky.utilities.Formatter
import com.stho.nyota.sky.utilities.Moment


class SatelliteDownloadElementsFragment : AbstractFragment() {

    private lateinit var viewModel: SatelliteDownloadElementsViewModel
    private var bindingReference: FragmentSatelliteDownloadElementsBinding? = null
    private val binding: FragmentSatelliteDownloadElementsBinding get() = bindingReference!!

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val satelliteName: String? = getSatelliteNameFromArguments()
        viewModel = createSatelliteViewModel(satelliteName)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentSatelliteDownloadElementsBinding.inflate(inflater, container, false)

        binding.buttonDownloadTle.setOnClickListener { onClickDownloadTle() }
        binding.buttonEarthView.setOnClickListener { onClickEarthView() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.universeLD.observe(viewLifecycleOwner, { universe -> updateSatellite(universe.moment) })
        viewModel.errorMessageLD.observe(viewLifecycleOwner, { errorMessage -> updateErrorMessage(errorMessage) })
        viewModel.processingStatusLD.observe(viewLifecycleOwner) { processing -> updateProcessingStatus(processing) }
        Handler().postDelayed( { onClickDownloadTle() }, 100)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
    }

    private fun updateSatellite(moment: Moment) {
        bind(moment, viewModel.satellite)
    }

    private fun bind(moment: Moment, satellite: Satellite) {
        binding.image.setImageResource(satellite.largeImageId)
        binding.title.text = satellite.displayName
        binding.tleDate.text = Formatter.formatDateTime.format(satellite.tle.date)
        binding.tleSummary.text = satellite.tle.toSummaryString()
        if (satellite.tle.isOutdated) {
            binding.tleStatus.text = getString(R.string.label_outdated)
            binding.buttonDownloadTle.setImageResource(R.drawable.download_tle_red)
        }
        else {
            binding.tleStatus.text = getString(R.string.label_ok)
            binding.buttonDownloadTle.setImageResource(R.drawable.download_tle_green)
        }
        updateActionBar(satellite.name, toLocalDateString(moment))
    }

    private fun updateErrorMessage(errorMessage: String?) {
        if (errorMessage.isNullOrBlank()) {
            binding.errorMessage.text = ""
            binding.errorMessage.visibility = View.INVISIBLE
        }
        else {
            binding.errorMessage.text = errorMessage
            binding.errorMessage.visibility = View.VISIBLE
        }
    }

    private fun updateProcessingStatus(processing: Boolean) {
        binding.buttonDownloadTle.isEnabled = !processing
        binding.processingStatus.isVisible = processing
    }

    private fun onClickDownloadTle() {
        val task = TLELoaderTask(viewModel)
        task.execute()
    }

    private fun onClickEarthView() {
        val satelliteName: String = viewModel.satellite.name
        val action = SatelliteDownloadElementsFragmentDirections.actionNavSatelliteDownloadElementsToNavSatelliteEarth(satelliteName)
        findNavController().navigate(action)
    }

    private fun getSatelliteNameFromArguments(): String? {
        return arguments?.getString("SATELLITE")
    }
}
