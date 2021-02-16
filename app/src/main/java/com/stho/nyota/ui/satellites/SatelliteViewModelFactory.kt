package com.stho.nyota.ui.satellites

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.Satellite

class SatelliteViewModelFactory(private val application: Application, private val repository: Repository, private val satellite: Satellite) :
    ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = when {
            modelClass.isAssignableFrom(SatelliteViewModel::class.java) -> SatelliteViewModel(
                application,
                repository,
                satellite
            )
            modelClass.isAssignableFrom(SatelliteEarthViewModel::class.java) -> SatelliteEarthViewModel(
                application,
                repository,
                satellite
            )
            modelClass.isAssignableFrom(SatelliteDownloadElementsViewModel::class.java) -> SatelliteDownloadElementsViewModel(
                application,
                repository,
                satellite
            )
            else -> super.create(modelClass)
        }
        @Suppress("UNCHECKED_CAST")
        return viewModel as T
    }
}

fun SatelliteFragment.createSatelliteViewModel(satelliteKey: String?): SatelliteViewModel {
    val repository = Repository.requireRepository(this.requireContext())
    val satellite: Satellite = repository.getSatelliteOrDefault(satelliteKey)
    val activity = this.requireActivity()
    val application = activity.application
    val viewModelFactory = SatelliteViewModelFactory(application, repository, satellite)
    return ViewModelProvider(this, viewModelFactory).get(SatelliteViewModel::class.java)
}

fun SatelliteEarthFragment.createSatelliteEarthViewModel(satelliteKey: String?): SatelliteEarthViewModel {
    val repository = Repository.requireRepository(this.requireContext())
    val satellite: Satellite = repository.getSatelliteOrDefault(satelliteKey)
    val activity = this.requireActivity()
    val application = activity.application
    val viewModelFactory = SatelliteViewModelFactory(application, repository, satellite)
    return ViewModelProvider(this, viewModelFactory).get(SatelliteEarthViewModel::class.java)
}

fun SatelliteDownloadElementsFragment.createSatelliteViewModel(satelliteKey: String?): SatelliteDownloadElementsViewModel {
    val repository = Repository.requireRepository(this.requireContext())
    val satellite: Satellite = repository.getSatelliteOrDefault(satelliteKey)
    val activity = this.requireActivity()
    val application = activity.application
    val viewModelFactory = SatelliteViewModelFactory(application, repository, satellite)
    return ViewModelProvider(this, viewModelFactory).get(SatelliteDownloadElementsViewModel::class.java)
}


