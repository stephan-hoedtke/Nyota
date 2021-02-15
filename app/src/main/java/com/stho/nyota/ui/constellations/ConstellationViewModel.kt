package com.stho.nyota.ui.constellations

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stho.nyota.RepositoryViewModelArgs
import com.stho.nyota.repository.Repository
import com.stho.nyota.settings.Settings
import com.stho.nyota.sky.universe.Constellation
import com.stho.nyota.sky.universe.Universe
import com.stho.nyota.sky.utilities.Topocentric
import com.stho.nyota.sky.utilities.projections.Projection
import com.stho.nyota.ui.sky.SkyFragmentViewOptions
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

    val options: SkyViewOptions = object: SkyViewOptions() {
        override var displaySymbols: Boolean = true
            set(value) {
                field = value
                touch()
            }
        override var displayConstellations: Boolean = true
            set(value) {
                field = value
                touch()
            }
        override var displayStarNames: Boolean = false
            set(value) {
                field = value
                touch()
            }
        override var displayConstellationNames: Boolean = false
        override var displayPlanetNames: Boolean = false
        override var displaySatellites: Boolean = false
        override var displayTargets: Boolean = false
        override var displayGrid: Boolean = false
        override var displayEcliptic: Boolean = false
        override var sphereProjection: Projection = Projection.STEREOGRAPHIC
        override var magnitude: Double = Settings.DEFAULT_MAGNITUDE
    }
}



