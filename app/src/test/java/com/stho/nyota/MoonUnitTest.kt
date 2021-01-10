package com.stho.nyota

import com.stho.nyota.sky.universe.Algorithms
import com.stho.nyota.sky.universe.Moon
import com.stho.nyota.sky.universe.Sun
import com.stho.nyota.sky.universe.Universe
import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.Location
import com.stho.nyota.sky.utilities.Moment
import org.junit.Assert
import org.junit.Test
import java.util.*

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
class MoonUnitTest : AbstractAstronomicUnitTest() {

    @Test
    fun moonRiseForBernau_isCorrect() {

        // https://www.timeanddate.de/mond/deutschland/bernau-bei-berlin
        // Bernau bei Berlin
        // 52°41'N / 13°35'E
        val moment: Moment = Moment.forUTC(
            getCity(
                Degree.fromPositive(52, 41, 0.0).angleInDegree,
                Degree.fromPositive(13, 35, 0.0).angleInDegree
            ),
            getCESTasUTC(2016, Calendar.SEPTEMBER, 13, 7, 30)
        )
        val universe: Universe = Universe().apply { updateFor(moment, true) }
        val sun: Sun = universe.solarSystem.sun
        val moon: Moon = universe.solarSystem.moon

        assertPosition("Sun azimuth", Degree.fromPositive(7, 0, 0.0).angleInDegree, sun.position!!.altitude)
        assertPosition("Sun altitude", Degree.fromPositive(94, 0, 0.0).angleInDegree, sun.position!!.azimuth)
        assertCalendar("Sunrise", getCESTasUTC(2016, Calendar.SEPTEMBER, 13, 6, 37), sun.position!!.riseTime!!)
        assertCalendar("Sunset", getCESTasUTC(2016, Calendar.SEPTEMBER, 13, 19, 25), sun.position!!.setTime!!)
        assertCalendar("Moon rise", getCESTasUTC(2016, Calendar.SEPTEMBER, 12, 17, 6), moon.position!!.prevRiseTime!!)
        assertCalendar("Moon set", getCESTasUTC(2016, Calendar.SEPTEMBER, 13, 2, 17), moon.position!!.setTime!!)
        assertCalendar("Moon rise", getCESTasUTC(2016, Calendar.SEPTEMBER, 13, 17, 44), moon.position!!.riseTime!!)
        assertCalendar("Moon set", getCESTasUTC(2016, Calendar.SEPTEMBER, 14, 3, 23), moon.position!!.nextSetTime!!)
    }

    @Test
    fun moonRiseAndSet_isCorrect() {

        // http://www.mondverlauf.de/#/52.6451,13.4918,18/2016.09.14/16:57/1
        // Berlin, Pölnitzweg
        // N 52°38'42.28''
        // E 13°29'30.44''
        val moment: Moment = Moment.forUTC(
            getCity(
                Degree.fromPositive(52, 38, 35.02).angleInDegree,
                Degree.fromPositive(13, 29, 43.19).angleInDegree
            ),
            getCESTasUTC(2016, Calendar.SEPTEMBER, 13, 7, 30)
        )
        val universe: Universe = Universe().apply { updateFor(moment, true) }
        val moon: Moon = universe.solarSystem.moon

        assertCalendar("Moon rise", getCESTasUTC(2016, Calendar.SEPTEMBER, 13, 17, 45), moon.position!!.riseTime!!)
        assertCalendar("Moon set", getCESTasUTC(2016, Calendar.SEPTEMBER, 14, 3, 25), moon.position!!.nextSetTime!!)
    }

    @Test
    fun moonPhaseAngle_isCorrect() {
        // Meeus, p. 347
        val moment: Moment = Moment.forUTC(
            getCity(
                Degree.fromPositive(52, 38, 35.02).angleInDegree,
                Degree.fromPositive(13, 29, 43.19).angleInDegree
            ),
            getUTC(1992, Calendar.APRIL, 12, 0, 0)
        )
        val universe: Universe = Universe().apply { updateFor(moment, true) }
        val sun: Sun = universe.solarSystem.sun
        val moon: Moon = universe.solarSystem.moon

        Assert.assertEquals("Moon RA", 134.6885, moon.RA, ONE_DEGREE)
        Assert.assertEquals("Moon Decl", 13.7684, moon.Decl, ONE_DEGREE)
        Assert.assertEquals("Moon Distance km", 368410.0, Algorithms.EARTH_RADIUS * moon.R, 1000.0)
        Assert.assertEquals("Sun RA", 20.6579, sun.RA, ONE_DEGREE)
        Assert.assertEquals("Sun Decl", 8.6964, sun.Decl, ONE_DEGREE)
        Assert.assertEquals("Sun Distance in AU", 1.0024977, sun.R, EPS)
        Assert.assertEquals("Sun Distance in km", 149971520.0, Algorithms.ASTRONOMIC_UNIT * sun.R, 1000.0)
        Assert.assertEquals("Moon phase", 0.6786, moon.phase, ONE_MINUTE_IN_DEGREES)
        Assert.assertEquals("Moon phase angle", 285.0, Degree.normalize(moon.phaseAngle), ONE_DEGREE)
    }
}