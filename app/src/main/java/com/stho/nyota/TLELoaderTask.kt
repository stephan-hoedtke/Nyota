package com.stho.nyota

import android.os.AsyncTask
import com.stho.nyota.sky.universe.TLELoader
import com.stho.nyota.ui.satellites.SatelliteDownloadElementsViewModel


class TLELoaderTask(private val viewModel: SatelliteDownloadElementsViewModel) : AsyncTask<String, Boolean, Boolean>() {

    override fun doInBackground(vararg params: String?): Boolean {
        viewModel.setProcessingStatus(true)
        return try {
            val satelliteNumber: Int = viewModel.satellite.noradSatelliteNumber
            load(satelliteNumber)
            true
        } catch (ex: Exception) {
            viewModel.setErrorMessage("Exception: " + ex.localizedMessage)
            false
        } finally {
            viewModel.setProcessingStatus(false)
        }
    }

    private fun load(satelliteNumber: Int) {
        val loader = TLELoader()
        val elements = loader.download(satelliteNumber)
        if (elements.isNullOrBlank()) {
            throw Exception("TLE not available.")
        }
        viewModel.updateElements(elements)
    }
}