package com.stho.nyota.ui.sky

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.AbstractPlanet
import com.stho.nyota.sky.universe.IElement

class SkyViewModelFactory(private val application: Application, private val repository: Repository, private val element: IElement? = null) :
    ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = when {
            modelClass.isAssignableFrom(SkyViewModel::class.java) -> SkyViewModel(
                application,
                repository,
                element
            )
            else -> super.create(modelClass)
        }
        @Suppress("UNCHECKED_CAST")
        return viewModel as T
    }
}

fun SkyFragment.createSkyViewModel(key: String?): SkyViewModel {
    val repository = Repository.requireRepository(this.requireContext())
    val element: IElement? = repository.getElementByKey(key)
    val activity = this.requireActivity()
    val application = activity.application
    val viewModelFactory = SkyViewModelFactory(application, repository, element)
    return ViewModelProvider(this, viewModelFactory).get(SkyViewModel::class.java)
}
