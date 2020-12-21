package com.stho.nyota

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.utilities.Location
import com.stho.nyota.sky.utilities.Orientation

class MainViewModel(application: Application, repository: Repository) : RepositoryViewModelNoArgs(application, repository) {

     val updateOrientationAutomatically: Boolean
        get() = repository.updateOrientationAutomatically

    val updateLocationAutomatically: Boolean
        get() = repository.updateLocationAutomatically

    fun updateForNow() =
        repository.updateForNow()

    fun updateForNow(location: Location) =
        repository.updateForNow(location)

    fun updateOrientation(orientation: Orientation) =
        repository.updateOrientation(orientation)

}