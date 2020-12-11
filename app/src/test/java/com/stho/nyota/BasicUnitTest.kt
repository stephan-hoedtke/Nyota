package com.stho.nyota

import com.stho.nyota.sky.universe.Algorithms
import com.stho.nyota.sky.utilities.*
import com.stho.nyota.sky.utilities.Formatter
import org.junit.Assert
import org.junit.Test
import java.util.*

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@Suppress("LocalVariableName")
class BasicUnitTest {

    @Test
    fun addition_isCorrect() {
        Assert.assertEquals(4, (2 + 2).toLong())
    }

    @Test
    fun truncate_isCorrect() {
        Assert.assertEquals("2.7", 2.0, Algorithms.truncate(2.7), EPS)
        Assert.assertEquals("0.7", 0.0, Algorithms.truncate(0.7), EPS)
        Assert.assertEquals("0.0", 0.0, Algorithms.truncate(0.0), EPS)
        Assert.assertEquals("-0.7", 0.0, Algorithms.truncate(-0.7), EPS)
        Assert.assertEquals("-2.7", -2.0, Algorithms.truncate(-2.7), EPS)
    }

    @Test
    fun getDecimals_isCorrect() {
        Assert.assertEquals("2.7", 0.7, Algorithms.decimals(2.7), EPS)
        Assert.assertEquals("0.7", 0.7, Algorithms.decimals(0.7), EPS)
        Assert.assertEquals("0.0", 0.0, Algorithms.decimals(0.0), EPS)
        Assert.assertEquals("-0.7", -0.7, Algorithms.decimals(-0.7), EPS)
        Assert.assertEquals("-2.7", -0.7, Algorithms.decimals(-2.7), EPS)
    }

    @Test
    fun angle_normalize_isCorrect() {
        // Double
        Assert.assertEquals("+113.7", 113.7, Degree.normalize(+113.7), EPS)
        Assert.assertEquals("+830.7", 113.7, Degree.normalize(+833.7), EPS)
        Assert.assertEquals("-880.7", 246.3, Degree.normalize(-833.7), EPS)
    }

    @Test
    fun radian_isCorrect() {
        Assert.assertEquals("+180", PI, Radian.fromDegrees(180.0), EPS)
        Assert.assertEquals("+90", -PI2, Radian.fromDegrees(-90.0), EPS)
        Assert.assertEquals("-90", -PI32, Radian.fromDegrees(-270.0), EPS)
        Assert.assertEquals("+180", PI, Radian.normalize(PI), EPS)
        Assert.assertEquals("+90", PI2, Radian.normalize(PI2), EPS)
        Assert.assertEquals("-90", PI32, Radian.normalize(-PI2), EPS)
        Assert.assertEquals("-270", PI2, Radian.normalize(-PI32), EPS)
    }

    @Test
    fun calendar_usage_isCorrect() {
        val calendar = Calendar.getInstance()
        calendar[1965, Calendar.SEPTEMBER] = 30

        Assert.assertEquals(calendar[Calendar.YEAR], 1965)
        Assert.assertEquals(calendar[Calendar.MONTH], 8)
        Assert.assertEquals(calendar[Calendar.MONTH], Calendar.SEPTEMBER)
        Assert.assertEquals(calendar[Calendar.DAY_OF_MONTH], 30)
    }

    @Test
    fun calendar_timeInMillis_usage_isCorrect() {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
        calendar[2000, Calendar.JANUARY, 1, 0, 0] = 0
        calendar[Calendar.MILLISECOND] = 0

        Assert.assertEquals("Jan 1st 2000 in millis", JANUARY_FIRST_2000_IN_MILLIS, calendar.timeInMillis)
    }

    @Test
    fun julianDay_isCorrect() {
        julianDay_isCorrect(1582, Calendar.OCTOBER, 15, 0, 0, 0, 2299160.5000)
        julianDay_isCorrect(2016, Calendar.AUGUST, 30, 8, 38, 41, 2457630.8602)
        julianDay_isCorrect(1900, Calendar.JANUARY, 1, 0, 0, 0, 2415020.5000)
        julianDay_isCorrect(1990, Calendar.JANUARY, 1, 0, 0, 0, 2447892.5000)
        julianDay_isCorrect(1990, Calendar.JANUARY, 1, 12, 0, 0, 2447893.0000)
        julianDay_isCorrect(1999, Calendar.DECEMBER, 31, 0, 0, 0, 2451543.5000)
        julianDay_isCorrect(2000, Calendar.JANUARY, 1, 0, 0, 0, 2451544.5000)
        julianDay_isCorrect(2000, Calendar.JANUARY, 1, 12, 0, 0, 2451545.0000)
        julianDay_isCorrect(2006, Calendar.JANUARY, 14, 16, 30, 0, 2453750.1875)
        julianDay_isCorrect(2010, Calendar.MARCH, 25, 16, 30, 0, 2455281.1875)
        julianDay_isCorrect(2143, Calendar.JULY, 15, 12, 0, 0, 2503970.0000)
        julianDay_isCorrect(1987, Calendar.APRIL, 10, 0, 0, 0, 2446895.5000)
        julianDay_isCorrect(1987, Calendar.APRIL, 10, 19, 21, 0, 2446896.30625)
        julianDay_isCorrect(1957, Calendar.OCTOBER, 4, 19, 26, 24, 2436116.31)
        julianDay_isCorrect(333, Calendar.JANUARY, 27, 12, 0, 0, 1842713.0)
        julianDay_isCorrect(1990, Calendar.APRIL, 19, 0, 0, 0, 2448000.5000)
        julianDay_isCorrect(2020, Calendar.DECEMBER, 6, 20, 36, 0,  2459190.35833)
        julianDay_isCorrect(2007, Calendar.JANUARY, 14, 13, 18, 59,  2454115.05486)
    }

