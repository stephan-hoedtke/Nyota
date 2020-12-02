package com.stho.nyota.sky.utilities

import com.stho.nyota.Interval
import java.util.*

/**
 * Created by shoedtke on 16.09.2016.
 */
class Moment(override val city: City, override val utc: UTC) : IMoment {

    override val lst: Double = utc.getLST(city.longitude) // in hours [0 ; 24]
    override val d: Double = utc.dayNumber

    override val location: Location
        get() = city.location

    override val timeZone: TimeZone
        get() = city.timeZone

    val version: Long = System.nanoTime()

    val localTime: Calendar
        get() {
            val calendar = Calendar.getInstance(city.timeZone)
            calendar.timeInMillis = utc.timeInMillis
            return calendar
        }

    override fun toString(): String {
        return Formatter.toString(utc.time, city.timeZone, Formatter.TimeFormat.DATETIME_SEC_TIMEZONE)
    }

    fun forNow(): Moment {
        return Moment(city, UTC.forNow())
    }

    fun forThisDate(year: Int, month: Int, dayOfMonth: Int): Moment {
        val calendar = localTime
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        return Moment(city, UTC.forCalendar(calendar))
    }

    fun forThisTime(hourOfDay: Int, minute: Int): Moment {
        val calendar = localTime
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        return Moment(city, UTC.forCalendar(calendar))
    }

    /**
     * Keep current location and timezone, but set a new time by adding milliseconds
     * @param millis
     * @return
     */
    private fun addMillis(millis: Long): Moment {
        return Moment(city, utc.addMillis(millis))
    }

    /**
     * Keep current location and timezone, but set a new time by adding hours
     * @param hours any time difference in hours
     * @return a new moment instance
     */
    private fun addHours(hours: Double): Moment {
        return Moment(city, utc.addHours(hours))
    }

    /**
     * Keep current location and timezone, but set a new time by adding months
     * @param months
     * @return
     */
    private fun addMonths(months: Int): Moment {
        val calendar = localTime
        calendar.add(Calendar.MONTH, months)
        return Moment(city, UTC.forCalendar(calendar))
    }

    /**
     * Keep current location and timezone, but set a new time by adding years
     * @param years
     * @return
     */
    private fun addYears(years: Int): Moment {
        val calendar = localTime
        calendar.add(Calendar.YEAR, years)
        return Moment(city, UTC.forCalendar(calendar))
    }

    internal fun next(interval: Interval): Moment =
        when (interval) {
            Interval.MINUTE -> addMillis(60000)
            Interval.HOUR -> addHours(1.0)
            Interval.DAY -> addHours(24.0)
            Interval.MONTH -> addMonths(1)
            Interval.YEAR -> addYears(1)
        }


    internal fun previous(interval: Interval): Moment =
        when (interval) {
            Interval.MINUTE -> addMillis(-60000)
            Interval.HOUR -> addHours(-1.0)
            Interval.DAY -> addHours(-24.0)
            Interval.MONTH -> addMonths(-1)
            Interval.YEAR -> addYears(-1)
        }


    companion object {

        fun forUTC(city: City, utc: UTC): Moment =
            Moment(city, utc)

        fun forUTC(city: City, timeInMillis: Long): Moment =
            Moment(city, UTC(timeInMillis))

        fun forNow(city: City): Moment =
            Moment(city, UTC.forNow())
    }
}