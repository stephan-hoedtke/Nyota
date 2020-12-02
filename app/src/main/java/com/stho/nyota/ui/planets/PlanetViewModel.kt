package com.stho.nyota.ui.planets

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stho.nyota.RepositoryViewModelArgs
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.AbstractPlanet
import com.stho.nyota.sky.universe.Universe
import com.stho.nyota.sky.utilities.Moment

class PlanetViewModel(application: Application, repository: Repository, val planet: AbstractPlanet) : RepositoryViewModelArgs(application, repository) {
}
