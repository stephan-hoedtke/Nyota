package com.stho.nyota.ui.cities

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.Constellation
import com.stho.nyota.sky.utilities.City

class CityViewModelFactory(private val application: Application, private val repository: Repository, private val city: City) :
    ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = when {
            modelClass.isAssignableFrom(CityViewModel::class.java) -> CityViewModel(
                application,
                repository,
                city
            )
            else -> super.create(modelClass)
        }
        @Suppress("UNCHECKED_CAST")
        return viewModel as T
    }
}

fun CityFragment.createCityViewModel(cityName: String?): CityViewModel {
    val repository = Repository.requireRepository(this.requireContext())
    val city: City = repository.getCityOrNewCity(cityName)
    val activity = this.requireActivity()
    val application = activity.application
    val viewModelFactory = CityViewModelFactory(application, repository, city)
    return ViewModelProvider(this, viewModelFactory).get(CityViewModel::class.java)
}
