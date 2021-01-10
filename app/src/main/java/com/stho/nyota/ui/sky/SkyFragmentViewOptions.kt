package com.stho.nyota.ui.sky

import android.view.View


class SkyFragmentViewOptions(view: View, val settings: ISkyViewSettings): SkyViewOptions(view), ISkyViewOptions, ISkyViewSettings {

    override var displaySymbols: Boolean
        get() = settings.displaySymbols
        set(value) {
            if (settings.displaySymbols != value) {
                settings.displaySymbols = value
                invalidate()
            }
        }

    override var displayMagnitude: Boolean
        get() = settings.displayMagnitude
        set(value) {
            if (settings.displayMagnitude != value) {
                settings.displayMagnitude = value
                invalidate()
            }
        }

    override var displayConstellations: Boolean
        get() = settings.displayConstellations
        set(value) {
            if (settings.displayConstellations != value) {
                settings.displayConstellations = value
                invalidate()
            }
        }

    override var displayConstellationNames: Boolean
        get() = settings.displayConstellationNames
        set(value) {
            if (settings.displayConstellationNames != value) {
                settings.displayConstellationNames = value
                invalidate()
            }
        }

    override var displayPlanetNames: Boolean
        get() = settings.displayPlanetNames
        set(value) {
            if (settings.displayPlanetNames != value) {
                settings.displayPlanetNames = value
                invalidate()
            }
        }

    override var displayStarNames: Boolean
        get() = settings.displayStarNames
        set(value) {
            if (settings.displayStarNames != value) {
                settings.displayStarNames = value
                invalidate()
            }
        }
}


