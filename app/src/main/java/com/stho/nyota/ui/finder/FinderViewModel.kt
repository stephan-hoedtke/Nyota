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

    private val ringAngleLiveData = MutableLiveData<Double>().apply { value = 0.0 }
    private val refreshAutomaticallyLiveData = MutableLiveData<Boolean>().apply { value = true }

    val ringAngleLD: LiveData<Double>
        get() = ringAngleLiveData

    val orientationLD: LiveData<Orientation>
        get() = repository.currentOrientationLD

    val orientation: Orientation
        get() = repository.currentOrientation

    val refreshAutomaticallyLD: LiveData<Boolean>
        get() = refreshAutomaticallyLiveData

    fun rotate(delta: Double) {
        val angle: Double = Degree.normalize(ringAngleLiveData.value!! + delta)
        ringAngleLiveData.postValue(angle)
    }

    fun getRotationToTargetManuallyFor(ringAngle: Double): Float =
        getRotationToTargetManuallyFor(ringAngle, element)

    fun getRotationToTargetAutomaticallyFor(orientation: Orientation): Float =
        getRotationToTargetAutomaticallyFor(orientation, element)

    fun getRotationToTarget(): Float =
        if (refreshAutomatically)
            getRotationToTargetAutomaticallyFor(orientation)
        else
            getRotationToTargetManuallyFor(ringAngleLiveData.value ?: 0.0)

    var refreshAutomatically: Boolean
        get() = refreshAutomaticallyLiveData.value ?: true
        set(value) {
            if (refreshAutomaticallyLiveData.value != value)
                refreshAutomaticallyLiveData.postValue(value)
        }

    fun toggleRefreshAutomatically() {
        refreshAutomatically = !refreshAutomatically
    }

    fun reset() =
        ringAngleLiveData.postValue(0.0)

    fun seek() =
        ringAngleLiveData.postValue(-repository.currentOrientation.azimuth)

    companion object {
        private fun getRotationToTargetManuallyFor(ringAngle: Double, element: IElement): Float =
            (ringAngle + targetAzimuth(element)).toFloat()

        private fun getRotationToTargetAutomaticallyFor(orientation: Orientation, element: IElement): Float =
            orientation.getRotationToTargetAt(targetAzimuth(element))

        private fun targetAzimuth(element: IElement): Double =
            element.position?.azimuth ?: 0.0

    }
}

