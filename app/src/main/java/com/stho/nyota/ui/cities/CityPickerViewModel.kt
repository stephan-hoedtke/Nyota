package com.stho.nyota.ui.cities

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.stho.nyota.Interval
import com.stho.nyota.RepositoryViewModelArgs
import com.stho.nyota.RepositoryViewModelNoArgs
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.AbstractPlanet
import com.stho.nyota.sky.utilities.Cities
import com.stho.nyota.sky.utilities.City
import com.stho.nyota.sky.utilities.createCities

class CityPickerViewModel(application: Application, repository: Repository) : RepositoryViewModelNoArgs(application, repository) {

    val citiesLD: LiveData<Cities>
        get() = repository.citiesLD

    val selectedCityLC: LiveData<City>
        get() = Transformations.map(repository.momentLD) { moment -> moment.city }

    fun createDefaultCities() =
        repository.createDefaultCities(getApplication())

    fun deleteCity(city: City) =
        repository.deleteCity(getApplication(), city)

    fun undoDeleteCity(position: Int, city: City) =
        repository.undoDeleteCity(getApplication(), position, city)
}