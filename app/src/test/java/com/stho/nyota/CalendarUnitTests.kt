package com.stho.nyota

import com.stho.nyota.sky.utilities.Formatter
import com.stho.nyota.sky.utilities.JulianDay
import com.stho.nyota.sky.utilities.UTC
import org.junit.Assert
import org.junit.Test
import java.util.*

class CalendarUnitTests
{
    @Test
    fun calendar_isCorrect() {
        calendar_isCorrect(1900, Calendar.JANUARY, 1, 0, 0)
        calendar_isCorrect(1965, Calendar.SEPTEMBER, 30, 17, 35)
        calendar_isCorrect(2020, Calendar.DECEMBER, 6, 22, 31)
        calendar_isCorrect(2020, Calendar.DECEMBER, 31, 23, 47)
    }

    private fun calendar_isCorrect(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
        calendar[year, month, day, hour, minute] = 0
        calendar[Calendar.MILLISECOND] = 0

        Assert.assertEquals("Year", year, calendar[Calendar.YEAR])
        Assert.assertEquals("Month", month, calendar[Calendar.MONTH])
        Assert.assertEquals("Day (of month)", day, calendar[Calendar.DAY_OF_MONTH])
        Assert.assertEquals("Hour (of day)", hour, calendar[Calendar.HOUR_OF_DAY])
        Assert.assertEquals("Minute", minute, calendar[Calendar.MINUTE])
        Assert.assertEquals("Second", 0, calendar[Calendar.SECOND])
        Assert.assertEquals("TimeZone Offset", 0, calendar[Calendar.ZONE_OFFSET])
    }

    @Test
    fun utc_isCorrect() {

        utc_isCorrect(1900, Calendar.JANUARY, 1, 0, 0)
        utc_isCorrect(1965, Calendar.SEPTEMBER, 30, 17, 35)
        utc_isCorrect(2020, Calendar.DECEMBER, 6, 22, 31)
        utc_isCorrect(2020, Calendar.DECEMBER, 31, 23, 47)
    }

    private fun utc_isCorrect(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
        calendar[year, month, day, hour, minute] = 0
        calendar[Calendar.MILLISECOND] = 0

        val utc: UTC = UTC.forCalendar(calendar)
        val timeStringUtc = Formatter.toString(utc, TimeZone.getTimeZone("GMT"), Formatter.TimeFormat.DATETIME_SEC_TIMEZONE)
        val timeStringCalendar = Formatter.toString(calendar, Formatter.TimeFormat.DATETIME_SEC_TIMEZONE)

        Assert.assertEquals("UTC(Calendar) = Calendar", timeStringUtc, timeStringCalendar)
    }

    @Test
    fun calendar_timeInMillis_isCorrect() {

        calendar_timeInMillis_isCorrect(1900, Calendar.JANUARY, 1, 0, 0)
        calendar_timeInMillis_isCorrect(1965, Calendar.SEPTEMBER, 30, 17, 35)
        calendar_timeInMillis_isCorrect(2020, Calendar.DECEMBER, 6, 22, 31)
        calendar_timeInMillis_isCorrect(2020, Calendar.DECEMBER, 31, 23, 47)
    }

    private fun calendar_timeInMillis_isCorrect(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
        val gmt = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
        gmt[year, month, day, hour, minute] = 0
        gmt[Calendar.MILLISECOND] = 0

        val cet = Calendar.getInstance(TimeZone.getTimeZone("CET"))
        cet[year, month, day, hour, minute] = 0
        cet[Calendar.MILLISECOND] = 0
        val offset = cet.timeZone.getOffset(cet.timeInMillis)
        cet.add(Calendar.MILLISECOND, offset)

        val timeStringGmt = Formatter.toString(gmt, Formatter.TimeFormat.DATETIME_SEC_TIMEZONE)
        val timeStringCet = Formatter.toString(cet, Formatter.TimeFormat.DATETIME_SEC_TIMEZONE)

        Assert.assertNotEquals("GMT != CET", timeStringGmt, timeStringCet)
        Assert.assertEquals("timeInMills for $timeStringGmt and $timeStringCet", gmt.timeInMillis, cet.timeInMillis)
    }

}