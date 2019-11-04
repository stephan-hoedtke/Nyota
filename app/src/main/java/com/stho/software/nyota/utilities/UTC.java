package com.stho.software.nyota.utilities;

import com.stho.software.nyota.universe.Algorithms;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Class to speed up calculations of time and date in Calendar, Milliseconds and Julian Day
 *     // Time API:
 *     // 1. Instant, ZonedDateTime, ...
 *     //       https://barta.me/new-date-time-api-in-java-8/
 *     // --> recommendation: use LocalDateTime, Instant, ZonedDateTime, and not the old Date or Calendar,
 *     //     but this Java API requires Android API Level >= 26
 *     //       https://stackoverflow.com/questions/32437550/whats-the-difference-between-instant-and-localdatetime
 *     // 2. Calendar:
 *     //       https://crunchify.com/how-to-convert-time-between-timezone-in-java/
 */
public class UTC implements ITime {

    private final double JD;
    private final long timeInMillis;
    private Calendar gmt = null;

    @Override
    public long getTimeInMillis() {
        return timeInMillis;
    }

    @Override
    public double getJulianDay() {
        return JD;
    }

    private Calendar getGmt() {
        // The calendar is only required from printing the date. Calculation can be done without it, just on JD.
        if (gmt == null) {
            gmt = createCalendarGMT(timeInMillis);
        }
        return gmt;
    }

    public java.util.Date getTime() {
        return getGmt().getTime();
    }

    public UTC(final long timeInMillis) {
        this.gmt = createCalendarGMT(timeInMillis);
        this.JD = JulianDay.fromGMT(gmt);
        this.timeInMillis = timeInMillis;
    }

    private UTC(final Calendar utc) {
        this.gmt = utc;
        this.JD = JulianDay.fromGMT(gmt);
        this.timeInMillis = utc.getTimeInMillis();
    }

    private UTC(final long timeInMillis, final double JD) {
        this.gmt = null;
        this.JD = JD;
        this.timeInMillis = timeInMillis;
    }

    public static UTC forUTC(final int year, final int month, final int day, final int hour, final int minute) {
        final Calendar calendar = createCalendar(year, month, day, hour, minute, 0);
        return new UTC(calendar);
    }

    public static UTC forUTC(final int year, final int month, final int day, final int hour, final int minute, final int seconds) {
        final Calendar calendar = createCalendar(year, month, day, hour, minute, seconds);
        return new UTC(calendar);
    }

    private static Calendar createCalendarGMT(final long timeInMillis) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(timeInMillis);
        return calendar;
    }

    private static Calendar createCalendar(final int year, final int month, final int day, final int hour, final int minute, final int seconds) {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.set(year, month, day, hour, minute, seconds);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }


    private static final double HOURS_PER_DAY = 24;
    private static final double MILLIS_PER_DAY = 86400000;
    private static final double MILLIS_PER_HOUR = 3600000;
    private static final double MILLIS_PER_MINUTE = 60000;
    private static final double JULIAN_DAY_2000_JAN_FIRST = 2451544.5000;
    private static final double JULIAN_DAYS_PER_YEAR = 365.25;

    public static UTC forNow() {
        return new UTC(System.currentTimeMillis());
    }

    public double getMinutes() {
        return this.timeInMillis / MILLIS_PER_MINUTE - 24563000;
    }

    public double getDayNumber() {
        return JD - 2451543.5;
    }

    public double getGMST0() {
        return Algorithms.GMST0(JD);
    }

    public double getGMST() {
        return Algorithms.GMST(JD);
    }

    public double getUT() {
        return Algorithms.UT(JD);
    }

    /**
     * Years since 2000 Jan 1st
     *
     * @return years and faction of a year of 365.25 days
     */
    public double getYearsSince2000() {
        return (this.JD - JULIAN_DAY_2000_JAN_FIRST) / JULIAN_DAYS_PER_YEAR;
    }

    /**
     * Returns the local sidereal time in angleInHours [0 ; 24]
     * The sidereal time is measured by the rotation of the Earth, with respect to the stars (rather than relative to the Sun).
     * Local sidereal time is the right ascension (RA, an equatorial coordinate) of a star on the observers meridian.
     * One sidereal day corresponds to the time taken for the Earth to rotate once with respect to the stars and lasts approximately 23 h 56 min.
     *
     * @param observerLongitude is the longitude for which the sidereal time shall be calculated in degrees.
     * @return the local sidereal time in angleInHours [0 ; 24]
     */
    public double getLST(final double observerLongitude) {
        return Algorithms.LST(JD, observerLongitude);
    }

    public UTC addMillis(final long millis) {
        return new UTC(this.timeInMillis + millis, this.JD + millis / MILLIS_PER_DAY);
    }

    public UTC addHours(final double hours) {
        double millis = MILLIS_PER_HOUR * hours;
        double days = hours / HOURS_PER_DAY;
        return new UTC(this.timeInMillis + (long) millis, this.JD + days);
    }

    public UTC setHours(final double hours) {
        double millis = MILLIS_PER_HOUR * hours;
        double days = hours / HOURS_PER_DAY;
        double ut = Algorithms.UT(JD) * MILLIS_PER_HOUR;
        return new UTC(this.timeInMillis - (long) ut + (long) millis, Algorithms.JD0(JD) + days);
    }

    public static double gapInHours(final UTC a, final UTC b) {
        return (b.timeInMillis - a.timeInMillis) / MILLIS_PER_HOUR;
    }

    @Override
    public String toString() {
        return Formatter.dateTime.format(getTime()) + Formatter.SPACE + "GMT";
    }

    public boolean isGreaterThan(final UTC that) {
        return this.JD > that.JD;
    }

    public boolean isLessThan(final UTC that) {
        return this.JD < that.JD;
    }


    public String serialize() {
        return Long.toString(timeInMillis);
    }

    public static UTC deserialize(final String str) {
        try {
            long timeInMillis = Long.parseLong(str);
            return new UTC(timeInMillis);
        } catch (Exception ex) {
            return null;
        }
    }
}

