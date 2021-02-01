package com.stho.nyota.ui.sky

import android.view.View
import com.stho.nyota.sky.utilities.projections.Projection


class SkyFragmentViewOptions(view: View, val settings: ISkyViewSettings): SkyViewOptions(view), ISkyViewOptions, ISkyViewSettings {

    override fun touch() {
        settings.touch()
        invalidate()
    }

    override var displaySymbols: Boolean
        get() = settings.displaySymbols
        set(value) {
            if (settings.displaySymbols != value) {
                settings.displaySymbols = value
                touch()
            }
        }

    override var displayConstellations: Boolean
        get() = settings.displayConstellations
        set(value) {
            if (settings.displayConstellations != value) {
                settings.displayConstellations = value
                touch()
            }
        }

    override var displayConstellationNames: Boolean
        get() = settings.displayConstellationNames
        set(value) {
            if (settings.displayConstellationNames != value) {
                settings.displayConstellationNames = value
                touch()
            }
        }

    override var displayPlanetNames: Boolean
        get() = settings.displayPlanetNames
        set(value) {
            if (settings.displayPlanetNames != value) {
                settings.displayPlanetNames = value
                touch()
            }
        }

    override var displayStarNames: Boolean
        get() = settings.displayStarNames
        set(value) {
            if (settings.displayStarNames != value) {
                settings.displayStarNames = value
                touch()
            }
        }

    override var displayTargets: Boolean
        get() = settings.displayTargets
        set(value) {
            if (settings.displayTargets != value) {
                settings.displayTargets = value
                touch()
            }
        }

    override var displaySatellites: Boolean
        get() = settings.displaySatellites
        set(value) {
            if (settings.displaySatellites != value) {
                settings.displaySatellites = value
                touch()
            }
        }

    override var displayGrid: Boolean
        get() = settings.displayGrid
        set(value) {
            if (settings.displayGrid != value) {
                settings.displayGrid = value
                touch()
            }
        }

    override var sphereProjection: Projection
        get() = settings.sphereProjection
        set(value) {
            if (settings.sphereProjection != value) {
                settings.sphereProjection = value
                touch()
            }
        }

    override var magnitude: Double
        get() = settings.magnitude
        set(value) {
            val validMagnitude = value.coerceIn(MIN_MAGNITUDE, MAX_MAGNITUDE)
            if (settings.magnitude != validMagnitude) {
                settings.magnitude = validMagnitude
                touch()
            }
        }

}


