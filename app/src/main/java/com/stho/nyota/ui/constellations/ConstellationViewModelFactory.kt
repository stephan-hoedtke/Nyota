package com.stho.nyota.ui.constellations

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.Constellation

class ConstellationViewModelFactory(private val application: Application, private val repository: Repository, private val constellation: Constellation) :
    ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = when {
            modelClass.isAssignableFrom(ConstellationViewModel::class.java) -> ConstellationViewModel(
                application,
                repository,
                constellation
            )
            else -> super.create(modelClass)
        }
        @Suppress("UNCHECKED_CAST")
        return viewModel as T
    }
}

fun ConstellationFragment.createConstellationViewModel(constellationName: String?): ConstellationViewModel {
    val repository = Repository.requireRepository(this.requireContext())
    val constellation: Constellation = repository.getConstellationOrDefault(constellationName)
    val activity = this.requireActivity()
    val application = activity.application
    val viewModelFactory = ConstellationViewModelFactory(application, repository, constellation)
    return ViewModelProvider(this, viewModelFactory).get(ConstellationViewModel::class.java)
}
