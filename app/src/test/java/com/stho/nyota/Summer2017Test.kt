package com.stho.nyota

import com.stho.nyota.sky.universe.Sun
import com.stho.nyota.sky.universe.Universe
import com.stho.nyota.sky.utilities.City
import com.stho.nyota.sky.utilities.Location
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.sky.utilities.UTC
import org.junit.Test
import java.util.*


/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
class Summer2017Test : AbstractAstronomicUnitTest() {

    @Test
    fun sunRise_sunSet_areCorrect() {

        // Calculate the moment of the highest Sun position
        // Calculate the day of the longest Sun above the horizon

        // Finding:
        // Tue 20th was reported as longest day (from sun-rise to sun-down) although 21st would have been correct.
        // More: Starting with 13:08 at Tue 20th the sun-rise was 4:41 and sun-set 21:34, while starting with 8:00 it was 4:42 and 21.33 respectively

        // 20.06.2017 20:07 --> 4:40 to 21.32
        // 20.06.2017 11:07 --> 4:42 to 21.33
        // 20.06.2017 09:07 --> 4:42 to 21.34

        // Bernau bei Berlin
        // 52°41'N / 13°35'E
        val buch: City = City.createNewCity("Buch", Location(52.64, 13.49), TimeZone.getDefault())
        val expectedPreviousSetTime: UTC = getCESTasUTC(2017, Calendar.JUNE, 19, 21, 33)
        val expectedRiseTime: UTC = getCESTasUTC(2017, Calendar.JUNE, 20, 4, 42)
        val expectedSetTime: UTC = getCESTasUTC(2017, Calendar.JUNE, 20, 21, 33)
        val expectedNextRiseTime: UTC = getCESTasUTC(2017, Calendar.JUNE, 21, 4, 42)

        sunRise_sunSet_areCorrect(Moment.forUTC(buch, getCESTasUTC(2017, Calendar.JUNE, 20, 3, 7)), expectedRiseTime, expectedPreviousSetTime)
        sunRise_sunSet_areCorrect(Moment.forUTC(buch, getCESTasUTC(2017, Calendar.JUNE, 20, 9, 7)), expectedRiseTime, expectedSetTime)
        sunRise_sunSet_areCorrect(Moment.forUTC(buch, getCESTasUTC(2017, Calendar.JUNE, 20, 13, 7)), expectedRiseTime, expectedSetTime)
        sunRise_sunSet_areCorrect(Moment.forUTC(buch, getCESTasUTC(2017, Calendar.JUNE, 20, 20, 7)), expectedRiseTime, expectedSetTime)
        sunRise_sunSet_areCorrect(Moment.forUTC(buch, getCESTasUTC(2017, Calendar.JUNE, 20, 23, 59)), expectedNextRiseTime, expectedSetTime)
    }

    private fun sunRise_sunSet_areCorrect(moment: Moment, expectedRiseTime: UTC, expectedSetTime: UTC) {

        val sun: Sun = Universe().apply { updateFor(moment, true) }.solarSystem.sun

        assertCalendar("Sunrise", expectedRiseTime, sun.position!!.riseTime!!)
        assertCalendar("Sunset", expectedSetTime, sun.position!!.setTime!!)
    }
}