package com.stho.nyota.ui.finder

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.IElement

class FinderViewModelFactory(private val application: Application, private val repository: Repository, private val element: IElement) :
    ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = when {
            modelClass.isAssignableFrom(FinderViewModel::class.java) -> FinderViewModel(
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

fun FinderFragment.createFinderViewModel(key: String?): FinderViewModel {
    val repository = Repository.requireRepository(this.requireContext())
    val element: IElement = repository.getElementByKeyOrDefault(key)
    val activity = this.requireActivity()
    val application = activity.application
    val viewModelFactory = FinderViewModelFactory(application, repository, element)
    return ViewModelProvider(this, viewModelFactory).get(FinderViewModel::class.java)
}
