package com.stho.nyota.ui.satellites

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.stho.nyota.AbstractFragment
import com.stho.nyota.AbstractViewModel
import com.stho.nyota.R
import com.stho.nyota.TLELoaderTask
import com.stho.nyota.sky.universe.Satellite
import com.stho.nyota.sky.utilities.Formatter
import com.stho.nyota.sky.utilities.Moment
import kotlinx.android.synthetic.main.fragment_satellite_download_elements.view.*


class SatelliteDownloadElementsFragment : AbstractFragment() {

    private lateinit var viewModel: SatelliteDownloadElementsViewModel

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val satelliteName: String? = getSatelliteNameFromArguments()
        viewModel = createSatelliteViewModel(satelliteName)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_satellite_download_elements, container, false)

        viewModel.universeLD.observe(viewLifecycleOwner, Observer { universe -> updateSatellite(universe.moment) })
        viewModel.errorMessageLD.observe(viewLifecycleOwner, { errorMessage -> updateErrorMessage(errorMessage) })
        viewModel.processingStatusLD.observe(viewLifecycleOwner) { processing -> updateProcessingStatus(processing) }

        root.buttonDownloadTle.setOnClickListener { onClickDownloadTle() }
        root.buttonEarthView.setOnClickListener { onClickEarthView() }

        Handler().postDelayed( Runnable { onClickDownloadTle() }, 100)

        return root
    }

    private fun updateSatellite(moment: Moment) {
        bind(moment, viewModel.satellite)
    }

    private fun bind(moment: Moment, satellite: Satellite) {
        view?.also {
            it.image.setImageResource(satellite.largeImageId)
            it.title.text = satellite.displayName
            it.tleDate.text = Formatter.formatDateTime.format(satellite.tle.date)
            it.tleSummary.text = satellite.tle.toSummaryString()
            if (satellite.tle.isOutdated) {
                it.tleStatus.text = getString(R.string.label_outdated)
                it.buttonDownloadTle.setImageResource(R.drawable.download_tle_red)
            }
            else {
                it.tleStatus.text = getString(R.string.label_ok)
                it.buttonDownloadTle.setImageResource(R.drawable.download_tle_green)
            }
        }
        updateActionBar(satellite.name, toLocalDateString(moment))
    }

    private fun updateErrorMessage(errorMessage: String?) {
        view?.also {
            if (errorMessage.isNullOrBlank()) {
                it.errorMessage.text = ""
                it.errorMessage.visibility = View.INVISIBLE
            }
            else {
                it.errorMessage.text = errorMessage
                it.errorMessage.visibility = View.VISIBLE
            }
        }
    }

    private fun updateProcessingStatus(processing: Boolean) {
        view?.also {
            it.buttonDownloadTle.isEnabled = !processing
            it.processingStatus.isVisible = processing
        }
    }

    private fun onClickDownloadTle() {
        val task = TLELoaderTask(viewModel)
        task.execute()
    }

    private fun onClickEarthView() {
        val satelliteName: String = viewModel.satellite.name
        val action =
            SatelliteDownloadElementsFragmentDirections.actionNavSatelliteDownloadElementsToNavSatelliteEarth(
                satelliteName
            )
        findNavController().navigate(action)
    }

    private fun getSatelliteNameFromArguments(): String? {
        return arguments?.getString("SATELLITE")
    }
}
