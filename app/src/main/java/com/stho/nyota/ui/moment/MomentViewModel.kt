package com.stho.nyota.ui.moment

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.stho.nyota.RepositoryViewModelArgs
import com.stho.nyota.RepositoryViewModelNoArgs
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.Satellite
import com.stho.nyota.sky.utilities.City
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.sky.utilities.createDefaultBerlin
import org.osmdroid.api.IGeoPoint
import java.util.*
import kotlin.math.max
import kotlin.math.min

class MomentViewModel(application: Application, repository: Repository) : RepositoryViewModelNoArgs(application, repository) {

    val currentAutomaticMomentLD: LiveData<Moment>
        get() = repository.currentAutomaticMomentLD

    val momentLD: LiveData<Moment>
        get() = repository.momentLD

    val updateTimeAutomaticallyLD: LiveData<Boolean>
        get() = repository.settings.updateTimeAutomaticallyLD

    var updateTimeAutomatically: Boolean
        get() = repository.settings.updateTimeAutomatically
        set(value) {
            repository.settings.updateTimeAutomatically = value
        }

    fun toggleAutomatic() {
        updateTimeAutomatically = !updateTimeAutomatically
    }

    fun setDate(year: Int, month: Int, dayOfMonth: Int) {
        updateTimeAutomatically = false
        repository.setDate(year, month, dayOfMonth)
    }

    fun setTime(hourOfDay: Int, minute: Int) {
        updateTimeAutomatically = false
        repository.setTime(hourOfDay, minute)
    }

}