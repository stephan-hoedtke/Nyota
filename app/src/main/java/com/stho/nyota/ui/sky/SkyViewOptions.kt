package com.stho.nyota.ui.sky

import android.view.View
import com.stho.nyota.sky.utilities.projections.Projection

// TODO: make ISkyViewSettings readonly (the view reads the data) and IMutableSkyViewSettings available to the fragment...

/*
    Implement by settings and stored in DB
 */
interface ISkyViewSettings
{
    var displaySymbols: Boolean
    var displayMagnitude: Boolean
    var displayConstellations: Boolean
    var displayConstellationNames: Boolean
    var displayPlanetNames: Boolean
    var displayStarNames: Boolean
    var displayTargets: Boolean
    var displaySatellites: Boolean
    var sphereProjection: Projection
}

/*
    Implement by SkyViewOptions and SimpleSkyViewOptions, not persisted
 */
interface ISkyViewOptions: ISkyViewSettings {
    var zoomAngle: Double
    var magnitude: Double
    var displayGrid: Boolean
    fun applyScale(scaleFactor: Double)
}

abstract class SkyViewOptions(private val view: View): ISkyViewOptions {

    override var zoomAngle: Double = DEFAULT_ZOOM_ANGLE
        set(value) {
            val validZoomAngle = value.coerceIn(MIN_ZOOM_ANGLE, MAX_ZOOM_ANGLE)
            if (field != validZoomAngle) {
                field = validZoomAngle
                invalidate()
            }
        }

    override fun applyScale(scaleFactor: Double) {
        zoomAngle /= scaleFactor
    }

    override var magnitude: Double = DEFAULT_MAGNITUDE
        set(value) {
            val validMagnitude = value.coerceIn(MIN_MAGNITUDE, MAX_MAGNITUDE)
            if (field != validMagnitude) {
                field = validMagnitude
                invalidate()
            }
        }

    override var displayGrid: Boolean = true
        set(value) {
            field = value
            invalidate()
        }

    fun invalidate() =
        view.invalidate()


    companion object {
        const val MAX_MAGNITUDE: Double = 10.0
        const val MIN_MAGNITUDE: Double = 0.0
        const val DEFAULT_MAGNITUDE = 10.0
        const val MIN_ZOOM_ANGLE = 0.5
        const val MAX_ZOOM_ANGLE = 120.0
        const val DEFAULT_ZOOM_ANGLE = 45.0

        internal fun brightnessToPercent(f: Double): Int =
            (100 * (f - MIN_MAGNITUDE) / (MAX_MAGNITUDE - MIN_MAGNITUDE) + 0.5).toInt()

        internal fun percentToBrightness(i: Int): Double =
            MIN_MAGNITUDE + (MAX_MAGNITUDE - MIN_MAGNITUDE) * (i / 100.0)
    }
}
