package com.stho.nyota

import com.stho.nyota.sky.universe.Moon
import com.stho.nyota.sky.universe.Sun
import com.stho.nyota.sky.universe.Universe
import com.stho.nyota.sky.utilities.Moment
import org.junit.Assert
import org.junit.Test
import java.util.*


/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
class SolarSystemUnitTest : AbstractAstronomicUnitTest() {
    
    @Test
    fun sun_isCorrect() {
        val moment = Moment(
            getCity(60.0, 15.0),
            getUTC(1990, Calendar.APRIL, 19, 0, 0)
        )
        
        Assert.assertEquals("DayNumber for 19.04.1990", -3543.0, moment.d, EPS)

        val universe: Universe = Universe()
        val sun: Sun = universe.solarSystem.sun.apply {
            updateBasics(moment.d)
            updateHeliocentricLatitudeLongitude(moment.d)
            updateGeocentricAscensionDeclination()
        }

        Assert.assertEquals("Sun w", 282.7735, sun.w, EPS)
        Assert.assertEquals("Sun a", 1.000000, sun.a, EPS)
        Assert.assertEquals("Sun e", 0.016713, sun.e, EPS)
        Assert.assertEquals("Sun M", 104.0653, sun.M, EPS)
        Assert.assertEquals("Sun E", 104.9904, sun.EA, EPS)
        Assert.assertEquals("Sun ecl", 23.4406, sun.ecl, EPS)
        Assert.assertEquals("Sun v", 105.9134, sun.v, EPS)
        Assert.assertEquals("Sun r", 1.004323, sun.mr, EPS) // TODO: check if sun.R or sun.mr shall be used.
        Assert.assertEquals("Sun L", 26.8388, sun.L, EPS)
        Assert.assertEquals("Sun longitude", 28.6869, sun.longitude, EPS)
        Assert.assertEquals("Sun latitude", 0.0, sun.latitude, EPS)
        Assert.assertEquals("Sun x", 0.881048, sun.x, EPS)
        Assert.assertEquals("Sun y", 0.482098, sun.y, EPS)
        Assert.assertEquals("Sun z", 0.0, sun.z, EPS)
        Assert.assertEquals("Sun RA", 26.6580, sun.RA, EPS)
        Assert.assertEquals("Sun Decl", 11.0084, sun.Decl, EPS)
        Assert.assertEquals("LST", 14.78925, moment.lst, ONE_MINUTE_IN_DAYS)

        sun.apply {
            updateAzimuthAltitude(moment)
        }
        
        Assert.assertEquals("Sun azimuth", 15.6767, sun.position!!.azimuth, ONE_MINUTE_IN_DEGREES)
        Assert.assertEquals("Sun altitude", -17.9570, sun.position!!.altitude, ONE_MINUTE_IN_DEGREES)

        val moon: Moon = universe.solarSystem.moon.apply {
            updateBasics(moment.d)
            updateHeliocentricLatitudeLongitude(moment.d)
        }

        Assert.assertEquals("Moon N", 312.7381, moon.N, EPS)
        Assert.assertEquals("Moon i", 5.1454, moon.i, EPS)
        Assert.assertEquals("Moon w", 95.7454, moon.w, EPS)
        Assert.assertEquals("Moon a", 60.2666, moon.a, EPS)
        Assert.assertEquals("Moon e", 0.054900, moon.e, EPS)
        Assert.assertEquals("Moon M", 266.0954, moon.M, EPS)
        Assert.assertEquals("Moon E", 262.9735, moon.EA, EPS)
        Assert.assertEquals("Moon v", 259.8605, moon.v, EPS)
        Assert.assertEquals("Moon r", 60.67134, moon.mr, EPS) // TODO use mr or R
        Assert.assertEquals("Moon longitude", 308.3616, moon.longitude, EPS)
        Assert.assertEquals("Moon latitude", -0.3937, moon.latitude, EPS)

        moon.apply {
            applyPerturbations(sun)
            updateGeocentricAscensionDeclination(sun)
        }

        Assert.assertEquals("Moon longitude (pert)", 306.9484, moon.longitude, EPS)
        Assert.assertEquals("Moon latitude (pert)", -0.5856, moon.latitude, EPS)
        Assert.assertEquals("Moon r (pert)", 60.6779, moon.mr, EPS)
        Assert.assertEquals("Moon RA", 309.5011, moon.RA, EPS)
        Assert.assertEquals("Moon Decl", -19.1032, moon.Decl, EPS)

        moon.apply {
            applyTopocentricCorrection(moment)
            updateAzimuthAltitude(moment)
        }

        Assert.assertEquals("Moon RA", 310.0017, moon.RA, EPS)
        Assert.assertEquals("Moon Decl", -19.8790, moon.Decl, EPS)
    }

