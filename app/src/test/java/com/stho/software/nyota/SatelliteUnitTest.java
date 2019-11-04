package com.stho.software.nyota;

import com.stho.software.nyota.universe.SatelliteAlgorithms;
import com.stho.software.nyota.universe.Algorithms;
import com.stho.software.nyota.universe.TLE;
import com.stho.software.nyota.utilities.City;
import com.stho.software.nyota.utilities.Location;
import com.stho.software.nyota.utilities.Moment;
import com.stho.software.nyota.utilities.Topocentric;
import com.stho.software.nyota.utilities.UTC;
import com.stho.software.nyota.utilities.Vector;

import org.junit.Test;

import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class SatelliteUnitTest extends AbstractAstronomicUnitTest {

    @Test
    public void TestISS()
    {
        // TLE from NORAD, Jan 18 2017, 19:27
        String elements = "1 25544U 98067A   17018.16759178  .00002139  00000-0  39647-4 0  9999\r\n2 25544  51.6445  66.1799 0007746  95.7219  10.7210 15.54083144 38425";

        // Positions from ESA Website
        TestPosition(elements, 2017, Calendar.JANUARY, 18, 18, 18, 0, -13.46, -138.77, 409, 27595);
        TestPosition(elements, 2017, Calendar.JANUARY, 18, 18, 45, 20, -41.20, -20.12, 423, 27557);
        TestPosition(elements, 2017, Calendar.JANUARY, 18, 19, 15, 11, 42.5, 62.0, 405, 27632);
    }

    private void TestPosition(String elements, int year, int month, int day, int hour, int minute, int second, double latitude, double longitude, double altitude, double speed)
    {
        TLE tle = new TLE();
        tle.deserialize(elements);

        UTC utc = UTC.forUTC(year, month, day, hour, minute, second);

        double julianDay = utc.getJulianDay();

        TestPosition(tle, julianDay, latitude, longitude, altitude, speed);
    }

    private void TestPosition(TLE tle, double julianDay, double latitude, double longitude, double altitude, double speed)
    {
        Vector position = new Vector();
        Vector velocity = new Vector();

        SatelliteAlgorithms.GetSatellite(tle, julianDay, position, velocity);

        Location location = Algorithms.getLocationForECI(position, julianDay);

        assertEquals("Latitude (in °)", latitude, location.getLatitude(), 0.01);
        assertEquals("Longitude (in °)", longitude, location.getLongitude(), 0.01);
        assertEquals("Altitude (in km)", altitude, location.getAltitude(), 0.5);
        assertEquals("Speed (in km/h)", speed, 60 * velocity.getLength(), 0.5);
    }

    @Test
    public void Test_TopocentricCoordinatesFromECI()
    {
        // MIR over Minneapolis

        Moment moment = Moment.forUTC(
                new City("Test", +45, -93, 0, TimeZone.getDefault()),
                UTC.forUTC(1995, Calendar.NOVEMBER, 18, 12, 46, 0));

        Vector mir = new Vector(-4400.594, +1932.870, +4760.712);

        Topocentric relative = Algorithms.getTopocentricFromPosition(moment, mir);

        assertEquals("Azimuth of MIR", 100.36, relative.azimuth, 0.01);
        assertEquals("Elevation of MIR", 81.52, relative.altitude, 0.01);
    }

    @Test
    public void Test_VisibilityRadius()
    {
        double radius = Algorithms.getVisibilityRadius(400, 20);

        assertEquals("Radius", 873, radius, 1);
    }

    @Test
    public void Test_NewPositionFromBearingDistance()
    {
        Location location = Algorithms.calculateNewLocationFromBearingDistance(new Location(60, 25), 30, 1);

        assertEquals("Latitude", 60.007788047871614, location.getLatitude(), 0.0001);
        assertEquals("Longitude", 25.008995333937197, location.getLongitude(), 0.0001);
    }

}




