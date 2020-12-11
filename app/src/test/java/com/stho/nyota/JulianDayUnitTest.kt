package com.stho.nyota


import com.stho.nyota.sky.utilities.JulianDay
import com.stho.nyota.sky.utilities.UTC
import org.junit.Assert
import org.junit.Test
import java.util.*
import kotlin.math.abs

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class JulianDayUnitTest {

    @Test
    fun julianDay_isCorrect() {
        val a = UTC.forCalendar(thisDate)
        val JD = a.julianDay
        val b  = UTC.forJulianDay(JD)
        val difference = abs(a.timeInMillis.toDouble() - b.timeInMillis)

        Assert.assertTrue("UTC -> JD -> UTC", difference < 3)
    }

    private val thisDate: Calendar
        get() {
            val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
            calendar[2020, Calendar.DECEMBER, 6, 21, 19] = 0
            return calendar
        }
}