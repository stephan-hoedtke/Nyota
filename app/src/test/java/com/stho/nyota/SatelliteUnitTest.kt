package com.stho.nyota


import com.stho.nyota.sky.universe.Algorithms
import com.stho.nyota.sky.universe.SatelliteAlgorithms
import com.stho.nyota.sky.universe.TLE
import com.stho.nyota.sky.utilities.*
import org.junit.Assert
import org.junit.Test
import java.time.ZoneId
import java.util.*
import java.util.Vector

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
class SatelliteUnitTest : AbstractAstronomicUnitTest() {

    @Test
    fun iss_position_isCorrect() {
        // TLE from NORAD, Jan 18 2017, 19:27
        val elements = "1 25544U 98067A   17018.16759178  .00002139  00000-0  39647-4 0  9999\r\n2 25544  51.6445  66.1799 0007746  95.7219  10.7210 15.54083144 38425"

        // Positions from ESA Website
        satellitePosition_isCorrect(elements, 2017, Calendar.JANUARY, 18, 18, 18, 0, -13.46, -138.77, 409.0, 27595.0)
        satellitePosition_isCorrect(elements, 2017, Calendar.JANUARY, 18, 18, 45, 20, -41.20, -20.12, 423.0, 27557.0)
        satellitePosition_isCorrect(elements, 2017, Calendar.JANUARY, 18, 19, 15, 11, 42.5, 62.0, 405.0, 27632.0)
    }

    private fun satellitePosition_isCorrect(elements: String, year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int, latitude: Double, longitude: Double, altitude: Double, speed: Double) {
        val tle = TLE.deserialize(elements)
        val utc: UTC = UTC.forUTC(year, month, day, hour, minute, second)
        val julianDay: Double = utc.julianDay
        satellitePosition_isCorrect(tle, julianDay, latitude, longitude, altitude, speed)
    }

    private fun satellitePosition_isCorrect(tle: TLE, julianDay: Double, latitude: Double, longitude: Double, altitude: Double, speed: Double) {
        val position = com.stho.nyota.sky.utilities.Vector()
        val velocity = com.stho.nyota.sky.utilities.Vector()

        SatelliteAlgorithms.calculatePositionVelocity(tle, julianDay, position, velocity)

        val location: Location = Algorithms.getLocationForECI(position, julianDay)

        Assert.assertEquals("Latitude (in °)", latitude, location.latitude, 0.01)
        Assert.assertEquals("Longitude (in °)", longitude, location.longitude, 0.01)
        Assert.assertEquals("Altitude (in km)", altitude, location.altitude, 0.5)
        Assert.assertEquals("Speed (in km/h)", speed, 60 * velocity.length, 0.5)
    }

    @Test
    fun topocentricCoordinatesFromECI_isCorrect() {
        // MIR over Minneapolis
        val moment: Moment = Moment.forUTC(
            City.createNewCity("Minneapolis", Location(+45.0, -93.0, 0.0), TimeZone.getTimeZone("America/Minasota")),
            UTC.forUTC(1995, Calendar.NOVEMBER, 18, 12, 46, 0)
        )
        val mir = com.stho.nyota.sky.utilities.Vector(-4400.594, +1932.870, +4760.712)
        val relative: Topocentric = Algorithms.getTopocentricFromPosition(moment, mir)

        Assert.assertEquals("Azimuth of MIR", 100.36, relative.azimuth, 0.01)
        Assert.assertEquals("Elevation of MIR", 81.52, relative.altitude, 0.01)
    }

    @Test
    fun visibilityRadius_isCorrect() {
        val radius: Double = Algorithms.getVisibilityRadius(400.0, 20.0)

        Assert.assertEquals("Radius", 873.0, radius, 1.0)
    }

    @Test
    fun newPositionFromBearingDistance_isCorrect() {
        val location: Location = Algorithms.calculateNewLocationFromBearingDistance(Location(60.0, 25.0), 30.0, 1.0)

        Assert.assertEquals("Latitude", 60.007788047871614, location.latitude, 0.0001)
        Assert.assertEquals("Longitude", 25.008995333937197, location.longitude, 0.0001)
    }
}