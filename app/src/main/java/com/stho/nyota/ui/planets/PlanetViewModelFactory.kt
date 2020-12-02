package com.stho.nyota.ui.planets

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.AbstractPlanet

class PlanetViewModelFactory(private val application: Application, private val repository: Repository, private val planet: AbstractPlanet) :
    ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = when {
            modelClass.isAssignableFrom(PlanetViewModel::class.java) -> PlanetViewModel(
                application,
                repository,
                planet
            )
            else -> super.create(modelClass)
        }
        @Suppress("UNCHECKED_CAST")
        return viewModel as T
    }
}

fun PlanetFragment.createPlanetViewModel(planetName: String?): PlanetViewModel {
    val repository = Repository.requireRepository(this.requireContext())
    val planet: AbstractPlanet = repository.getPlanetOrDefault(planetName)
    val activity = this.requireActivity()
    val application = activity.application
    val viewModelFactory = PlanetViewModelFactory(application, repository, planet)
    return ViewModelProvider(this, viewModelFactory).get(PlanetViewModel::class.java)
}
