package com.stho.nyota.ui.moment

import android.app.Application
import androidx.lifecycle.LiveData
import com.stho.nyota.RepositoryViewModelNoArgs
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.utilities.Moment

class MomentViewModel(application: Application, repository: Repository) : RepositoryViewModelNoArgs(application, repository) {

    val momentLD: LiveData<Moment>
        get() = repository.momentLD

    val updateTimeAutomaticallyLD: LiveData<Boolean>
        get() = repository.settings.updateTimeAutomaticallyLD

    val updateLocationAutomaticallyLD: LiveData<Boolean>
        get() = repository.settings.updateLocationAutomaticallyLD

    fun toggleAutomaticTime() =
        repository.toggleAutomaticTime()

    fun toggleAutomaticLocation() =
        repository.toggleAutomaticLocation()

    fun setDate(year: Int, month: Int, dayOfMonth: Int) =
        repository.setDate(year, month, dayOfMonth)

    fun setTime(hourOfDay: Int, minute: Int) =
        repository.setTime(hourOfDay, minute)
}
