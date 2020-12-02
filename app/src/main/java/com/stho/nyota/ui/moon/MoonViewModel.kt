package com.stho.nyota.ui.moon

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.stho.nyota.Interval
import com.stho.nyota.RepositoryViewModelNoArgs
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.Moon
import com.stho.nyota.sky.universe.Universe
import com.stho.nyota.sky.utilities.Moment

class MoonViewModel(application: Application, repository: Repository) : RepositoryViewModelNoArgs(application, repository) {

    val moon: Moon
        get() = repository.solarSystem.moon

    val moonLD: LiveData<Moon>
        get() = repository.moonLD
}

