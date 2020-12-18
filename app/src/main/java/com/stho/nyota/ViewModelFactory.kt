package com.stho.nyota

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.stho.nyota.repository.Repository
import com.stho.nyota.ui.cities.CityPickerViewModel
import com.stho.nyota.ui.constellations.ConstellationListViewModel
import com.stho.nyota.ui.home.HomeViewModel
import com.stho.nyota.ui.interval.IntervalPickerViewModel
import com.stho.nyota.ui.moment.MomentViewModel
import com.stho.nyota.ui.moon.MoonViewModel
import com.stho.nyota.ui.planets.PlanetListViewModel
import com.stho.nyota.ui.satellites.SatelliteListViewModel
import com.stho.nyota.ui.stars.StarListViewModel
import com.stho.nyota.ui.sun.SunViewModel

class ViewModelFactory(private val application: Application, private val repository: Repository) :
    ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = when {
            modelClass.isAssignableFrom(SatelliteListViewModel::class.java) -> SatelliteListViewModel(application, repository)
            modelClass.isAssignableFrom(PlanetListViewModel::class.java) -> PlanetListViewModel(application, repository)
            modelClass.isAssignableFrom(ConstellationListViewModel::class.java) -> ConstellationListViewModel(application, repository)
            modelClass.isAssignableFrom(MoonViewModel::class.java) -> MoonViewModel(application, repository)
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(application, repository)
            modelClass.isAssignableFrom(SunViewModel::class.java) -> SunViewModel(application, repository)
            modelClass.isAssignableFrom(StarListViewModel::class.java) -> StarListViewModel(application, repository)
            modelClass.isAssignableFrom(IntervalPickerViewModel::class.java) -> IntervalPickerViewModel(application, repository)
            modelClass.isAssignableFrom(CityPickerViewModel::class.java) -> CityPickerViewModel(application, repository)
            modelClass.isAssignableFrom(MomentViewModel::class.java) -> MomentViewModel(application, repository)
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(application, repository)
            else -> super.create(modelClass)
        }
        @Suppress("UNCHECKED_CAST")
        return viewModel as T
    }
}

fun <T: RepositoryViewModelNoArgs?> Fragment.createViewModel(modelClass: Class<T>): T {
    val repository = Repository.requireRepository(this.requireContext())
    val activity = this.requireActivity()
    val application = activity.application
    val viewModelFactory = ViewModelFactory(application, repository)
    return ViewModelProvider(activity, viewModelFactory).get(modelClass)
}

fun <T: RepositoryViewModelNoArgs?> FragmentActivity.createViewModel(modelClass: Class<T>): T {
    val repository = Repository.requireRepository(this)
    val viewModelFactory = ViewModelFactory(this.application, repository)
    return ViewModelProvider(this, viewModelFactory).get(modelClass)
}