package com.stho.nyota.sky.utilities

import java.io.Serializable

/**
 * Topocentric, that is, horizontal coordinates, in relation to an observer at the surface of the earth, with azimuth in degrees, altitude in degrees and, if applicable, distance in km.
 * Create a topocentric position by azimuth in degrees (from North, clockwise), altitude in degrees (from horizon to North) and the distance in km
 * @param azimuth
 * @param altitude
 * @param distance
 *
 * horizontal coordinates, from North (0°) through East (90°), South (180°), West (270°) and back to North.
*/
class Topocentric(var azimuth: Double, var altitude: Double, var distance: Double = 0.0) : Serializable {
    var nextSetTime: UTC? = null
    var nextRiseTime: UTC? = null
    var setTime: UTC? = null
    var riseTime: UTC? = null
    var prevSetTime: UTC? = null
    var prevRiseTime: UTC? = null
    var inSouth: UTC? = null
    var culmination = 0.0

    override fun toString(): String {
        return Angle.toString(azimuth, Angle.AngleType.AZIMUTH) + Formatter.SPACE + Angle.toString(altitude, Angle.AngleType.ALTITUDE)
    }

    val isVisible: Boolean =
        altitude > 5.0 && altitude < 175.0

    val isAboveHorizon: Boolean =
        altitude >= 0.0 && altitude <= 180.0

    val isBelowHorizon: Boolean =
        altitude < 0.0 || altitude > 180.0

    val isDizzy: Boolean =
        altitude >= 0.0 && altitude <= 5.0

    companion object {
        fun isAboveHorizon(position: Topocentric?) = position?.isAboveHorizon ?: false
        fun isBelowHorizon(position: Topocentric?) = position?.isBelowHorizon ?: false
        fun isVisible(position: Topocentric?) = position?.isVisible ?: false
        fun isDizzy(position: Topocentric?) = position?.isDizzy ?: false
    }

}

