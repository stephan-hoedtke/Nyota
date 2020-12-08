package com.stho.nyota.sky.utilities

import com.stho.nyota.BuildConfig
import com.stho.nyota.sky.universe.Algorithms
import java.security.InvalidParameterException
import java.util.*

/**
 * Created by shoedtke on 20.01.2017.
 */
object JulianDay {

    /**
     * Julian Day Number
     * @param utc a date in UTC
     * @return Julian Day Number
     * see for example: https://squarewidget.com/julian-day/ by James Still
     */
    @Suppress("LocalVariableName")
    @JvmStatic
    fun fromGMT(utc: Calendar): Double {
        if (BuildConfig.DEBUG) {
            if (utc.timeZone.rawOffset != 0) {
                throw InvalidParameterException("Julian day requires GMT")
            }
        }
        var Y: Int = utc[Calendar.YEAR]
        var M: Int = utc[Calendar.MONTH] + 1
        val D: Int = utc[Calendar.DAY_OF_MONTH]
        if (M == 1 || M == 2) {
            Y -= 1
            M += 12
        }
        val A: Int = Y / 100
        val B = if (isGregorian(Y, M, D)) 2 - A + (A / 4) else 0
        val JD = Algorithms.truncate(365.25 * (Y + 4716)) + Algorithms.truncate(30.6001 * (M + 1)) + D + B - 1524.5
        val UT = Algorithms.UT(utc)
        return JD + UT / 24.0
    }

    /**
     * Universal Time in angleInHours (without year / month / day)
     * @param JD Julian Day
     * @return Universal Time
     */
    fun UT(JD: Double): Double {
        return 24.0 * Algorithms.decimals(JD - 0.5)
    }

    private fun isGregorian(Y: Int, M: Int, D: Int): Boolean {
        return Y > 1582 || Y == 1582 && M > 10 || Y == 1582 && M == 10 && D > 4
    }
}