package com.stho.nyota.ui.constellations

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stho.nyota.RepositoryViewModelArgs
import com.stho.nyota.repository.Repository
import com.stho.nyota.settings.Settings
import com.stho.nyota.settings.Versionable
import com.stho.nyota.sky.universe.Constellation
import com.stho.nyota.sky.universe.Star
import com.stho.nyota.sky.utilities.Topocentric
import com.stho.nyota.sky.utilities.projections.Projection
import com.stho.nyota.ui.sky.IConstellationViewOptions
import com.stho.nyota.ui.sky.ISkyViewOptions

class ConstellationViewModel(application: Application, repository: Repository, val constellation: Constellation) : RepositoryViewModelArgs(application, repository) {

    data class Tip(val star: Star? = null)

    private val zoomAngleLiveData: MutableLiveData<Double> = MutableLiveData<Double>().apply { value = Settings.DEFAULT_ZOOM_ANGLE }
    private val centerLiveData: MutableLiveData<Topocentric> = MutableLiveData<Topocentric>().apply { value = constellation.position }
    private val tipLiveData: MutableLiveData<Tip> = MutableLiveData<Tip>().apply { value = Tip() }

    val zoomAngleLD: LiveData<Double>
        get() = zoomAngleLiveData

    val centerLD: LiveData<Topocentric>
        get() = centerLiveData

    val tipLD: LiveData<Tip>
        get() = tipLiveData

    fun setZoomAngle(zoomAngle: Double) =
        zoomAngle.coerceIn(Settings.MIN_ZOOM_ANGLE, Settings.MAX_ZOOM_ANGLE).also {
            if (zoomAngleLiveData.value != it) {
                zoomAngleLiveData.postValue(it)
            }
        }

    fun setCenter(center: Topocentric) {
        centerLiveData.postValue(center)
    }

    fun setTippedStar(star: Star? = null) {
        tipLiveData.postValue(Tip(star))
    }

    fun undoTip(star: Star) {
        tipLiveData.value?.also {
            if (it.star == star) {
                tipLiveData.postValue(Tip(null))
            }
        }
    }

    fun applyScale(scaleFactor: Double) {
        val zoomAngle = zoomAngleLiveData.value ?: Settings.DEFAULT_ZOOM_ANGLE
        setZoomAngle(zoomAngle / scaleFactor)
    }

    val options: IConstellationViewOptions = object: Versionable(), IConstellationViewOptions {

        override var displaySymbols: Boolean = true
            set(value) {
                if (field != value) {
                    field = value
                    touch()
                }
            }

        override var displayConstellations: Boolean = true
            set(value) {
                if (field != value) {
                    field = value
                    touch()
                }
            }

        override var displayStarNames: Boolean = false
            set(value) {
                if (field != value) {
                    field = value
                    touch()
                }
            }

        override val displayConstellationNames: Boolean = false
        override val displayPlanetNames: Boolean = false
        override val displaySatellites: Boolean = false
        override val displayTargets: Boolean = false
        override val displayGrid: Boolean = false
        override val displayEcliptic: Boolean = false
        override val displayHints: Boolean = false
        override val sphereProjection: Projection = Projection.Stereographic
        override val magnitude: Double = settings.magnitude
        override val radius: Double = settings.radius
        override val gamma: Double = settings.gamma
        override val lambda: Double = settings.lambda
        override var style: Settings.Style = Settings.Style.Normal
            set(value) {
                if (field != value) {
                    field = value
                    touch()
                }
            }

        override fun toggleStyle() {
            style = when (style) {
                Settings.Style.Normal -> Settings.Style.Plain
                Settings.Style.Plain -> Settings.Style.Normal
            }
        }
    }
}



