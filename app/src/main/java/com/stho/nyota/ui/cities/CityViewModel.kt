package com.stho.nyota.ui.cities

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stho.nyota.RepositoryViewModelArgs
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.Constellation
import com.stho.nyota.sky.universe.Universe
import com.stho.nyota.sky.utilities.City
import com.stho.nyota.sky.utilities.Location
import com.stho.nyota.sky.utilities.createDefaultBerlin
import org.osmdroid.api.IGeoPoint

class CityViewModel(application: Application, repository: Repository, val city: City) : RepositoryViewModelArgs(application, repository) {

    fun updateCity(city: City) =
        repository.updateCity(getApplication(), city)
}
