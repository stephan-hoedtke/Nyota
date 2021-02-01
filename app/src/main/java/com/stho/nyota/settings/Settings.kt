package com.stho.nyota.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stho.nyota.Interval
import com.stho.nyota.sky.utilities.LiveMode
import com.stho.nyota.sky.utilities.projections.Projection
import com.stho.nyota.ui.sky.ISkyViewSettings

class Settings: ISkyViewSettings {

    private val zoomLiveData: MutableLiveData<Double> = MutableLiveData()
    private val intervalLiveData: MutableLiveData<Interval> = MutableLiveData()
    private val liveModeLiveData: MutableLiveData<LiveMode> = MutableLiveData<LiveMode>().apply { value = LiveMode.Off }
    private val updateTimeAutomaticallyLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val updateLocationAutomaticallyLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val updateOrientationAutomaticallyLiveData: MutableLiveData<Boolean> = MutableLiveData()

    var isDirty: Boolean = false

    val updateLocationAutomaticallyLD: LiveData<Boolean>
        get() = updateLocationAutomaticallyLiveData

    var updateLocationAutomatically: Boolean
        get() = updateLocationAutomaticallyLiveData.value ?: true
        set(value) {
            if (updateLocationAutomaticallyLiveData.value != value) {
                updateLocationAutomaticallyLiveData.postValue(value)
            }
        }

    val updateTimeAutomaticallyLD: LiveData<Boolean>
        get() = updateTimeAutomaticallyLiveData

    var updateTimeAutomatically: Boolean
        get() = updateTimeAutomaticallyLiveData.value ?: true
        set(value) {
            if (updateTimeAutomaticallyLiveData.value != value) {
                updateTimeAutomaticallyLiveData.postValue(value)
            }
        }

    val updateOrientationAutomaticallyLD: LiveData<Boolean>
        get() = updateOrientationAutomaticallyLiveData

    var updateOrientationAutomatically: Boolean
        get() = updateOrientationAutomaticallyLiveData.value ?: true
        set(value) {
            if (updateOrientationAutomaticallyLiveData.value != value) {
                updateLocationAutomaticallyLiveData.postValue(value)
            }
        }

    var currentLocation: String? = null

    override var displaySymbols: Boolean = true
    override var displayConstellations: Boolean = true
    override var displayConstellationNames: Boolean = true
    override var displayPlanetNames: Boolean = true
    override var displayStarNames: Boolean = true
    override var displayTargets: Boolean = false
    override var displaySatellites: Boolean = true
    override var displayGrid: Boolean = false
    override var sphereProjection: Projection = Projection.SPHERE
    override var magnitude: Double = DEFAULT_MAGNITUDE

    val zoomLD: LiveData<Double>
        get() = zoomLiveData

    /**
     * Zoom level of the map view
     */
    var zoom: Double
        get() = zoomLiveData.value ?: DEFAULT_ZOOM
        set(value) {
            if (zoomLiveData.value != value) {
                zoomLiveData.postValue(value)
            }
        }

    val intervalLD: LiveData<Interval>
        get() = intervalLiveData

    var interval: Interval
        get() = intervalLiveData.value ?: DEFAULT_INTERVAL
        set(value) {
            if (intervalLiveData.value != value) {
                intervalLiveData.postValue(value)
            }
        }

    val liveModeLD: LiveData<LiveMode>
        get() = liveModeLiveData

    var liveMode: LiveMode
        get() = liveModeLiveData.value ?: LiveMode.Off
        set(value) {
            if (liveModeLiveData.value != value) {
                liveModeLiveData.postValue(value)
            }
        }

    override fun touch() {
        isDirty = true
    }

    companion object {
        internal const val DEFAULT_ZOOM: Double = 7.0
        internal const val DEFAULT_MAGNITUDE: Double = 10.0
        internal val DEFAULT_INTERVAL: Interval = Interval.HOUR
    }
}
