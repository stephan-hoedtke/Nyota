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
import com.stho.software.nyota.views.SkyView

class SkyViewModel(application: Application, repository: Repository, val element: IElement?) : RepositoryViewModelArgs(application, repository)
{
    private val brightnessLiveData: MutableLiveData<Double> = MutableLiveData()

    val universe: Universe
        get() = repository.universe

    val skyViewSettings: ISkyViewSettings
        get() = repository.settings

    init {
        brightnessLiveData.value = SkyViewOptions.DEFAULT_BRIGHTNESS
    }

    val brightnessLD: LiveData<Double>
        get() = brightnessLiveData

    var brightness: Double
        get() = brightnessLiveData.value ?: SkyViewOptions.DEFAULT_BRIGHTNESS
        set(value) {
            val validValue = value.coerceIn(SkyViewOptions.MIN_BRIGHTNESS, SkyViewOptions.MAX_BRIGHTNESS)
            if (brightnessLiveData.value != validValue) {
                brightnessLiveData.postValue(validValue)
            }
        }

}

