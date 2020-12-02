package com.stho.nyota.ui.constellations

import android.app.Application
import androidx.lifecycle.LiveData
import com.stho.nyota.RepositoryViewModelNoArgs
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.Constellations
import com.stho.nyota.sky.universe.Universe

class ConstellationListViewModel(application: Application, repository: Repository) : RepositoryViewModelNoArgs(application, repository) {
}