    @Test
    fun planets_areCorrect() {
        val moment = Moment(
            getCity(60.0, 15.0),
            getUTC(1990, Calendar.APRIL, 19, 0, 0)
        )
        val universe: Universe = Universe().apply {
            updateFor(moment, false)
        }
        
        Assert.assertEquals("Sun RA", 26.6580, universe.solarSystem.sun.RA, EPS)
        Assert.assertEquals("Sun Decl", 11.0084, universe.solarSystem.sun.Decl, EPS)
        Assert.assertEquals("Sun azimuth", 15.6767, universe.solarSystem.sun.position!!.azimuth, ONE_MINUTE_IN_DEGREES)
        Assert.assertEquals("Sun altitude", -17.9570, universe.solarSystem.sun.position!!.altitude, ONE_MINUTE_IN_DEGREES)
        Assert.assertEquals("Moon RA", 310.0017, universe.solarSystem.moon.RA, EPS)
        Assert.assertEquals("Moon Decl", -19.8790, universe.solarSystem.moon.Decl, EPS)
        Assert.assertEquals("Mercury N", 48.2163, universe.solarSystem.mercury.N, EPS)
        Assert.assertEquals("Mercury i", 7.0045, universe.solarSystem.mercury.i, EPS)
        Assert.assertEquals("Mercury w", 29.0882, universe.solarSystem.mercury.w, EPS)
        Assert.assertEquals("Mercury a", 0.387098, universe.solarSystem.mercury.a, EPS)
        Assert.assertEquals("Mercury e", 0.205633, universe.solarSystem.mercury.e, EPS)
        Assert.assertEquals("Mercury M", 69.5153, universe.solarSystem.mercury.M, EPS)
        Assert.assertEquals("Venus N", 76.5925, universe.solarSystem.venus.N, EPS)
        Assert.assertEquals("Venus i", 3.3945, universe.solarSystem.venus.i, EPS)
        Assert.assertEquals("Venus w", 54.8420, universe.solarSystem.venus.w, EPS)
        Assert.assertEquals("Venus a", 0.723330, universe.solarSystem.venus.a, EPS)
        Assert.assertEquals("Venus e", 0.006778, universe.solarSystem.venus.e, EPS)
        Assert.assertEquals("Venus M", 131.6578, universe.solarSystem.venus.M, EPS)
        Assert.assertEquals("Mars N", 49.4826, universe.solarSystem.mars.N, EPS)
        Assert.assertEquals("Mars i", 1.8498, universe.solarSystem.mars.i, EPS)
        Assert.assertEquals("Mars w", 286.3978, universe.solarSystem.mars.w, EPS)
        Assert.assertEquals("Mars a", 1.523688, universe.solarSystem.mars.a, EPS)
        Assert.assertEquals("Mars e", 0.093396, universe.solarSystem.mars.e, EPS)
        Assert.assertEquals("Mars M", 321.9965, universe.solarSystem.mars.M, EPS)
        Assert.assertEquals("Jupiter N", 100.3561, universe.solarSystem.jupiter.N, EPS)
        Assert.assertEquals("Jupiter i", 1.3036, universe.solarSystem.jupiter.i, EPS)
        Assert.assertEquals("Jupiter w", 273.8194, universe.solarSystem.jupiter.w, EPS)
        Assert.assertEquals("Jupiter a", 5.20256, universe.solarSystem.jupiter.a, EPS)
        Assert.assertEquals("Jupiter e", 0.048482, universe.solarSystem.jupiter.e, EPS)
        Assert.assertEquals("Jupiter M", 85.5238, universe.solarSystem.jupiter.M, EPS)
        Assert.assertEquals("Saturn N", 113.5787, universe.solarSystem.saturn.N, EPS)
        Assert.assertEquals("Saturn i", 2.4890, universe.solarSystem.saturn.i, EPS)
        Assert.assertEquals("Saturn w", 339.2884, universe.solarSystem.saturn.w, EPS)
        Assert.assertEquals("Saturn a", 9.55475, universe.solarSystem.saturn.a, EPS)
        Assert.assertEquals("Saturn e", 0.055580, universe.solarSystem.saturn.e, EPS)
        Assert.assertEquals("Saturn M", 198.4741, universe.solarSystem.saturn.M, EPS)
        Assert.assertEquals("Uranus N", 73.9510, universe.solarSystem.uranus.N, EPS)
        Assert.assertEquals("Uranus i", 0.7732, universe.solarSystem.uranus.i, EPS)
        Assert.assertEquals("Uranus w", 96.5529, universe.solarSystem.uranus.w, EPS)
        Assert.assertEquals("Uranus a", 19.18176, universe.solarSystem.uranus.a, EPS)
        Assert.assertEquals("Uranus e", 0.047292, universe.solarSystem.uranus.e, EPS)
        Assert.assertEquals("Uranus M", 101.0460, universe.solarSystem.uranus.M, EPS)
        Assert.assertEquals("Neptune N", 131.6737, universe.solarSystem.neptune.N, EPS)
        Assert.assertEquals("Neptune i", 1.7709, universe.solarSystem.neptune.i, EPS)
        Assert.assertEquals("Neptune w", 272.8675, universe.solarSystem.neptune.w, EPS)
        Assert.assertEquals("Neptune a", 30.05814, universe.solarSystem.neptune.a, EPS)
        Assert.assertEquals("Neptune e", 0.008598, universe.solarSystem.neptune.e, EPS)
        Assert.assertEquals("Neptune M", 239.0063, universe.solarSystem.neptune.M, EPS)
        Assert.assertEquals("Mercury longitude", 170.5709, universe.solarSystem.mercury.longitude, EPS)
        Assert.assertEquals("Mercury latitude", 5.9255, universe.solarSystem.mercury.latitude, EPS)
        Assert.assertEquals("Mercury r", 0.374862, universe.solarSystem.mercury.mr, EPS)
        Assert.assertEquals("Venus longitude", 263.6570, universe.solarSystem.venus.longitude, EPS)
        Assert.assertEquals("Venus latitude", -0.4180, universe.solarSystem.venus.latitude, EPS)
        Assert.assertEquals("Venus r", 0.726607, universe.solarSystem.venus.mr, EPS)
        Assert.assertEquals("Mars longitude", 290.6297, universe.solarSystem.mars.longitude, EPS)
        Assert.assertEquals("Mars latitude", -1.6203, universe.solarSystem.mars.latitude, EPS)
        Assert.assertEquals("Mars r", 1.417194, universe.solarSystem.mars.mr, EPS)
        Assert.assertEquals("Jupiter longitude", 105.2423, universe.solarSystem.jupiter.longitude, EPS)
        Assert.assertEquals("Jupiter latitude", 0.1113, universe.solarSystem.jupiter.latitude, EPS)
        Assert.assertEquals("Jupiter r", 5.19508, universe.solarSystem.jupiter.mr, EPS)
        Assert.assertEquals("Saturn longitude", 289.3824, universe.solarSystem.saturn.longitude, EPS)
        Assert.assertEquals("Saturn latitude", 0.1845, universe.solarSystem.saturn.latitude, EPS)
        Assert.assertEquals("Saturn r", 10.06118, universe.solarSystem.saturn.mr, EPS)
        Assert.assertEquals("Uranus longitude", 276.7672, universe.solarSystem.uranus.longitude, EPS)
        Assert.assertEquals("Uranus latitude", -0.3003, universe.solarSystem.uranus.latitude, EPS)
        Assert.assertEquals("Uranus r", 19.39628, universe.solarSystem.uranus.mr, EPS)
        Assert.assertEquals("Neptune longitude", 282.7192, universe.solarSystem.neptune.longitude, 2 * EPS)
        Assert.assertEquals("Neptune latitude", 0.8575, universe.solarSystem.neptune.latitude, EPS)
        Assert.assertEquals("Neptune r", 30.19284, universe.solarSystem.neptune.mr, EPS)
        Assert.assertEquals("Mercury RA", 43.2598, universe.solarSystem.mercury.RA, EPS)
        Assert.assertEquals("Mercury Decl", 19.6460, universe.solarSystem.mercury.Decl, EPS)
        Assert.assertEquals("Mercury R", 0.748296, universe.solarSystem.mercury.R, EPS)
    }

    @Test
    fun sunRise_isCorrect() {

        // Berlin-Buch
        val moment: Moment = Moment.forUTC(
            getCity(52.50, 13.42),
            getCESTasUTC(2016, Calendar.SEPTEMBER, 12, 8, 35)
        )
        val universe: Universe = Universe().apply {
            updateFor(moment, true)
        }

        // compare with https://sunrisesunsetmap.com/
        // 52°38'N 013°29'E, Monday 12 September 2016, Rise: 06:36 CEST, Set: 19:28 CEST
        assertCalendar("Sunrise", getCESTasUTC(2016, Calendar.SEPTEMBER, 12, 6, 36), universe.solarSystem.sun.position!!.riseTime!!)
        assertCalendar("Sunset", getCESTasUTC(2016, Calendar.SEPTEMBER, 12, 19, 28), universe.solarSystem.sun.position!!.setTime!!)
    }
}