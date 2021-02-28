package com.stho.nyota.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stho.nyota.Interval
import com.stho.nyota.sky.utilities.LiveMode
import com.stho.nyota.sky.utilities.projections.Projection
import com.stho.nyota.ui.sky.IViewOptions
import com.stho.nyota.ui.sky.ISkyViewOptions


class Settings: Versionable(), IViewOptions, ISkyViewOptions {

    private val zoomLiveData: MutableLiveData<Double> = MutableLiveData()
    private val intervalLiveData: MutableLiveData<Interval> = MutableLiveData()
    private val liveModeLiveData: MutableLiveData<LiveMode> = MutableLiveData<LiveMode>().apply { value = LiveMode.Off }
    private val updateTimeAutomaticallyLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val updateLocationAutomaticallyLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val updateOrientationAutomaticallyLiveData: MutableLiveData<Boolean> = MutableLiveData()

    var isDirty: Boolean = false

    val zoomLD: LiveData<Double>
        get() = zoomLiveData

    val intervalLD: LiveData<Interval>
        get() = intervalLiveData

    val liveModeLD: LiveData<LiveMode>
        get() = liveModeLiveData

    val updateLocationAutomaticallyLD: LiveData<Boolean>
        get() = updateLocationAutomaticallyLiveData

    val updateTimeAutomaticallyLD: LiveData<Boolean>
        get() = updateTimeAutomaticallyLiveData

    val updateOrientationAutomaticallyLD: LiveData<Boolean>
        get() = updateOrientationAutomaticallyLiveData

    var updateLocationAutomatically: Boolean
        get() = updateLocationAutomaticallyLiveData.value ?: true
        set(value) {
            if (updateLocationAutomaticallyLiveData.value != value) {
                updateLocationAutomaticallyLiveData.postValue(value)
                touch()
            }
        }

    var updateTimeAutomatically: Boolean
        get() = updateTimeAutomaticallyLiveData.value ?: true
        set(value) {
            if (updateTimeAutomaticallyLiveData.value != value) {
                updateTimeAutomaticallyLiveData.postValue(value)
                touch()
            }
        }

    var updateOrientationAutomatically: Boolean
        get() = updateOrientationAutomaticallyLiveData.value ?: true
        set(value) {
            if (updateOrientationAutomaticallyLiveData.value != value) {
                updateOrientationAutomaticallyLiveData.postValue(value)
                touch()
            }
        }

    var currentLocation: String = ""
        set(value) {
            if (field != value) {
                field = value
                touch()
            }
        }

    override var displaySymbols: Boolean = false
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

    override var displayConstellationNames: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                touch()
            }
        }

    override var displayPlanetNames: Boolean = true
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

    override var displayTargets: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                touch()
            }
        }

    override var displaySatellites: Boolean = true
        set(value) {
            if (field != value) {
                field = value
                touch()
            }
        }
    override var displayGrid: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                touch()
            }
        }

    override var displayEcliptic: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                touch()
            }
        }

    override var displayHints: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                touch()
            }
        }

    override var sphereProjection: Projection = Projection.Stereographic
        set(value) {
            if (field != value) {
                field = value
                touch()
            }
        }

    override var magnitude: Double = DEFAULT_MAGNITUDE
        set(value) {
            val valueValue = value.coerceIn(MIN_MAGNITUDE, MAX_MAGNITUDE)
            if (field != valueValue) {
                field = valueValue
                touch()
            }
        }

    override var radius: Double = DEFAULT_RADIUS
        set(value) {
            val validValue = value.coerceIn(MIN_RADIUS, MAX_RADIUS)
            if (field != validValue) {
                field = validValue
                touch()
            }
        }

    override var gamma: Double = DEFAULT_GAMMA
        set(value) {
            val validValue = value.coerceIn(MIN_GAMMA, MAX_GAMMA)
            if (field != validValue) {
                field = validValue
                touch()
            }
        }

    override var lambda: Double = DEFAULT_LAMBDA
        set(value) {
            val validValue = value.coerceIn(MIN_LAMBDA, MAX_LAMBDA)
            if (field != validValue) {
                field = validValue
                touch()
            }
        }

    /**
     * Zoom level of the map view
     */
    var zoom: Double
        get() = zoomLiveData.value ?: DEFAULT_ZOOM
        set(value) {
            // valid values are handle by SatelliteEarthViewModel
            if (zoomLiveData.value != value) {
                zoomLiveData.postValue(value)
            }
        }

    var interval: Interval
        get() = intervalLiveData.value ?: DEFAULT_INTERVAL
        set(value) {
            if (intervalLiveData.value != value) {
                intervalLiveData.postValue(value)
            }
        }

    var liveMode: LiveMode
        get() = liveModeLiveData.value ?: LiveMode.Off
        set(value) {
            if (liveModeLiveData.value != value) {
                liveModeLiveData.postValue(value)
            }
        }

    override var style: ViewStyle = ViewStyle.Normal
        set(value) {
            if (field != value) {
                field = value
                touch()
            }
        }

    override fun touch() {
        isDirty = true
        super.touch()
    }

    companion object {
        private const val DEFAULT_ZOOM: Double = 7.0
        private const val DEFAULT_MAGNITUDE: Double = 10.0
        private const val DEFAULT_RADIUS: Double = 6.0
        private const val DEFAULT_LAMBDA: Double = 1.3
        private const val DEFAULT_GAMMA: Double = 0.4
        private val DEFAULT_INTERVAL: Interval = Interval.HOUR

        internal const val MAX_MAGNITUDE: Double = 10.0
        private const val MIN_MAGNITUDE: Double = 0.0
        private const val MIN_RADIUS: Double = 1.0
        private const val MAX_RADIUS: Double = 10.0
        private const val MIN_GAMMA: Double = 0.01
        private const val MAX_GAMMA: Double = 3.00
        private const val MIN_LAMBDA: Double = 0.01
        private const val MAX_LAMBDA: Double = 3.00

        internal const val DEFAULT_ZOOM_ANGLE: Double = 45.0
        internal const val MIN_ZOOM_ANGLE = 0.5
        internal const val MAX_ZOOM_ANGLE = 120.0

        internal fun brightnessToPercent(f: Double): Int =
            valueToPercent(f, MIN_MAGNITUDE, MAX_MAGNITUDE)

        internal fun percentToBrightness(i: Int): Double =
            percentToValue(i, MIN_MAGNITUDE, MAX_MAGNITUDE)

        internal fun radiusToPercent(f: Double): Int =
            valueToPercent(f, MIN_RADIUS, MAX_RADIUS)

        internal fun percentToRadius(i: Int): Double =
            percentToValue(i, MIN_RADIUS, MAX_RADIUS)

        internal fun gammaToPercent(f: Double): Int =
            valueToPercent(f, MIN_GAMMA, MAX_GAMMA)

        internal fun percentToGamma(i: Int): Double =
            percentToValue(i, MIN_GAMMA, MAX_GAMMA)

        internal fun lambdaToPercent(f: Double): Int =
            valueToPercent(f, MIN_LAMBDA, MAX_LAMBDA)

        internal fun percentToLambda(i: Int): Double =
            percentToValue(i, MIN_LAMBDA, MAX_LAMBDA)

        private fun valueToPercent(f: Double, min: Double, max: Double): Int =
            (100 * (f - min) / (max - min) + 0.5).toInt().coerceIn(0, 100)

        private fun percentToValue(i: Int, min: Double, max: Double): Double =
            (min + (max - min) * (i / 100.0)).coerceIn(min, max)


    }
}
