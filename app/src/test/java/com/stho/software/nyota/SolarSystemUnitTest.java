package com.stho.software.nyota;

import com.stho.software.nyota.universe.Moon;
import com.stho.software.nyota.universe.Sun;
import com.stho.software.nyota.universe.Universe;
import com.stho.software.nyota.utilities.Moment;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class SolarSystemUnitTest extends AbstractAstronomicUnitTest {


    @Test
    public void sun_isCorrect() throws Exception {

        Moment moment = new Moment(
                getCity(60, 15),
                getUTC(1990, Calendar.APRIL, 19, 0, 0));

        assertEquals("DayNumber for 19.04.1990", -3543.0, moment.d, EPS);

        Universe universe = Universe.getInstance();

        Sun sun = universe.solarSystem.sun;

        sun.updateBasics(moment.d);
        sun.updateHeliocentricLatitudeLongitude(moment.d);
        sun.updateGeocentricAscensionDeclination();

        assertEquals("Sun w", 282.7735, sun.w, EPS);
        assertEquals("Sun a", 1.000000, sun.a, EPS);
        assertEquals("Sun e", 0.016713, sun.e, EPS);
        assertEquals("Sun M", 104.0653, sun.M, EPS);
        assertEquals("Sun E", 104.9904, sun.E, EPS);
        assertEquals("Sun ecl", 23.4406, sun.ecl, EPS);
        assertEquals("Sun v", 105.9134, sun.v, EPS);
        assertEquals("Sun r", 1.004323, sun.r, EPS);
        assertEquals("Sun L", 26.8388, sun.L, EPS);
        assertEquals("Sun longitude", 28.6869, sun.longitude, EPS);
        assertEquals("Sun latitude", 0, sun.latitude, EPS);
        assertEquals("Sun x", 0.881048, sun.x, EPS);
        assertEquals("Sun y", 0.482098, sun.y, EPS);
        assertEquals("Sun z", 0.0, sun.z, EPS);
        assertEquals("Sun RA", 26.6580, sun.RA, EPS);
        assertEquals("Sun Decl", 11.0084, sun.Decl, EPS);

        assertEquals("LST", 14.78925, moment.LST, ONE_MINUTE_IN_DAYS);

        sun.updateAzimuthAltitude(moment);

        assertEquals("Sun azimuth", 15.6767, sun.position.azimuth, ONE_MINUTE_IN_DEGREES);
        assertEquals("Sun altitude", -17.9570, sun.position.altitude, ONE_MINUTE_IN_DEGREES);

        Moon moon = universe.solarSystem.moon;
        moon.updateBasics(moment.d);
        moon.updateHeliocentricLatitudeLongitude(moment.d);

        assertEquals("Moon N", 312.7381, moon.N, EPS);
        assertEquals("Moon i", 5.1454, moon.i, EPS);
        assertEquals("Moon w", 95.7454, moon.w, EPS);
        assertEquals("Moon a", 60.2666, moon.a, EPS);
        assertEquals("Moon e", 0.054900, moon.e, EPS);
        assertEquals("Moon M", 266.0954, moon.M, EPS);
        assertEquals("Moon E", 262.9735, moon.E, EPS);
        assertEquals("Moon v", 259.8605, moon.v, EPS);
        assertEquals("Moon r", 60.67134, moon.r, EPS);
        assertEquals("Moon longitude", 308.3616, moon.longitude, EPS);
        assertEquals("Moon latitude", -0.3937, moon.latitude, EPS);

        moon.applyPerturbations(sun);
        moon.updateGeocentricAscensionDeclination(sun);

        assertEquals("Moon longitude (pert)", 306.9484, moon.longitude, EPS);
        assertEquals("Moon latitude (pert)", -0.5856, moon.latitude, EPS);
        assertEquals("Moon r (pert)", 60.6779 , moon.r, EPS);
        assertEquals("Moon RA", 309.5011, moon.RA, EPS);
        assertEquals("Moon Decl", -19.1032, moon.Decl, EPS);

        moon.applyTopocentricCorrection(moment);
        moon.updateAzimuthAltitude(moment);

        assertEquals("Moon RA", 310.0017, moon.RA, EPS);
        assertEquals("Moon Decl", -19.8790, moon.Decl, EPS);
    }

    @Test
    public void planets_areCorrect() throws Exception {

        Moment moment = new Moment(
                getCity(60, 15),
                getUTC(1990, Calendar.APRIL, 19, 0, 0));

        Universe universe = Universe.getInstance();
        universe.updateFor(moment, false);

        assertEquals("Sun RA", 26.6580, universe.solarSystem.sun.RA, EPS);
        assertEquals("Sun Decl", 11.0084, universe.solarSystem.sun.Decl, EPS);

        assertEquals("Sun azimuth", 15.6767, universe.solarSystem.sun.position.azimuth, ONE_MINUTE_IN_DEGREES);
        assertEquals("Sun altitude", -17.9570, universe.solarSystem.sun.position.altitude, ONE_MINUTE_IN_DEGREES);

        assertEquals("Moon RA", 310.0017, universe.solarSystem.moon.RA, EPS);
        assertEquals("Moon Decl", -19.8790, universe.solarSystem.moon.Decl, EPS);

        assertEquals("Mercury N", 48.2163, universe.solarSystem.mercury.N, EPS);
        assertEquals("Mercury i", 7.0045, universe.solarSystem.mercury.i, EPS);
        assertEquals("Mercury w", 29.0882, universe.solarSystem.mercury.w, EPS);
        assertEquals("Mercury a", 0.387098, universe.solarSystem.mercury.a, EPS);
        assertEquals("Mercury e", 0.205633, universe.solarSystem.mercury.e, EPS);
        assertEquals("Mercury M", 69.5153, universe.solarSystem.mercury.M, EPS);

        assertEquals("Venus N", 76.5925, universe.solarSystem.venus.N, EPS);
        assertEquals("Venus i", 3.3945, universe.solarSystem.venus.i, EPS);
        assertEquals("Venus w", 54.8420, universe.solarSystem.venus.w, EPS);
        assertEquals("Venus a", 0.723330, universe.solarSystem.venus.a, EPS);
        assertEquals("Venus e", 0.006778, universe.solarSystem.venus.e, EPS);
        assertEquals("Venus M", 131.6578, universe.solarSystem.venus.M, EPS);

        assertEquals("Mars N", 49.4826, universe.solarSystem.mars.N, EPS);
        assertEquals("Mars i", 1.8498, universe.solarSystem.mars.i, EPS);
        assertEquals("Mars w", 286.3978, universe.solarSystem.mars.w, EPS);
        assertEquals("Mars a", 1.523688, universe.solarSystem.mars.a, EPS);
        assertEquals("Mars e", 0.093396, universe.solarSystem.mars.e, EPS);
        assertEquals("Mars M", 321.9965, universe.solarSystem.mars.M, EPS);

        assertEquals("Jupiter N", 100.3561, universe.solarSystem.jupiter.N, EPS);
        assertEquals("Jupiter i", 1.3036, universe.solarSystem.jupiter.i, EPS);
        assertEquals("Jupiter w", 273.8194, universe.solarSystem.jupiter.w, EPS);
        assertEquals("Jupiter a", 5.20256, universe.solarSystem.jupiter.a, EPS);
        assertEquals("Jupiter e", 0.048482, universe.solarSystem.jupiter.e, EPS);
        assertEquals("Jupiter M", 85.5238, universe.solarSystem.jupiter.M, EPS);

        assertEquals("Saturn N", 113.5787, universe.solarSystem.saturn.N, EPS);
        assertEquals("Saturn i", 2.4890, universe.solarSystem.saturn.i, EPS);
        assertEquals("Saturn w", 339.2884, universe.solarSystem.saturn.w, EPS);
        assertEquals("Saturn a", 9.55475, universe.solarSystem.saturn.a, EPS);
        assertEquals("Saturn e", 0.055580, universe.solarSystem.saturn.e, EPS);
        assertEquals("Saturn M", 198.4741, universe.solarSystem.saturn.M, EPS);

        assertEquals("Uranus N", 73.9510, universe.solarSystem.uranus.N, EPS);
        assertEquals("Uranus i", 0.7732, universe.solarSystem.uranus.i, EPS);
        assertEquals("Uranus w", 96.5529, universe.solarSystem.uranus.w, EPS);
        assertEquals("Uranus a", 19.18176, universe.solarSystem.uranus.a, EPS);
        assertEquals("Uranus e", 0.047292, universe.solarSystem.uranus.e, EPS);
        assertEquals("Uranus M", 101.0460, universe.solarSystem.uranus.M, EPS);

        assertEquals("Neptune N", 131.6737, universe.solarSystem.neptune.N, EPS);
        assertEquals("Neptune i", 1.7709, universe.solarSystem.neptune.i, EPS);
        assertEquals("Neptune w", 272.8675, universe.solarSystem.neptune.w, EPS);
        assertEquals("Neptune a", 30.05814, universe.solarSystem.neptune.a, EPS);
        assertEquals("Neptune e", 0.008598, universe.solarSystem.neptune.e, EPS);
        assertEquals("Neptune M", 239.0063, universe.solarSystem.neptune.M, EPS);

        assertEquals("Mercury longitude", 170.5709, universe.solarSystem.mercury.longitude, EPS);
        assertEquals("Mercury latitude", 5.9255, universe.solarSystem.mercury.latitude, EPS);
        assertEquals("Mercury r", 0.374862, universe.solarSystem.mercury.r, EPS);

        assertEquals("Venus longitude", 263.6570, universe.solarSystem.venus.longitude, EPS);
        assertEquals("Venus latitude", -0.4180, universe.solarSystem.venus.latitude, EPS);
        assertEquals("Venus r", 0.726607, universe.solarSystem.venus.r, EPS);

        assertEquals("Mars longitude", 290.6297, universe.solarSystem.mars.longitude, EPS);
        assertEquals("Mars latitude", -1.6203, universe.solarSystem.mars.latitude, EPS);
        assertEquals("Mars r", 1.417194, universe.solarSystem.mars.r, EPS);

        assertEquals("Jupiter longitude", 105.2423, universe.solarSystem.jupiter.longitude, EPS);
        assertEquals("Jupiter latitude", 0.1113, universe.solarSystem.jupiter.latitude, EPS);
        assertEquals("Jupiter r", 5.19508, universe.solarSystem.jupiter.r, EPS);

        assertEquals("Saturn longitude", 289.3824, universe.solarSystem.saturn.longitude, EPS);
        assertEquals("Saturn latitude", 0.1845, universe.solarSystem.saturn.latitude, EPS);
        assertEquals("Saturn r", 10.06118, universe.solarSystem.saturn.r, EPS);

        assertEquals("Uranus longitude", 276.7672, universe.solarSystem.uranus.longitude, EPS);
        assertEquals("Uranus latitude", -0.3003, universe.solarSystem.uranus.latitude, EPS);
        assertEquals("Uranus r", 19.39628, universe.solarSystem.uranus.r, EPS);

        assertEquals("Neptune longitude", 282.7192, universe.solarSystem.neptune.longitude, 2*EPS);
        assertEquals("Neptune latitude", 0.8575, universe.solarSystem.neptune.latitude, EPS);
        assertEquals("Neptune r", 30.19284, universe.solarSystem.neptune.r, EPS);

        assertEquals("Mercury RA", 43.2598, universe.solarSystem.mercury.RA, EPS);
        assertEquals("Mercury Decl", 19.6460, universe.solarSystem.mercury.Decl, EPS);
        assertEquals("Mercury R", 0.748296, universe.solarSystem.mercury.R, EPS);
    }

    @Test
    public void SunRise_isCorrect() throws Exception {

        // Berlin-Buch
        Moment moment = Moment.forUTC(
                getCity(52.50, 13.42),
                getCESTasUTC(2016, Calendar.SEPTEMBER, 12, 8, 35));

        Universe universe = Universe.getInstance();
        universe.updateFor(moment, true);

        // compare with https://sunrisesunsetmap.com/
        // 52°38'N 013°29'E, Monday 12 September 2016, Rise: 06:36 CEST, Set: 19:28 CEST
        assertCalendar("Sunrise", getCESTasUTC(2016, Calendar.SEPTEMBER, 12, 6, 36), universe.solarSystem.sun.position.riseTime);
        assertCalendar("Sunset", getCESTasUTC(2016, Calendar.SEPTEMBER, 12, 19, 28), universe.solarSystem.sun.position.setTime);
    }
}





