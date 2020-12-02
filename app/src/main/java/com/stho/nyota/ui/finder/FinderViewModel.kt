package com.stho.nyota.ui.finder

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stho.nyota.RepositoryViewModelArgs
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.utilities.Angle
import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.Vector
import com.stho.nyota.LowPassFilter

class FinderViewModel(application: Application, repository: Repository, val element: IElement) : RepositoryViewModelArgs(application, repository) {

    private val acceleration: Acceleration = Acceleration()
    private val lowPassFilter: LowPassFilter = LowPassFilter()

    private val ringAngleLiveData = MutableLiveData<Float>()
    private val northPointerPositionLiveData = MutableLiveData<Double>()

    init {
        northPointerPositionLiveData.postValue(0.0)
        ringAngleLiveData.postValue(0.0f)
    }

    val northPointerPositionLD: LiveData<Float>
        get() = Transformations.map(northPointerPositionLiveData) { angle -> Angle.toDegree(angle)}

    val ringAngleLD: LiveData<Float>
        get() = ringAngleLiveData


    fun update(orientationAngles: FloatArray) {
        val gravity: Vector = lowPassFilter.setAcceleration(orientationAngles)
        update(gravity.x, Angle.normalizeTo180(gravity.z))
    }

    fun rotate(delta: Double) {
        val angle: Double = Degree.normalize(ringAngleLiveData.value!! + delta)
        ringAngleLiveData.postValue(angle.toFloat())
    }

    fun reset() {
        lowPassFilter.reset()
        ringAngleLiveData.postValue(0f)
    }

    fun seek() {
        ringAngleLiveData.postValue(-Angle.toDegree(acceleration.position))
    }

    fun updateNorthPointer() {
        northPointerPositionLiveData.postValue(acceleration.position)
    }


    private fun update(azimuth: Double, roll: Double) {
        val angle = when {
            roll < -PI_90 || roll > PI_90 -> Angle.normalize(0 - azimuth) // look at screen from below
            else -> azimuth
        }
        updateAcceleration(angle)
    }

    private fun updateAcceleration(newAngle: Double) {
        val angle: Double = acceleration.position
        acceleration.update(Angle.rotateTo(angle, newAngle))
    }

    @Suppress("PrivatePropertyName")
    private val PI_90 = 0.5 * Math.PI

}