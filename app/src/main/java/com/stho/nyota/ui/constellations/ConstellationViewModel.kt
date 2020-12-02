package com.stho.nyota.ui.constellations

import android.app.Application
import androidx.lifecycle.LiveData
import com.stho.nyota.RepositoryViewModelArgs
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.Constellation
import com.stho.nyota.sky.universe.Universe

class ConstellationViewModel(application: Application, repository: Repository, val constellation: Constellation) : RepositoryViewModelArgs(application, repository) {
}