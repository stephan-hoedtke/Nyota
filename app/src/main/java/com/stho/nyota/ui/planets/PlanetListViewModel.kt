package com.stho.nyota.ui.planets

import android.app.Application
import androidx.lifecycle.LiveData
import com.stho.nyota.RepositoryViewModelNoArgs
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.Universe

class PlanetListViewModel(application: Application, repository: Repository) : RepositoryViewModelNoArgs(application, repository) {
}
