package com.stho.nyota.sky.utilities

import java.io.Serializable
import java.lang.Math.pow
import java.text.FieldPosition
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sqrt

/**
 * Topocentric, that is, horizontal coordinates, in relation to an observer at the surface of the earth, with azimuth in degrees, altitude in degrees and, if applicable, distance in km.
 * Create a topocentric position by azimuth in degrees (from North, clockwise), altitude in degrees (from horizon to North) and the distance in km
 * @param azimuth
 * @param altitude
 * @param distance
 *
 * horizontal coordinates, from North (0°) through East (90°), South (180°), West (270°) and back to North.
*/
class Topocentric(var azimuth: Double, var altitude: Double, var distance: Double = 0.0) {
    var nextSetTime: UTC? = null
    var nextRiseTime: UTC? = null
    var setTime: UTC? = null
    var riseTime: UTC? = null
    var prevSetTime: UTC? = null
    var prevRiseTime: UTC? = null
    var isUp: Boolean = false
    var inSouth: UTC? = null
    var culmination = 0.0

    override fun toString(): String =
        Angle.toString(azimuth, Angle.AngleType.AZIMUTH) + Formatter.SPACE + Angle.toString(altitude, Angle.AngleType.ALTITUDE)

    val isVisible: Boolean
        get() = altitude > 5.0 && altitude < 175.0

    val isAboveHorizon: Boolean
        get() = altitude >= 0.0 && altitude <= 180.0

    val isBelowHorizon: Boolean
        get() = altitude < 0.0 || altitude > 180.0

    val isDizzy: Boolean
        get() = altitude >= 0.0 && altitude <= 5.0

    val azimuthDistanceFactor: Double
        get() = abs(Degree.cos(altitude)).coerceAtLeast(0.0001)

    fun isNear(position: Topocentric, toleranceInDegree: Double): Boolean =
        isNear(position, toleranceInDegree / azimuthDistanceFactor, toleranceInDegree)

    fun isNear(position: Topocentric, azimuthTolerance: Double, altitudeTolerance: Double): Boolean =
        abs(Degree.difference(azimuth, position.azimuth)) < azimuthTolerance && abs(Degree.difference(altitude, position.altitude)) < altitudeTolerance

    fun distanceTo(position: Topocentric): Double {
        val x = Degree.difference(azimuth, position.azimuth)
        val y = Degree.difference(altitude, position.altitude)
        return sqrt(x * x + y * y)
    }

    companion object {
        const val INVALID_DISTANCE: Double = 10000.0
        fun isAboveHorizon(position: Topocentric?) = position?.isAboveHorizon ?: false
        fun isBelowHorizon(position: Topocentric?) = position?.isBelowHorizon ?: false
        fun isVisible(position: Topocentric?) = position?.isVisible ?: false
        fun isDizzy(position: Topocentric?) = position?.isDizzy ?: false
    }

}

