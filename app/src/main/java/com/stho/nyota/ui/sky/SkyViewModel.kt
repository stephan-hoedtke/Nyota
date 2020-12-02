package com.stho.nyota.ui.sky

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stho.nyota.RepositoryViewModelArgs
import com.stho.nyota.RepositoryViewModelNoArgs
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.universe.Universe
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.views.AbstractSkyView

class SkyViewModel(application: Application, repository: Repository, val element: IElement?) : RepositoryViewModelArgs(application, repository)
{
    val universe: Universe
        get() = repository.universe

    val skyViewSettings: AbstractSkyView.ISkyViewSettings
        get() = repository.settings
}

