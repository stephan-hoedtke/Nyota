package com.stho.nyota.sky.utilities

import com.stho.nyota.sky.universe.Algorithms.GMST
import com.stho.nyota.sky.universe.Algorithms.GMST0
import com.stho.nyota.sky.universe.Algorithms.JD0
import com.stho.nyota.sky.universe.Algorithms.LST
import com.stho.nyota.sky.universe.Algorithms.UT
import java.util.*

/**
 * Class to speed up calculations of time and date in Calendar, Milliseconds and Julian Day
 * // Time API:
 * // 1. Instant, ZonedDateTime, ...
 * //       https://barta.me/new-date-time-api-in-java-8/
 * // --> recommendation: use LocalDateTime, Instant, ZonedDateTime, and not the old Date or Calendar,
 * //     but this Java API requires Android API Level >= 26
 * //       https://stackoverflow.com/questions/32437550/whats-the-difference-between-instant-and-localdatetime
 * // 2. Calendar:
 * //       https://crunchify.com/how-to-convert-time-between-timezone-in-java/
 */
@Suppress("PrivatePropertyName")
class UTC : ITime {

    private val JD: Double
    private var gmt: Calendar? = null

    override val timeInMillis: Long

    private constructor(timeInMillis: Long) {
        val utc = createCalendarGMT(timeInMillis)
        this.gmt = utc
        this.JD = JulianDay.fromGMT(utc)
        this.timeInMillis = timeInMillis
    }

    private constructor(utc: Calendar) {
        this.gmt = utc
        this.JD = JulianDay.fromGMT(utc)
        this.timeInMillis = utc.timeInMillis
    }

    private constructor(timeInMillis: Long, JD: Double) {
        this.JD = JD
        this.timeInMillis = timeInMillis
    }

    override val julianDay: Double
        get() = JD;

    fun getGmt(): Calendar {
        // The calendar is only required rarely.
        // All the calculation can be done without, just using JD. --> lets make it faster without...
        return if (gmt == null) {
            gmt = createCalendarGMT(timeInMillis)
            gmt!!
        } else {
            gmt!!
        }
    }

    val time: Date
        get() = getGmt().time

    val minutes: Double
        get() = timeInMillis / MILLIS_PER_MINUTE - 24563000

    val dayNumber: Double
        get() = JD - 2451543.5

    @Suppress("PropertyName")
    val GMST0: Double
        get() = GMST0(JD)

    @Suppress("PropertyName")
    val GMST: Double
        get() = GMST(JD)

    @Suppress("PropertyName")
    val UT: Double
        get() = UT(JD)

    /**
     * Years since 2000 Jan 1st
     *
     * @return years and faction of a year of 365.25 days
     */
    val yearsSince2000: Double
        get() = (JD - JULIAN_DAY_2000_JAN_FIRST) / JULIAN_DAYS_PER_YEAR

    /**
     * Returns the local sidereal time in angleInHours [0 ; 24]
     * The sidereal time is measured by the rotation of the Earth, with respect to the stars (rather than relative to the Sun).
     * Local sidereal time is the right ascension (RA, an equatorial coordinate) of a star on the observers meridian.
     * One sidereal day corresponds to the time taken for the Earth to rotate once with respect to the stars and lasts approximately 23 h 56 min.
     *
     * @param observerLongitude is the longitude for which the sidereal time shall be calculated in degrees.
     * @return the local sidereal time in angleInHours [0 ; 24]
     */
    fun getLST(observerLongitude: Double): Double {
        return LST(JD, observerLongitude)
    }

    fun addMillis(millis: Long): UTC {
        return UTC(timeInMillis + millis, JD + millis / MILLIS_PER_DAY)
    }

    fun addHours(hours: Double): UTC {
        val millis = MILLIS_PER_HOUR * hours
        val days = hours / HOURS_PER_DAY
        return UTC(timeInMillis + millis.toLong(), JD + days)
    }

    fun setHours(hours: Double): UTC {
        val millis = MILLIS_PER_HOUR * hours
        val days = hours / HOURS_PER_DAY
        val ut = UT(JD) * MILLIS_PER_HOUR
        return UTC(timeInMillis - ut.toLong() + millis.toLong(), JD0(JD) + days)
    }

    override fun toString(): String =
        Formatter.toString(time, Formatter.timeZoneGMT, Formatter.TimeFormat.DATETIME_SEC_TIMEZONE)

    fun isGreaterThan(that: UTC?): Boolean =
        this.JD > that?.JD ?: 0.0

    fun isLessThan(that: UTC?): Boolean =
        this.JD < that?.JD ?: 0.0

    fun serialize(): String {
        return timeInMillis.toString()
    }

    companion object {
        private val timeZoneGMT: TimeZone by lazy { TimeZone.getTimeZone("GMT") }

        fun forUTC(year: Int, month: Int, day: Int, hour: Int, minute: Int): UTC =
            UTC(createCalendarGMT(year, month, day, hour, minute, 0))

        fun forUTC(year: Int, month: Int, day: Int, hour: Int, minute: Int, seconds: Int): UTC =
            UTC(createCalendarGMT(year, month, day, hour, minute, seconds))

        fun forTimeInMillis(timeInMillis: Long): UTC =
            UTC(timeInMillis)

        fun forCalendar(calendar: Calendar): UTC =
            UTC(calendar.timeInMillis) // as calendar may belong to another timeZone

        fun forJulianDay(julianDay: Double): UTC {
            val days = julianDay - JULIAN_DAY_2000_JAN_FIRST
            val timeInMillis = JANUARY_FIRST_2000_IN_MILLIS + days * MILLIS_PER_DAY
            return UTC(timeInMillis.toLong())
        }

        private fun createCalendarGMT(timeInMillis: Long): Calendar {
            val calendar = Calendar.getInstance(timeZoneGMT)
            calendar.timeInMillis = timeInMillis
            return calendar
        }

        private fun createCalendarGMT(year: Int, month: Int, day: Int, hour: Int, minute: Int, seconds: Int): Calendar {
            val calendar = Calendar.getInstance(timeZoneGMT)
            calendar[year, month, day, hour, minute] = seconds
            calendar[Calendar.MILLISECOND] = 0
            return calendar
        }

        private const val HOURS_PER_DAY = 24.0
        private const val MILLIS_PER_DAY = 86400000.0
        private const val MILLIS_PER_HOUR = 3600000.0
        private const val MILLIS_PER_MINUTE = 60000.0
        private const val JULIAN_DAY_2000_JAN_FIRST = 2451544.5000
        private const val JULIAN_DAYS_PER_YEAR = 365.25
        private const val JANUARY_FIRST_2000_IN_MILLIS = 946684800000L

        fun forNow(): UTC {
            return UTC(System.currentTimeMillis())
        }

        fun gapInHours(a: UTC, b: UTC): Double {
            return (b.timeInMillis - a.timeInMillis) / MILLIS_PER_HOUR
        }

        fun deserialize(str: String): UTC? {
            return try {
                val timeInMillis = str.toLong()
                UTC(timeInMillis)
            } catch (ex: Exception) {
                null
            }
        }
    }
}