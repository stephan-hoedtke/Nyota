package com.stho.software.nyota;

import com.stho.software.nyota.universe.Moon;
import com.stho.software.nyota.universe.Sun;
import com.stho.software.nyota.universe.Universe;
import com.stho.software.nyota.universe.Algorithms;
import com.stho.software.nyota.utilities.Degree;
import com.stho.software.nyota.utilities.Moment;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class MoonUnitTest extends AbstractAstronomicUnitTest {

    @Test
    public void MoonRiseForBernau_isCorrect() throws Exception {

        // https://www.timeanddate.de/mond/deutschland/bernau-bei-berlin
        // Bernau bei Berlin
        // 52°41'N / 13°35'E
        Moment moment = Moment.forUTC(
                getCity(Degree.fromPositive(52, 41, 0).angleInDegree,
                        Degree.fromPositive(13, 35, 0).angleInDegree),
                getCESTasUTC(2016, Calendar.SEPTEMBER, 13, 7, 30));

        Universe universe = Universe.getInstance();
        universe.updateFor(moment, true);

        Sun sun = universe.solarSystem.sun;
        Moon moon = universe.solarSystem.moon;

        assertPosition("Sun azimuth", Degree.fromPositive(7, 0, 0).angleInDegree, sun.position.altitude);
        assertPosition("Sun altitude", Degree.fromPositive(94, 0, 0).angleInDegree, sun.position.azimuth);
        assertCalendar("Sunrise", getCESTasUTC(2016, Calendar.SEPTEMBER, 13, 6, 37), sun.position.riseTime);
        assertCalendar("Sunset", getCESTasUTC(2016, Calendar.SEPTEMBER, 13, 19, 25), sun.position.setTime);

        assertCalendar("Moon rise", getCESTasUTC(2016, Calendar.SEPTEMBER, 13, 17, 44), moon.position.riseTime);
        assertCalendar("Moon set", getCESTasUTC(2016, Calendar.SEPTEMBER, 14, 3, 23), moon.position.setTime);
    }


    @Test
    public void MoonRiseAndSet_isCorrect() throws Exception {

        // http://www.mondverlauf.de/#/52.6451,13.4918,18/2016.09.14/16:57/1
        // Berlin, Pölnitzweg
        // N 52°38'42.28''
        // E 13°29'30.44''
        Moment moment = Moment.forUTC(
                getCity(Degree.fromPositive(52, 38, 35.02).angleInDegree,
                        Degree.fromPositive(13, 29, 43.19).angleInDegree),
                getCESTasUTC(2016, Calendar.SEPTEMBER, 13, 7, 30));

        Universe universe = Universe.getInstance();
        universe.updateFor(moment, true);

        Sun sun = universe.solarSystem.sun;
        Moon moon = universe.solarSystem.moon;

        assertCalendar("Moon rise", getCESTasUTC(2016, Calendar.SEPTEMBER, 13, 17, 45), moon.position.riseTime);
        assertCalendar("Moon set", getCESTasUTC(2016, Calendar.SEPTEMBER, 14, 3, 25), moon.position.setTime);
    }


    @Test
    public void MoonPhaseAngle_isCorrect() throws Exception {
        // Meeus, p. 347
        Moment moment = Moment.forUTC(
                getCity(Degree.fromPositive(52, 38, 35.02).angleInDegree,
                        Degree.fromPositive(13, 29, 43.19).angleInDegree),
                getUTC(1992, Calendar.APRIL, 12, 0, 0));



        Universe universe = Universe.getInstance();
        universe.updateFor(moment, true);

        Sun sun = universe.solarSystem.sun;
        Moon moon = universe.solarSystem.moon;

        assertEquals("Moon RA", 134.6885, moon.RA, ONE_DEGREE);
        assertEquals("Moon Decl", 13.7684, moon.Decl, ONE_DEGREE);
        assertEquals("Moon Distance km", 368410, Algorithms.EARTH_RADIUS * moon.R, 1000);


        assertEquals("Sun RA", 20.6579, sun.RA, ONE_DEGREE);
        assertEquals("Sun Decl", 8.6964, sun.Decl, ONE_DEGREE);
        assertEquals("Sun Distance in AU", 1.0024977, sun.R, EPS);
        assertEquals("Sun Distance in km", 149971520, Algorithms.ASTRONOMIC_UNIT * sun.R, 1000);

        assertEquals("Moon phase", 0.6786, moon.phase, ONE_MINUTE_IN_DEGREES);
        assertEquals("Moon phase angle", 285.0, Degree.normalize(moon.phaseAngle), ONE_DEGREE);
    }
}





