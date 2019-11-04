package com.stho.software.nyota.utilities;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by shoedtke on 16.09.2016.
 */
public class Moment {

    private final UTC utc;
    private final City city;

    public final double LST; // in hours [0 ; 24]
    public final double d;

    // only work with UTC time, timeZone = GMT
    // when printing time for a moment, it means to print time for a timeZone, so use calendar there when displaying the time. Only then.

    public Moment(final City city, final UTC utc) {
        this.utc = utc; // assuming utc.getTimeZone() is always GMT
        this.city = city;
        this.LST = utc.getLST(city.getLongitude());
        this.d = utc.getDayNumber();
    }

    public Moment(final City city, final long timeInMillis) {
        this.utc = new UTC(timeInMillis);
        this.city = city;
        this.LST = utc.getLST(city.getLongitude());
        this.d = utc.getDayNumber();
    }


    @Override
    public String toString() {
        return Formatter.toString(utc.getTime(), city.getTimeZone(), Formatter.TimeFormat.DATETIME_SEC_TIMEZONE);
    }

    public Moment forNow() {
        return new Moment(city, UTC.forNow());
    }

    /**
     * Keep current location and timezone, but set a new time
     * @param timeInMillis UTC in milliseconds
     * @return a new moment instance
     */
    Moment forUTC(final long timeInMillis) {
        return new Moment(city, new UTC(timeInMillis));
    }

    /**
     * Keep current location and timezone, but set a new time
     * @param utc UTC
     * @return a new moment instance
     */
    public Moment forUTC(final UTC utc) {
        return new Moment(city, utc);
    }

    /**
     * Keep current location and timezone, but set a new time by adding milliseconds
     * @param millis
     * @return
     */
    public Moment addMillis(final long millis) {
        return new Moment(city, utc.addMillis(millis));
    }

    /**
     * Keep current location and timezone, but set a new time by adding hours
     * @param hours any time difference in hours
     * @return a new moment instance
     */
    public Moment addHours(final double hours) {
        return new Moment(city, utc.addHours(hours));
    }

    /**
     * Keep current location and timezone, but set a new time by adding months
     * @param months
     * @return
     */
    public Moment addMonths(final int months) {
        Calendar calendar = getLocalTime();
        calendar.add(Calendar.MONTH, months);
        return forUTC(calendar.getTimeInMillis());
    }

    /**
     * Keep current location and timezone, but set a new time by adding years
     * @param years
     * @return
     */
    public Moment addYears(final int years) {
        Calendar calendar = getLocalTime();
        calendar.add(Calendar.YEAR, years);
        return forUTC(calendar.getTimeInMillis());
    }

    public static Moment forUTC(final City city, final UTC utc) {
        return new Moment(city, utc);
    }

    public static Moment forNow(final City city) {
        return new Moment(city, UTC.forNow());
    }

    public Location getLocation() {
        return city.getLocation();
    }

    public TimeZone getTimeZone() {
        return city.getTimeZone();
    }

    public UTC getUTC() {
        return utc;
    }

    public City getCity() {
        return city;
    }

    public Calendar getLocalTime() {
        final Calendar calendar = Calendar.getInstance(city.getTimeZone());
        calendar.setTimeInMillis(utc.getTimeInMillis());
        return calendar;
    }
}

