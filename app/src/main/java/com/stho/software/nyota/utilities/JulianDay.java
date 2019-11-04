package com.stho.software.nyota.utilities;

import com.stho.software.nyota.BuildConfig;
import com.stho.software.nyota.universe.Algorithms;

import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by shoedtke on 20.01.2017.
 */
public class JulianDay {

    private static final double MILLIS_PER_DAY = 86400000;
    private static final double JULIAN_DAY_2000_JAN_FIRST = 2451544.5000;
    private static final long JANUARY_FIRST_2000_IN_MILLIS = 946684800000L;

    /**
     * Julian Day Number
     * @param utc a date in UTC
     * @return Julian Day Number
     */
    public static double fromGMT(Calendar utc) {
        if (BuildConfig.DEBUG) {
            if (utc.getTimeZone().getRawOffset() != TimeZone.getTimeZone("GMT").getRawOffset()) {
                throw new InvalidParameterException("Julian day requires GMT");
            }
        }
        int Y = utc.get(Calendar.YEAR);
        int M = utc.get(Calendar.MONTH) + 1;
        int D = utc.get(Calendar.DAY_OF_MONTH);
        if (M <= 2) {
            Y = Y - 1;
            M = M + 12;
        }
        double A = Math.rint(Y / 100);
        double B = 0;

        if (JulianDay.isGregorian(Y, M, D))
            B = Algorithms.truncate(2 - A + Algorithms.truncate(A / 4));

        return Algorithms.truncate(365.25 * (Y + 4716)) + Algorithms.truncate(30.6001 * (M + 1)) + D + B - 1524.5 + Algorithms.UT(utc) / 24.0;
    }

    //TODO use this here...
    public static UTC toUTC(double julianDay) {
        long millis = JulianDay.toTimeInMillis(julianDay);
        return new UTC(millis);
    }

    //TODO: replace
    public static long toTimeInMillis(double julianDay) {
        double days = julianDay - JULIAN_DAY_2000_JAN_FIRST;
        double millis = JANUARY_FIRST_2000_IN_MILLIS + days * MILLIS_PER_DAY;
        return (long)millis;
    }

    /**
     * Universal Time in angleInHours (without year / month / day)
     * @param JD Julian Day
     * @return Universal Time
     */
    public static double UT(double JD) {
        return 24.0 * Algorithms.getDecimals(JD - 0.5);
    }

    private static boolean isGregorian(int Y, int M, int D) {
        return (Y > 1582) || (Y == 1582 && M > 10) || (Y == 1582 && M == 10 && D > 4);
    }
}
