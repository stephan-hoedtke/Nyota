package com.stho.nyota.ui.satellites

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stho.nyota.RepositoryViewModelArgs
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.Satellite
import com.stho.nyota.sky.utilities.Moment

class SatelliteViewModel(application: Application, repository: Repository, val satellite: Satellite) : RepositoryViewModelArgs(application, repository) {
}

