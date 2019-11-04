package com.stho.software.nyota;

import com.stho.software.nyota.universe.Algorithms;
import com.stho.software.nyota.utilities.Angle;
import com.stho.software.nyota.utilities.Degree;
import com.stho.software.nyota.utilities.Hour;
import com.stho.software.nyota.utilities.JulianDay;
import com.stho.software.nyota.utilities.Radian;
import com.stho.software.nyota.utilities.UTC;

import org.junit.Test;

import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class BasicUnitTest {

    static final double EPS = 0.0001;
    static final double ONE_MINUTE_IN_DAYS = 0.0005;
    static final long JANUARY_FIRST_2000_IN_MILLIS = 946684800000L;

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void truncate_isCorrect() throws Exception {
        assertEquals("2.7", 2, Algorithms.truncate(2.7), EPS);
        assertEquals("0.7", 0, Algorithms.truncate(0.7), EPS);
        assertEquals("0", 0, Algorithms.truncate(0), EPS);
        assertEquals("-0.7", 0, Algorithms.truncate(-0.7), EPS);
        assertEquals("-2.7", -2, Algorithms.truncate(-2.7), EPS);
    }

    @Test
    public void decimals_isCorrect() throws Exception {
        assertEquals("2.7", 0.7, Algorithms.getDecimals(2.7), EPS);
        assertEquals("0.7", 0.7, Algorithms.getDecimals(0.7), EPS);
        assertEquals("0", 0, Algorithms.getDecimals(0), EPS);
        assertEquals("-0.7", -0.7, Algorithms.getDecimals(-0.7), EPS);
        assertEquals("-2.7", -0.7, Algorithms.getDecimals(-2.7), EPS);
    }

    @Test
    public void angle_isCorrect() throws Exception {
        assertEquals("+113.7", 113.7, Angle.normalize(+113.7), EPS);
        assertEquals("+830.7", 113.7, Angle.normalize(+833.7), EPS);
        assertEquals("-880.7", 246.3, Angle.normalize(-833.7), EPS);

        assertEquals("+113.7", 113.7f, Angle.normalize(+113.7f), EPS);
        assertEquals("+830.7", 113.7f, Angle.normalize(+833.7f), EPS);
        assertEquals("-880.7", 246.3f, Angle.normalize(-833.7f), EPS);
    }

    @Test
    public void radian_isCorrect() throws Exception {
        final double PI = Math.PI;
        final double PI2 = Math.PI / 2;
        final double PI32 = Math.PI * 3 / 2;

        assertEquals("+180", PI, Radian.fromDegrees(180), EPS);
        assertEquals("+90", -PI2, Radian.fromDegrees(-90), EPS);
        assertEquals("-90", -PI32, Radian.fromDegrees(-270), EPS);

        assertEquals("+180", PI, Radian.normalize(PI), EPS);
        assertEquals("+90", PI2, Radian.normalize(PI2), EPS);
        assertEquals("-90", PI32, Radian.normalize(-PI2), EPS);
        assertEquals("-270", PI2, Radian.normalize(-PI32), EPS);
    }


    @Test
    public void calendarUsage() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1965, Calendar.SEPTEMBER, 30);

        int year = calendar.get(Calendar.YEAR);
        int zero_based_month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        assertEquals(year, 1965);
        assertEquals(zero_based_month, 8);
        assertEquals(zero_based_month, Calendar.SEPTEMBER);
        assertEquals(day, 30);
    }

    @Test
    public void calendar_timeInMillis() throws Exception {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.set(2000, Calendar.JANUARY, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        assertEquals("Jan 1st 2000 in millis", JANUARY_FIRST_2000_IN_MILLIS, calendar.getTimeInMillis());
    }

    @Test
    public void julianDay_isCorrect() throws Exception {
        julianDay_isCorrect(1582, Calendar.OCTOBER, 15, 0, 0, 0, 2299160.5000);
        julianDay_isCorrect(2016, Calendar.AUGUST, 30, 8, 38, 41, 2457630.8602);
        julianDay_isCorrect(1900, Calendar.JANUARY, 1, 0, 0, 0, 2415020.5000);
        julianDay_isCorrect(1990, Calendar.JANUARY, 1, 0, 0, 0, 2447892.5000);
        julianDay_isCorrect(1990, Calendar.JANUARY, 1, 12, 0, 0, 2447893.0000);
        julianDay_isCorrect(1999, Calendar.DECEMBER, 31, 0, 0, 0, 2451543.5000);
        julianDay_isCorrect(2000, Calendar.JANUARY, 1, 0, 0, 0, 2451544.5000);
        julianDay_isCorrect(2000, Calendar.JANUARY, 1, 12, 0, 0, 2451545.0000);
        julianDay_isCorrect(2006, Calendar.JANUARY, 14, 16, 30, 0, 2453750.1875);
        julianDay_isCorrect(2010, Calendar.MARCH, 25, 16, 30, 0, 2455281.1875);
        julianDay_isCorrect(2143, Calendar.JULY, 15, 12, 0, 0, 2503970.0000);
        julianDay_isCorrect(1987, Calendar.APRIL, 10, 0, 0, 0, 2446895.5000);
        julianDay_isCorrect(1987, Calendar.APRIL, 10, 19, 21, 0, 2446896.30625);
        julianDay_isCorrect(1957, Calendar.OCTOBER, 4, 19, 26, 24, 2436116.31);
        julianDay_isCorrect(333, Calendar.JANUARY, 27, 12, 0, 0, 1842713.0);
        julianDay_isCorrect(1990, Calendar.APRIL, 19, 0, 0, 0, 2448000.5000);
    }

    private void julianDay_isCorrect(int year, int month, int day, int hour, int minute, int second, double expected) {
        Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        utc.set(year, month, day, hour, minute, second);
        utc.set(Calendar.MILLISECOND, 0);
        assertEquals("JD for " + utc.toString(), expected, JulianDay.fromGMT(utc), EPS);
    }

    @Test
    public void yearsSince2000_isCorrect() {
        UTC utc = UTC.forUTC(2016, Calendar.SEPTEMBER, 25, 17, 31, 10);
        assertEquals("years since 2000", 16.7357, utc.getYearsSince2000(), EPS);
    }

    @Test
    public void UTC_calculations_isCorrect() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.set(Calendar.MILLISECOND, 0);
        double JD = JulianDay.fromGMT(calendar);
        long millis = JulianDay.toTimeInMillis(JD);
        assertEquals("UTC --> JD --> millis", millis, calendar.getTimeInMillis(), 1);
    }


    @Test
    public void universalTime_isCorrect() throws Exception {
        universalTime_isCorrect(2016, Calendar.SEPTEMBER, 17, 14, 9, 25);
        universalTime_isCorrect(2000, Calendar.SEPTEMBER, 17, 14, 9, 25);
        universalTime_isCorrect(1990, Calendar.SEPTEMBER, 17, 14, 9, 25);
        universalTime_isCorrect(2000, Calendar.SEPTEMBER, 17, 0, 0, 0);
        universalTime_isCorrect(1987, Calendar.APRIL, 10, 0, 0, 0);
        universalTime_isCorrect(1987, Calendar.APRIL, 10, 19, 21, 35);
        universalTime_isCorrect(1990, Calendar.APRIL, 19, 0, 0, 0);
    }

    public void universalTime_isCorrect(int year, int month, int day, int hour, int minute, int second) throws Exception {
        UTC utc = UTC.forUTC(year, month, day, hour, minute, second);
        double time = hour + minute / 60.0 + second / 3600.0;
        assertEquals("UT of " + utc.toString(), time, utc.getUT(), EPS);
    }


    @Test
    public void siderealTime_isCorrect() throws Exception {
        siderealTime_isCorrect(1987, Calendar.APRIL, 10, 0, 0, 0, Hour.fromHour(13, 10, 46.1351).angleInHours, 0);
        siderealTime_isCorrect(1987, Calendar.APRIL, 10, 19, 21, 0, Hour.fromHour(8, 34, 57.0896).angleInHours, 0);
        siderealTime_isCorrect(1990, Calendar.APRIL, 19, 0, 0, 0, 14.78925, 15);
    }

    private void siderealTime_isCorrect(int year, int month, int day, int hour, int minute, int second, double expected, double longitude) {
        UTC utc = UTC.forUTC(year, month, day, hour, minute, second);
        double GMST = utc.getGMST();
        double LST = Hour.normalize(GMST + longitude/15);
        assertEquals("GMST for " + utc.toString(), expected, LST, ONE_MINUTE_IN_DAYS);
    }

    @Test
    public void sin_isCorrect() throws Exception {
        assertEquals("SIN 1104.06528413449996", 0.970019377784596, Degree.sin(104.06528413449996), EPS);
    }

    @Test
    public void cos_isCorrect() throws Exception {
        assertEquals("COS 104.06528413449996", -0.243027315679457, Degree.cos(104.06528413449996), EPS);
    }

    @Test
    public void UTC_isCorrect() throws Exception {

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.set(2016, 9, 15, 17, 25, 33);
        calendar.set(Calendar.MILLISECOND, 0);

        double JD = JulianDay.fromGMT(calendar);
        UTC utc = UTC.forUTC(2016, 9, 15, 17, 25, 33);

        assertEquals("UTC JD", JD, utc.getJulianDay(), EPS);
    }
}