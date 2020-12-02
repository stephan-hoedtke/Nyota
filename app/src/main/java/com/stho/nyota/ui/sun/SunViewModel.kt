package com.stho.nyota.ui.sun

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.stho.nyota.RepositoryViewModelNoArgs
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.Sun
import com.stho.nyota.sky.utilities.Moment

class SunViewModel(application: Application, repository: Repository) : RepositoryViewModelNoArgs(application, repository) {

    val sun: Sun
        get() = repository.universe.solarSystem.sun
}
