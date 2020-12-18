package com.stho.nyota

import com.stho.nyota.sky.utilities.City
import com.stho.nyota.sky.utilities.Location
import com.stho.nyota.sky.utilities.UTC
import org.junit.Assert
import java.util.*

/**
 * Created by shoedtke on 24.09.2016.
 */
abstract class AbstractAstronomicUnitTest {

    @Throws(Exception::class)
    protected fun assertCalendar(text: String?, expected: UTC, actual: UTC) {
        Assert.assertEquals(text, expected.minutes, actual.minutes, ONE_MINUTE)
    }

    @Throws(Exception::class)
    protected fun assertPosition(text: String?, expected: Double, actual: Double) {
        Assert.assertEquals(text, expected, actual, ONE_DEGREE)
    }

    companion object {
        const val EPS = 0.0001
        const val ONE_DEGREE = 1.0
        const val ONE_MINUTE = 1.0
        const val ONE_MINUTE_IN_DEGREES = 0.01
        const val ONE_MINUTE_IN_DAYS = 0.0005

        private fun getUTC(year: Int, month: Int, day: Int): UTC {
            val calendar = getCalendar("GMT", year, month, day, 0, 0, 0)
            return UTC.forTimeInMillis(calendar.timeInMillis)
        }

        @JvmStatic
        protected fun getUTC(year: Int, month: Int, day: Int, hour: Int, minute: Int, seconds: Int = 0, millis: Int = 0): UTC {
            val calendar = getCalendar("GMT", year, month, day, hour, minute, seconds, millis)
            return UTC.forTimeInMillis(calendar.timeInMillis)
        }

        @JvmStatic
        protected fun getCESTasUTC(year: Int, month: Int, day: Int, hour: Int, minute: Int, seconds: Int = 0, millis: Int = 0): UTC {
            val calendar = getCalendar("CET", year, month, day, hour, minute, seconds, millis)
            return UTC.forTimeInMillis(calendar.timeInMillis)
        }

        internal fun getCalendar(timezone: String, year: Int, month: Int, day: Int, hour: Int = 0, minute: Int = 0, seconds: Int = 0, millis: Int = 0): Calendar {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone(timezone))
            calendar[year, month, day, hour, minute] = seconds
            calendar[Calendar.MILLISECOND] = millis
            return calendar
        }

        @JvmStatic
        internal fun getObserver(latitude: Double, longitude: Double): Location {
            return Location(latitude, longitude)
        }

        internal fun getCity(latitude: Double, longitude: Double): City {
            return City("Test", Location(latitude = latitude, longitude = longitude), TimeZone.getDefault())
        }

        internal fun getCity(latitude: Double, longitude: Double, altitude: Double, timeZone: String?): City {
            return City("Test", Location(latitude = latitude, longitude = longitude, altitude = altitude), TimeZone.getTimeZone(timeZone))
        }

        internal fun getCity(location: Location): City {
            return City("Test", location, TimeZone.getDefault())
        }
    }
}

