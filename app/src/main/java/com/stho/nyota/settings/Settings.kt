package com.stho.nyota.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stho.nyota.Interval
import com.stho.nyota.ui.sky.ISkyViewSettings

class Settings: ISkyViewSettings {

    private val zoomLiveData: MutableLiveData<Double> = MutableLiveData()
    private val intervalLiveData: MutableLiveData<Interval> = MutableLiveData()
    private val updateTimeAutomaticallyLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val updateLocationAutomaticallyLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val updateOrientationAutomaticallyLiveData: MutableLiveData<Boolean> = MutableLiveData()

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
    override var displayMagnitude: Boolean = true
    override var displayConstellations: Boolean = true
    override var displayConstellationNames: Boolean = true
    override var displayPlanetNames: Boolean = true
    override var displayStarNames: Boolean = true

    val zoomLD: LiveData<Double>
        get() = zoomLiveData

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

    companion object {
        internal const val DEFAULT_ZOOM: Double = 7.0
        internal val DEFAULT_INTERVAL: Interval = Interval.HOUR
    }
}
