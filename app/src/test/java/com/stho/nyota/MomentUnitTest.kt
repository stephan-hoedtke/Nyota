package com.stho.nyota

import com.stho.nyota.sky.utilities.*
import com.stho.nyota.sky.utilities.Formatter
import org.junit.Assert
import org.junit.Test
import java.util.*
import kotlin.math.truncate

class MomentUnitTest : AbstractAstronomicUnitTest() {

    @Test
    fun moment_setHours_isCorrect() {
        val berlin: City = City.createDefaultBerlin()
        val utc: UTC = getCESTasUTC(2017, Calendar.JUNE, 20, 9, 7)
        val moment: IMoment = Moment(berlin, utc)
        val UT: Double = 11.122707

        val hours: Int = truncate(UT).toInt()
        val minutes: Int = truncate((UT - hours) * MINUTES_PER_HOUR).toInt()
        val seconds: Int = truncate((UT - hours - minutes / MINUTES_PER_HOUR) * SECONDS_PER_HOUR).toInt()
        val millis: Int = truncate((UT - hours - minutes / MINUTES_PER_HOUR - seconds / SECONDS_PER_HOUR) * MILLISECONDS_PER_HOUR).toInt()
        val referenceCalendar: Calendar = getCalendar("CET", 2017, Calendar.JUNE, 20, 13, minutes, seconds, millis)
        val referenceMoment: Moment = Moment(berlin, UTC.forCalendar(referenceCalendar))

        val newUtc: UTC = moment.utc.setHours(UT) // --> 11.122707 GMT meaning 13.122707 in Berlin already
        val newMoment: Moment = Moment(berlin, newUtc)

        Assert.assertEquals("Hours", 11, hours)
        Assert.assertEquals("Minutes", 7, minutes)
        Assert.assertEquals("Seconds", 21, seconds)
        Assert.assertEquals("Reference UTC","20 Jun 2017 13:07:21 +0200", Formatter.toString(referenceCalendar, Formatter.TimeFormat.DATETIME_SEC_TIMEZONE))
        Assert.assertEquals("Moment", "20 Jun 2017 09:07:00 +0200", moment.toString())
        Assert.assertEquals("Reference Moment","20 Jun 2017 13:07:21 +0200", referenceMoment.toString())
        Assert.assertEquals("Universal Time", UT, newUtc.UT, EPS)
        Assert.assertEquals("New Moment","20 Jun 2017 13:07:21 +0200", newMoment.toString())
        Assert.assertEquals("New UTC / GMT","20 Jun 2017 11:07:21 +0000", Formatter.toString(newUtc, TimeZone.getTimeZone("GMT"), Formatter.TimeFormat.DATETIME_SEC_TIMEZONE))
        Assert.assertEquals("New UTC / Berlin","20 Jun 2017 13:07:21 +0200", Formatter.toString(newUtc, TimeZone.getTimeZone("CET"), Formatter.TimeFormat.DATETIME_SEC_TIMEZONE))
        Assert.assertEquals("New = Reference", referenceMoment.utc.timeInMillis, newMoment.utc.timeInMillis)
    }

    companion object {
        private const val EPS = 0.00000001
        private const val MINUTES_PER_HOUR = 60.0
        private const val SECONDS_PER_HOUR = 3600.0
        private const val MILLISECONDS_PER_HOUR = 3600000.0
    }
}
