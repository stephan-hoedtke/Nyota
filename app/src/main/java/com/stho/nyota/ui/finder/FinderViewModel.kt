package com.stho.nyota.ui.finder

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stho.nyota.RepositoryViewModelArgs
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.Orientation

class FinderViewModel(application: Application, repository: Repository, val element: IElement) : RepositoryViewModelArgs(application, repository) {

    private val ringAngleLiveData = MutableLiveData<Double>()

    init {
         ringAngleLiveData.value = 0.0
    }

    val ringAngleLD: LiveData<Double>
        get() = ringAngleLiveData

    val orientationLD: LiveData<Orientation>
        get() = repository.currentOrientationLD

    fun rotate(delta: Double) {
        val angle: Double = Degree.normalize(ringAngleLiveData.value!! + delta)
        ringAngleLiveData.postValue(angle)
    }

    fun reset() =
        ringAngleLiveData.postValue(0.0)

    fun seek() =
        ringAngleLiveData.postValue(-repository.currentOrientation.azimuth)
}

