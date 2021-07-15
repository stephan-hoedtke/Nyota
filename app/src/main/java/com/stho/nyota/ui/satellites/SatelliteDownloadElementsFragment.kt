package com.stho.nyota.ui.satellites

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.stho.nyota.AbstractFragment
import com.stho.nyota.AbstractViewModel
import com.stho.nyota.R
import com.stho.nyota.databinding.FragmentSatelliteDownloadElementsBinding
import com.stho.nyota.sky.universe.Satellite
import com.stho.nyota.sky.universe.TLELoader
import com.stho.nyota.sky.utilities.Formatter
import com.stho.nyota.sky.utilities.Moment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SatelliteDownloadElementsFragment : AbstractFragment() {

    private lateinit var viewModel: SatelliteDownloadElementsViewModel
    private var bindingReference: FragmentSatelliteDownloadElementsBinding? = null
    private val binding: FragmentSatelliteDownloadElementsBinding get() = bindingReference!!

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val satelliteKey: String? = getSatelliteKeyFromArguments()
        viewModel = createSatelliteViewModel(satelliteKey)
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
        Handler(Looper.getMainLooper()).postDelayed( { onClickDownloadTle() }, 100)
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
        binding.title.text = satellite.friendlyName
        binding.tleDate.text = Formatter.toString(satellite.tle.date, moment.timeZone, Formatter.TimeFormat.DATETIME_TIMEZONE)
        binding.tleSummary.text = satellite.tle.toSummaryString()
        if (satellite.tle.isOutdated) {
            binding.tleStatus.text = getString(R.string.label_outdated)
            binding.buttonDownloadTle.setImageResource(R.drawable.download_tle_red)
        }
        else {
            binding.tleStatus.text = getString(R.string.label_ok)
            binding.buttonDownloadTle.setImageResource(R.drawable.download_tle_green)
        }
        setActionBarTitle(satellite.name)
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
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                viewModel.setProcessingStatus(true)
                val satelliteNumber: Int = viewModel.satellite.noradSatelliteNumber
                val elements = load(satelliteNumber)
                if (elements.isNullOrBlank()) {
                    throw Exception("TLE not available.")
                }
                viewModel.updateElements(elements)
                viewModel.setProcessingStatus(false)

            } catch(ex: Exception) {
                viewModel.setErrorMessage("Exception: " + ex.localizedMessage)
                viewModel.setProcessingStatus(false)
            }
        }
    }

    /**
     * load satellite elements by calling the Web service.
     * The method shall be called by a coroutine in IO thread (not in the main thread).
     */
    private suspend fun load(satelliteNumber: Int): String? {
        return withContext(Dispatchers.IO) {
            TLELoader().download(satelliteNumber)
        }
    }

    private fun onClickEarthView() {
        val action = SatelliteDownloadElementsFragmentDirections.actionNavSatelliteDownloadElementsToNavSatelliteEarth(viewModel.satellite.key)
        findNavController().navigate(action)
    }

    private fun getSatelliteKeyFromArguments(): String? {
        return arguments?.getString("SATELLITE")
    }
}
