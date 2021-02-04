package com.stho.nyota.ui.sky

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stho.nyota.sky.utilities.IProperty
import com.stho.nyota.sky.utilities.projections.Projection
import com.stho.nyota.views.AbstractSkyView

// TODO: make ISkyViewSettings readonly (the view reads the data) and IMutableSkyViewSettings available to the fragment...

/*
    Implement by settings and stored in DB
 */
interface ISkyViewSettings
{
    var displaySymbols: Boolean
    var displayConstellations: Boolean
    var displayConstellationNames: Boolean
    var displayPlanetNames: Boolean
    var displayStarNames: Boolean
    var displayTargets: Boolean
    var displaySatellites: Boolean
    var displayGrid: Boolean
    var sphereProjection: Projection
    var magnitude: Double
    fun touch()
}

/*
    Implement by SkyViewOptions and SimpleSkyViewOptions, not persisted
 */
interface ISkyViewOptions: ISkyViewSettings {
    var radius: Double
    var gamma: Double
    var alpha: Double
}

abstract class SkyViewOptions: ISkyViewOptions {

    private val touchLiveData: MutableLiveData<Long> = MutableLiveData<Long>().apply { value = 0 }

    val touchLD: LiveData<Long>
        get() = touchLiveData

    override fun touch() {
        val v = touchLiveData.value ?: 0
        touchLiveData.postValue(v + 1)
    }

    override var radius: Double = 6.0
        set(value) {
            val validRadius = value.coerceIn(MIN_RADIUS, MAX_RADIUS)
            if (field != validRadius) {
                field = validRadius
                touch()
            }
        }

    override var gamma: Double = 15.0
        set(value) {
            val validGamma = value.coerceIn(MIN_GAMMA, MAX_GAMMA)
            if (field != validGamma) {
                field = validGamma
                touch()
            }
        }

    override var alpha: Double = 0.36
        set(value) {
            val validAlpha = value.coerceIn(MIN_ALPHA, MAX_ALPHA)
            if (field != validAlpha) {
                field = validAlpha
                touch()
            }
        }

    companion object {
        const val MAX_MAGNITUDE: Double = 10.0
        const val MIN_MAGNITUDE: Double = 0.0
        const val MIN_ZOOM_ANGLE = 0.5
        const val MAX_ZOOM_ANGLE = 120.0
        const val MIN_RADIUS: Double = 1.0
        const val MAX_RADIUS: Double = 10.0
        const val MIN_GAMMA: Double = 0.01
        const val MAX_GAMMA: Double = 20.0
        const val MIN_ALPHA: Double = 0.01
        const val MAX_ALPHA: Double = 1.00

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

        internal fun alphaToPercent(f: Double): Int =
            valueToPercent(f, MIN_ALPHA, MAX_ALPHA)

        internal fun percentToAlpha(i: Int): Double =
            percentToValue(i, MIN_ALPHA, MAX_ALPHA)

        private fun valueToPercent(f: Double, min: Double, max: Double): Int =
            (100 * (f - min) / (max - min) + 0.5).toInt()

        private fun percentToValue(i: Int, min: Double, max: Double): Double =
            (min + (max - min) * (i / 100.0)).coerceIn(min, max)

    }
}
