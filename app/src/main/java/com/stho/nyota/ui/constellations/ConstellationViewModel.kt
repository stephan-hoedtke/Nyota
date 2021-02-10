package com.stho.nyota.ui.constellations

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stho.nyota.RepositoryViewModelArgs
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.Constellation
import com.stho.nyota.sky.universe.Universe
import com.stho.nyota.sky.utilities.Topocentric
import com.stho.nyota.ui.sky.SkyViewOptions

class ConstellationViewModel(application: Application, repository: Repository, val constellation: Constellation) : RepositoryViewModelArgs(application, repository) {

    private val zoomAngleLiveData: MutableLiveData<Double> = MutableLiveData<Double>().apply { value = SkyViewOptions.DEFAULT_ZOOM_ANGLE }
    private val centerLiveData: MutableLiveData<Topocentric> = MutableLiveData<Topocentric>().apply { value = constellation.position }

    val zoomAngleLD: LiveData<Double>
        get() = zoomAngleLiveData

    val centerLD: LiveData<Topocentric>
        get() = centerLiveData

    fun setZoomAngle(zoomAngle: Double) =
        zoomAngle.coerceIn(SkyViewOptions.MIN_ZOOM_ANGLE, SkyViewOptions.MAX_ZOOM_ANGLE).also {
            if (zoomAngleLiveData.value != it) {
                zoomAngleLiveData.postValue(it)
            }
        }

    fun setCenter(center: Topocentric) {
        centerLiveData.postValue(center)
    }

    fun applyScale(scaleFactor: Double) {
        val zoomAngle = zoomAngleLiveData.value ?: SkyViewOptions.DEFAULT_ZOOM_ANGLE
        setZoomAngle(zoomAngle / scaleFactor)
    }

}