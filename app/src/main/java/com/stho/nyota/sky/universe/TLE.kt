package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.Formatter
import com.stho.nyota.sky.utilities.JulianDay.toUTC
import com.stho.nyota.sky.utilities.Radian.toDegrees
import com.stho.nyota.sky.utilities.UTC
import com.stho.nyota.sky.utilities.UTC.Companion.forNow
import java.security.InvalidParameterException
import java.util.*

/**
 * Two line elements
 */
data class TLE(
        val elements: String,
        val Epoch: Double, /* Epoch of the TLE as Julian Day  */
        val bstar: Double,
        val xincl: Double,  /* Inclination (in radian) */
        val xnodeo: Double, /* Right Ascension of the Ascending Node (in radian) */
        val eo: Double, /* Eccentricity (decimal)  */
        val omegao: Double, /* Argument of Perigee (in radian)  */
        val xmo: Double, /* Mean Anomaly (in radian)  */
        val xno: Double, /* Mean Motion (in radian per minute)  */
        val noradSatelliteNumber: Int, /* NORAD satellite number */
        val revolutionNumber: Int,
        val internationalDesignator: String,
        val meanDistanceFromEarth: Double, /* Mean Distance in km  */
        val revolutionsPerDay: Double) { /* Mean Motion (in revolutions per day) */

    /** If the TLE is still valid or if aa new download is required  */
    val isOutdated: Boolean
        get() = isOutdated(this)

    fun serialize(): String = elements

    val date: Date
        get() = toUTC(Epoch).time

    override fun toString(): String {
        val difference = eo * (meanDistanceFromEarth + Algorithms.EARTH_RADIUS)
        val sb = StringBuilder()
        sb.append(String.format("NORAD = %d", noradSatelliteNumber))
        sb.append(EOL)
        sb.append(String.format("Epoch (UTC) = %s", Formatter.formatDateTime.format(toUTC(Epoch))))
        sb.append(EOL)
        sb.append(String.format("Eccentricity = %.3f", eo))
        sb.append(EOL)
        sb.append(String.format("Inclination = %.1f°", toDegrees(xincl)))
        sb.append(EOL)
        sb.append(String.format("Mean Height = %.f km", meanDistanceFromEarth))
        sb.append(EOL)
        sb.append(String.format("Perigee Height = %.f km", meanDistanceFromEarth - difference))
        sb.append(EOL)
        sb.append(String.format("Apogee Height = %.f km", meanDistanceFromEarth + difference))
        sb.append(EOL)
        sb.append(String.format("Right Ascension of Ascend Node = %.1f°", toDegrees(xnodeo)))
        sb.append(EOL)
        sb.append(String.format("Argument of Perigee = %.1f°", toDegrees(omegao)))
        sb.append(EOL)
        sb.append(String.format("Revolutions per Day = %.1f", revolutionsPerDay))
        sb.append(EOL)
        sb.append(String.format("Mean anomaly = %.1f°", toDegrees(xmo)))
        return sb.toString()
    }

    fun toSummaryString(): String {
        val difference = eo * (meanDistanceFromEarth + Algorithms.EARTH_RADIUS)
        val sb = StringBuilder()
        sb.append(String.format("Height %.0f - %.0f km", (meanDistanceFromEarth - difference), (meanDistanceFromEarth + difference)))
        sb.append(EOL)
        sb.append(String.format("Revolutions per Day = %.1f", revolutionsPerDay))
        return sb.toString()
    }

    companion object {

        private const val DAYS_TLE_KEEP_VALID: Int = 3
        private const val EOL: String = "\n"

        private fun isOutdated(tle: TLE): Boolean {
            val now: UTC = forNow()
            return (now.julianDay - tle.Epoch) > DAYS_TLE_KEEP_VALID
        }

        /*
            Deserializes the 2-line-elements into a TLE structure or throws an InvalidParameterException if parsing the elements fails
         */
        fun deserialize(elements: String): TLE {
            val tle = TLEParser().parseTLE(elements)
            if (tle != null) {
                return tle
            } else {
                throw InvalidParameterException("Invalid TLE: $elements")
            }
        }
    }
}