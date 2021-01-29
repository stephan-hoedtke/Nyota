package com.stho.nyota.ui.stars

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.Star

class StarViewModelFactory(private val application: Application, private val repository: Repository, private val star: Star) :
    ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = when {
            modelClass.isAssignableFrom(StarViewModel::class.java) -> StarViewModel(
                application,
                repository,
                star
            )
            else -> super.create(modelClass)
        }
        @Suppress("UNCHECKED_CAST")
        return viewModel as T
    }
}

fun StarFragment.createStarViewModel(key: String?): StarViewModel {
    val repository = Repository.requireRepository(this.requireContext())
    val star = repository.getStarByKeyOrDefault(key);
    val activity = this.requireActivity()
    val application = activity.application
    val viewModelFactory = StarViewModelFactory(application, repository, star)
    return ViewModelProvider(this, viewModelFactory).get(StarViewModel::class.java)
}
