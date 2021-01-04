package com.stho.nyota.ui.sky

import android.view.View


interface ISkyViewSettings
{
    var displayNames: Boolean
    var displaySymbols: Boolean
    var displayMagnitude: Boolean
    var displayConstellations: Boolean
}

interface ISkyViewOptions: ISkyViewSettings
{
    var zoomAngle: Double
    var brightness: Double
    var drawGrid: Boolean

    fun applyScale(scaleFactor: Double)
    fun loadSettings(settings: ISkyViewSettings)
}

class SkyViewOptions(private var view: View): ISkyViewOptions {

    override var zoomAngle: Double = 45.0
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

    override var brightness: Double = 0.0
        set(value) {
            val validBrightness = value.coerceIn(MIN_BRIGHTNESS, MAX_BRIGHTNESS)
            if (field != validBrightness) {
                field = validBrightness
                view.invalidate()
            }
        }

    override var displayNames: Boolean = true
        set(value) {
            field = value
            view.invalidate()
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

    override var drawGrid: Boolean = true
        set(value) {
            field = value
            view.invalidate()
        }

    override fun loadSettings(settings: ISkyViewSettings) {
        displayNames = settings.displayNames
        displaySymbols = settings.displaySymbols
        displayMagnitude = settings.displayMagnitude
        displayConstellations = settings.displayConstellations
    }

    companion object {

        const val MAX_BRIGHTNESS: Double = 9.0
        const val MIN_BRIGHTNESS: Double = 0.0
        const val DEFAULT_BRIGHTNESS = 0.0
        private const val MIN_ZOOM_ANGLE = 0.5
        private const val MAX_ZOOM_ANGLE = 120.0

        internal fun brightnessToPercent(f: Double): Int =
            (255 * (f - MIN_BRIGHTNESS) / (MAX_BRIGHTNESS - MIN_BRIGHTNESS) + 0.5).toInt()

        internal fun percentToBrightness(i: Int): Double =
            MIN_BRIGHTNESS + (MAX_BRIGHTNESS - MIN_BRIGHTNESS) * (i / 255.0)
    }
}