    private fun julianDay_isCorrect(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int, expected: Double) {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
        calendar[year, month, day, hour, minute] = second
        calendar[Calendar.MILLISECOND] = 0
        val utc: UTC = UTC.forCalendar(calendar)
        val actual = JulianDay.fromGMT(calendar)
        val timeString = Formatter.toString(utc, TimeZone.getTimeZone("GMT"), Formatter.TimeFormat.DATETIME_SEC_TIMEZONE)
        Assert.assertEquals("JD for $utc = ${timeString}", expected, actual, EPS)
    }

    @Test
    fun yearsSince2000_isCorrect() {
        val utc: UTC = UTC.forUTC(2016, Calendar.SEPTEMBER, 25, 17, 31, 10)
        Assert.assertEquals("years since 2000", 16.7357, utc.yearsSince2000, EPS)
    }

    @Test
    fun utc_isCorrect() {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
        calendar[Calendar.MILLISECOND] = 0
        val JD: Double = JulianDay.fromGMT(calendar)
        val millis: Long = UTC.forJulianDay(JD).timeInMillis
        Assert.assertEquals("UTC --> JD --> millis", millis.toFloat(), calendar.timeInMillis.toFloat(), 1f)
    }

    @Test
    fun universalTime_isCorrect() {
        universalTime_isCorrect(2016, Calendar.SEPTEMBER, 17, 14, 9, 25)
        universalTime_isCorrect(2000, Calendar.SEPTEMBER, 17, 14, 9, 25)
        universalTime_isCorrect(1990, Calendar.SEPTEMBER, 17, 14, 9, 25)
        universalTime_isCorrect(2000, Calendar.SEPTEMBER, 17, 0, 0, 0)
        universalTime_isCorrect(1987, Calendar.APRIL, 10, 0, 0, 0)
        universalTime_isCorrect(1987, Calendar.APRIL, 10, 19, 21, 35)
        universalTime_isCorrect(1990, Calendar.APRIL, 19, 0, 0, 0)
    }

    private fun universalTime_isCorrect(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int) {
        val utc: UTC = UTC.forUTC(year, month, day, hour, minute, second)
        val time = hour + minute / 60.0 + second / 3600.0
        Assert.assertEquals("UT of $utc", time, utc.UT, EPS)
    }

    @Test
    fun siderealTime_isCorrect() {
        siderealTime_isCorrect(1987, Calendar.APRIL, 10, 0, 0, 0, Hour.fromHour(13, 10, 46.1351).angleInHours, 0.0)
        siderealTime_isCorrect(1987, Calendar.APRIL, 10, 19, 21, 0, Hour.fromHour(8, 34, 57.0896).angleInHours, 0.0)
        siderealTime_isCorrect(1990, Calendar.APRIL, 19, 0, 0, 0, 14.78925, 15.0)
    }

    private fun siderealTime_isCorrect(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int, expected: Double, longitude: Double) {
        val utc: UTC = UTC.forUTC(year, month, day, hour, minute, second)
        val GMST: Double = utc.GMST
        val LST: Double = Hour.normalize(GMST + longitude / 15)
        Assert.assertEquals("GMST for $utc", expected, LST, ONE_MINUTE_IN_DAYS)
    }

    @Test
    fun sin_isCorrect() {
        Assert.assertEquals("SIN 1104.06528413449996", 0.970019377784596, Degree.sin(104.06528413449996), EPS)
    }

    @Test
    fun cos_isCorrect() {
        Assert.assertEquals("COS 104.06528413449996", -0.243027315679457, Degree.cos(104.06528413449996), EPS)
    }

    @Test
    fun utc_julianDay_isCorrect() {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
        calendar[2016, 9, 15, 17, 25] = 33
        calendar[Calendar.MILLISECOND] = 0

        val JD: Double = JulianDay.fromGMT(calendar)
        val utc: UTC = UTC.forUTC(2016, 9, 15, 17, 25, 33)

        Assert.assertEquals("UTC JD", JD, utc.julianDay, EPS)
    }

    companion object {
        private const val EPS = 0.0001
        private const val ONE_MINUTE_IN_DAYS = 0.0005
        private const val JANUARY_FIRST_2000_IN_MILLIS = 946684800000L
        private const val PI = Math.PI
        private const val PI2 = Math.PI / 2
        private const val PI32 = Math.PI * 3 / 2
    }
}