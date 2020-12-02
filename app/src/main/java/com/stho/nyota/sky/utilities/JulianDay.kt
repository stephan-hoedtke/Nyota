package com.stho.nyota.sky.utilities

import com.stho.nyota.BuildConfig
import com.stho.nyota.sky.universe.Algorithms
import java.security.InvalidParameterException
import java.util.*

/**
 * Created by shoedtke on 20.01.2017.
 */
object JulianDay {
    private const val MILLIS_PER_DAY = 86400000.0
    private const val JULIAN_DAY_2000_JAN_FIRST = 2451544.5000
    private const val JANUARY_FIRST_2000_IN_MILLIS = 946684800000L

    /**
     * Julian Day Number
     * @param utc a date in UTC
     * @return Julian Day Number
     */
    @JvmStatic
    fun fromGMT(utc: Calendar): Double {
        if (BuildConfig.DEBUG) {
            if (utc.timeZone.rawOffset != TimeZone.getTimeZone("GMT").rawOffset) {
                throw InvalidParameterException("Julian day requires GMT")
            }
        }
        var Y = utc[Calendar.YEAR]
        var M = utc[Calendar.MONTH] + 1
        val D = utc[Calendar.DAY_OF_MONTH]
        if (M <= 2) {
            Y = Y - 1
            M = M + 12
        }
        val A = Math.rint(Y / 100.toDouble())
        var B = 0.0
        if (isGregorian(Y, M, D)) B = Algorithms.truncate(2 - A + Algorithms.truncate(A / 4))
        return Algorithms.truncate(365.25 * (Y + 4716)) + Algorithms.truncate(30.6001 * (M + 1)) + D + B - 1524.5 + Algorithms.UT(utc) / 24.0
    }

    //TODO use this here...
    fun toUTC(julianDay: Double): UTC {
        val millis = toTimeInMillis(julianDay)
        return UTC(millis)
    }

    //TODO: replace
    fun toTimeInMillis(julianDay: Double): Long {
        val days = julianDay - JULIAN_DAY_2000_JAN_FIRST
        val millis = JANUARY_FIRST_2000_IN_MILLIS + days * MILLIS_PER_DAY
        return millis.toLong()
    }

    /**
     * Universal Time in angleInHours (without year / month / day)
     * @param JD Julian Day
     * @return Universal Time
     */
    fun UT(JD: Double): Double {
        return 24.0 * Algorithms.getDecimals(JD - 0.5)
    }

    private fun isGregorian(Y: Int, M: Int, D: Int): Boolean {
        return Y > 1582 || Y == 1582 && M > 10 || Y == 1582 && M == 10 && D > 4
    }
}