package com.stho.nyota.ui.moon

import android.app.Application
import androidx.lifecycle.LiveData
import com.stho.nyota.RepositoryViewModelNoArgs
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.Moon
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.sky.utilities.UTC

class MoonViewModel(application: Application, repository: Repository) : RepositoryViewModelNoArgs(application, repository) {

    val moon: Moon
        get() = repository.solarSystem.moon

    val moonLD: LiveData<Moon>
        get() = repository.moonLD

    fun addHours(hours: Double) {
        repository.addHours(hours)
    }

    fun previousFullMoon() {
        moon.fullMoon?.addHours(-HOURS_PER_MONTH)?.also {
            repository.setTime(it)
        }
    }

    fun nextFullMoon() {
        moon.fullMoon?.addHours(HOURS_PER_MONTH)?.also {
            repository.setTime(it)
        }
    }

    fun forNow() {
        repository.setTime(UTC.forNow())
    }

    companion object {
        private const val HOURS_PER_MONTH = 730.0
    }
}

