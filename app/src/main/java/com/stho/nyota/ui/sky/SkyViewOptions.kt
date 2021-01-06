package com.stho.nyota.ui.sky

import android.view.View


interface ISkyViewSettings
{
    var displaySymbols: Boolean
    var displayMagnitude: Boolean
    var displayConstellations: Boolean
    var displayConstellationNames: Boolean
    var displayPlanetNames: Boolean
    var displayStarNames: Boolean
}

interface ISkyViewOptions: ISkyViewSettings
{
    var zoomAngle: Double
    var magnitude: Double
    var drawGrid: Boolean

    fun applyScale(scaleFactor: Double)
    fun loadSettings(settings: ISkyViewSettings)
}

class SkyViewOptions(private var view: View): ISkyViewOptions {

    override var zoomAngle: Double = DEFAULT_ZOOM_ANGLE
        set(value) {
            val validZoomAngle = value.coerceIn(MIN_ZOOM_ANGLE, MAX_ZOOM_ANGLE)
            if (field != validZoomAngle) {
                field = validZoomAngle
                view.invalidate()
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
                view.invalidate()
            }
        }

    override var displaySymbols: Boolean = true
        set(value) {
            field = value
            view.invalidate()
        }

    override var displayMagnitude: Boolean = true
        set(value) {
            field = value
            view.invalidate()
        }

    override var displayConstellations: Boolean = true
        set(value) {
            field = value
            view.invalidate()
        }

    override var displayConstellationNames: Boolean = true
        set(value) {
            field = value
            view.invalidate()
        }

    override var displayPlanetNames: Boolean = true
        set(value) {
            field = value
            view.invalidate()
        }

    override var displayStarNames: Boolean = true
        set(value) {
            field = value
            view.invalidate()
        }

    override var drawGrid: Boolean = true
        set(value) {
            field = value
            view.invalidate()
        }

    override fun loadSettings(settings: ISkyViewSettings) {
        displaySymbols = settings.displaySymbols
        displayMagnitude = settings.displayMagnitude
        displayConstellations = settings.displayConstellations
        displayConstellationNames = settings.displayConstellationNames
        displayPlanetNames = settings.displayPlanetNames
        displayStarNames = settings.displayStarNames
        view.invalidate()
    }

    companion object {

        // Sun = -26.72
        // Sirius = -1.46
        // Vega = 0.03
        // Brightness * 100 --> Magnitude - 5
        const val MAX_MAGNITUDE: Double = 10.0
        const val MIN_MAGNITUDE: Double = 0.0
        const val DEFAULT_MAGNITUDE = 10.0
        private const val MIN_ZOOM_ANGLE = 0.5
        private const val MAX_ZOOM_ANGLE = 120.0
        private const val DEFAULT_ZOOM_ANGLE = 45.0

        internal fun brightnessToPercent(f: Double): Int =
            (255 * (f - MIN_MAGNITUDE) / (MAX_MAGNITUDE - MIN_MAGNITUDE) + 0.5).toInt()

        internal fun percentToBrightness(i: Int): Double =
            MIN_MAGNITUDE + (MAX_MAGNITUDE - MIN_MAGNITUDE) * (i / 255.0)
    }
}

