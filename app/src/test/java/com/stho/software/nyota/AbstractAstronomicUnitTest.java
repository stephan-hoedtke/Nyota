package com.stho.software.nyota;

import com.stho.software.nyota.utilities.City;
import com.stho.software.nyota.utilities.Location;
import com.stho.software.nyota.utilities.UTC;

import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

/**
 * Created by shoedtke on 24.09.2016.
 */
public abstract class AbstractAstronomicUnitTest {
    static final double EPS = 0.0001;
    static final double ONE_DEGREE = 1;
    static final double ONE_MINUTE = 1;
    static final double ONE_MINUTE_IN_DEGREES = 0.01;
    static final double ONE_MINUTE_IN_DAYS = 0.0005;

    private static UTC getUTC(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.set(year, month, day, 0, 0, 0);
        return new UTC(calendar.getTimeInMillis());
    }

    protected static UTC getUTC(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.set(year, month, day, hour, minute, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new UTC(calendar.getTimeInMillis());
    }

    protected static UTC getCESTasUTC(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("CET"));
        calendar.set(year, month, day, hour, minute, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long millis = calendar.getTimeInMillis();
        return new UTC(millis);
    }

    protected static Location getObserver(double latitude, double longitude) {
        return new Location(latitude, longitude);
    }

    protected static City getCity(double latitude, double longitude) {
        return new City("Test", latitude, longitude, 0, TimeZone.getDefault());
    }

    protected static City getCity(double latitude, double longitude, double altitude, String timeZone) {
        return new City("Test", latitude, longitude, altitude, TimeZone.getTimeZone(timeZone));
    }


    protected static City getCity(Location location) {
        return new City("Test", location, TimeZone.getDefault());
    }

    protected void assertCalendar(String text, UTC expected, UTC actual) throws Exception {
        assertEquals(text, expected.getMinutes(), actual.getMinutes(), ONE_MINUTE);
    }

    protected void assertPosition(String text, double expected, double actual) throws Exception {
        assertEquals(text, expected, actual, ONE_DEGREE);
    }
}
