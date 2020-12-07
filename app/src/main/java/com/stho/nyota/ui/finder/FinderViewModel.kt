package com.stho.nyota.ui.finder

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stho.nyota.RepositoryViewModelArgs
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.utilities.Angle
import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.Vector
import com.stho.nyota.LowPassFilter
import com.stho.nyota.sky.utilities.Orientation

class FinderViewModel(application: Application, repository: Repository, val element: IElement) : RepositoryViewModelArgs(application, repository) {

    private val orientationLiveData = MutableLiveData<Orientation>()
    private val ringAngleLiveData = MutableLiveData<Double>()

    init {
        // TODO: orientationLiveData should go to Repository
        orientationLiveData.value = Orientation.defaultOrientation
        ringAngleLiveData.value = 0.0
    }

    val ringAngleLD: LiveData<Double>
        get() = ringAngleLiveData

    val orientationLD: LiveData<Orientation>
        get() = orientationLiveData

    fun updateDeviceOrientation(orientation: Orientation) =
        orientationLiveData.postValue(orientation)

    fun rotate(delta: Double) {
        val angle: Double = Degree.normalize(ringAngleLiveData.value!! + delta)
        ringAngleLiveData.postValue(angle)
    }

    fun reset() {
        ringAngleLiveData.postValue(0.0)
    }

    fun seek() {
        val orientation: Orientation = orientationLiveData.value ?: Orientation.defaultOrientation
        ringAngleLiveData.postValue(-Angle.toDegree(orientation.azimuth))
    }
}