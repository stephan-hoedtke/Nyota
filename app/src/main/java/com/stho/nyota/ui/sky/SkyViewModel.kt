package com.stho.nyota.ui.sky

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stho.nyota.RepositoryViewModelArgs
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.universe.Universe
import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.LiveMode
import com.stho.nyota.sky.utilities.Orientation
import com.stho.nyota.sky.utilities.Topocentric
import com.stho.nyota.views.AbstractSkyView
import kotlin.math.abs

class SkyViewModel(application: Application, repository: Repository, val element: IElement?) : RepositoryViewModelArgs(application, repository)
{
    /**
     * Calculate the alpha value for the arrows: left, right, down, up:
     *      alpha = 0.6 * min(30, abs(lambda)) / 30 for lambda --> linear from 0 to 30
     */
    data class SkyOrientation(val left: Float, val right: Float, val down: Float, val up: Float) {

        companion object {
            fun getSkyOrientation(orientation: Orientation, center: Topocentric): SkyOrientation {
                val lambda = Degree.difference(center.azimuth, orientation.centerAzimuth)
                val phi = Degree.difference(center.altitude, orientation.centerAltitude)
                return SkyOrientation(
                    left =  if (lambda < 0) alpha(lambda) else 0f,
                    right = if (lambda > 0) alpha(lambda) else 0f,
                    down = if (phi < 0) alpha(phi) else 0f,
                    up = if (phi > 0) alpha(phi) else 0f)
            }

            fun getOK(): SkyOrientation {
                return SkyOrientation(0f, 0f, 0f, 0f)
            }

            private fun alpha(angle: Double): Float =
                (ALPHA * abs(angle).coerceAtMost(ANGLE) / ANGLE).toFloat()

            private const val ALPHA: Double = 0.5
            private const val ANGLE: Double = 20.0
        }
    }

    private val isLiveLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    private val showZoomLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    private val skyOrientationLiveData: MutableLiveData<SkyOrientation> = MutableLiveData<SkyOrientation>().apply { value = SkyOrientation.getOK() }
    private val zoomAngleLiveData: MutableLiveData<Double> = MutableLiveData<Double>().apply { value = SkyViewOptions.DEFAULT_ZOOM_ANGLE }
    private val centerLiveData: MutableLiveData<Topocentric> = MutableLiveData<Topocentric>().apply { value = element?.position ?: Topocentric(0.0, 0.0) }

    val isLiveLD: LiveData<Boolean>
        get() = isLiveLiveData

    val showZoomLD: LiveData<Boolean>
        get() = showZoomLiveData

    val liveModeLD: LiveData<LiveMode>
        get() = repository.settings.liveModeLD

    val skyOrientationLD: LiveData<SkyOrientation>
        get() = skyOrientationLiveData

    val currentOrientationLD: LiveData<Orientation>
        get() = repository.currentOrientationLD

    var isLive: Boolean
        get() = isLiveLiveData.value ?: false
        set(value) {
            if (isLiveLiveData.value != value) {
                isLiveLiveData.postValue(value)
            }
        }

    val liveMode: LiveMode
        get() =
            when (isLive) {
                true -> repository.settings.liveMode
                else -> LiveMode.Off
            }

    var showZoom: Boolean
        get() = showZoomLiveData.value ?: false
        set(value) {
            if (showZoomLiveData.value != value) {
                showZoomLiveData.postValue(value)
            }
        }

    val zoomAngleLD: LiveData<Double>
        get() = zoomAngleLiveData

    val centerLD: LiveData<Topocentric>
        get() = centerLiveData

    fun onUpdateOrientation(orientation: Orientation, center: Topocentric) {
        val value = when (isLive) {
            true -> SkyOrientation.getSkyOrientation(orientation, center)
            else -> SkyOrientation.getOK()
        }
        skyOrientationLiveData.postValue(value)
    }

    fun onToggleLiveMode() {
        val value: Boolean = when (repository.settings.liveMode) {
            LiveMode.Off -> false
            LiveMode.Hints,
            LiveMode.MoveCenter -> !(isLiveLiveData.value ?: false)
        }
        isLiveLiveData.postValue(value)
    }

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

    val universe: Universe = repository.universe

    val options: SkyFragmentViewOptions = SkyFragmentViewOptions(repository.settings)
}


