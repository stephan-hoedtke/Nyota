package com.stho.nyota.ui.stars

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.stho.nyota.RepositoryViewModelArgs
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.AbstractPlanet
import com.stho.nyota.sky.universe.Star
import com.stho.nyota.sky.utilities.Moment

class StarViewModel(application: Application, repository: Repository, val star: Star) : RepositoryViewModelArgs(application, repository) {
}
