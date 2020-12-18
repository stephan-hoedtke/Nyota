package com.stho.nyota.ui.cities

import android.app.Application
import com.stho.nyota.RepositoryViewModelArgs
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.utilities.City

class CityViewModel(application: Application, repository: Repository, val city: City) : RepositoryViewModelArgs(application, repository) {

    fun updateCity(city: City) =
        repository.updateCity(getApplication(), city)

}
