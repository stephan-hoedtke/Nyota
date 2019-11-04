package com.stho.software.nyota.utilities;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by shoedtke on 04.10.2016.
 */
@SuppressWarnings("WeakerAccess")
public final class Formatter {

    final static SimpleDateFormat dateTimeSecTimeZone = new SimpleDateFormat("d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
    final static SimpleDateFormat dateTimeSec = new SimpleDateFormat("d MMM HH:mm:ss", Locale.ENGLISH);
    final static SimpleDateFormat dateTimeTimeZone = new SimpleDateFormat("d MMM yyyy HH:mm Z", Locale.ENGLISH);
    public final static SimpleDateFormat dateTime = new SimpleDateFormat("d MMM HH:mm", Locale.ENGLISH);
    final static SimpleDateFormat time = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
    final static SimpleDateFormat dateTimeZone = new SimpleDateFormat("d MMM yyyy Z", Locale.ENGLISH);
    public final static SimpleDateFormat date = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
    public final static NumberFormat df0 = DecimalFormat.getNumberInstance(Locale.ENGLISH);
    public final static NumberFormat df2 = DecimalFormat.getNumberInstance(Locale.ENGLISH);
    public final static NumberFormat df3 = DecimalFormat.getNumberInstance(Locale.ENGLISH);

    public static final String SPACE = "  ";
    public static final char UNICODE_THIN_SPACE = '\u2009';

    static {
        // Static Initializer of properties set above
        DecimalFormatSymbols symbols = ((DecimalFormat)df0).getDecimalFormatSymbols();
        symbols.setGroupingSeparator(UNICODE_THIN_SPACE); // unicode thin space

        df0.setMaximumFractionDigits(0);
        df0.setMinimumFractionDigits(0);
        df0.setMinimumIntegerDigits(1);
        ((DecimalFormat)df0).setDecimalFormatSymbols(symbols);

        df2.setMinimumFractionDigits(2);
        df2.setMaximumFractionDigits(2);
        df2.setMinimumIntegerDigits(1);
        df3.setMinimumFractionDigits(3);
        df3.setMaximumFractionDigits(3);
        df3.setMinimumIntegerDigits(1);
    }

    static int parseInt(String s) {
        return Integer.parseInt(s.trim());
    }

    static double parseDouble(String s) throws ParseException {
        return df3.parse(s.trim()).doubleValue();
    }

    static String parseString(String s) {
        return s.trim();
    }

    static TimeZone parseTimeZone(String s) {
        return TimeZone.getTimeZone(s.trim());
    }

    public enum TimeFormat
    {
        DATE,
        DATE_TIMEZONE,
        TIME,
        DATETIME,
        DATETIME_TIMEZONE,
        DATETIME_SEC,
        DATETIME_SEC_TIMEZONE,
    }

    public static String toString(UTC utc, TimeZone timeZone, TimeFormat timeFormat) {
        return toString(utc.getTime(), timeZone, timeFormat);
    }

    static String toString(Calendar calendar, TimeFormat timeFormat) {
        return toString(calendar.getTime(), calendar.getTimeZone(), timeFormat);
    }

    private static final long HALF_A_MINUTE_IN_MILLISECONDS = 29000;

    private static java.util.Date toNearestWholeMinute(java.util.Date utc) {
        return new Date(utc.getTime() + HALF_A_MINUTE_IN_MILLISECONDS);
    }

    static String toString(java.util.Date utc, TimeZone timeZone, TimeFormat timeFormat) {
        Calendar calendar = Calendar.getInstance(timeZone);
        SimpleDateFormat df = null;
        switch (timeFormat) {
            case TIME:
                df = Formatter.time;
                df.setTimeZone(timeZone);
                return df.format(utc);

            case DATE:
                df = Formatter.date;
                df.setTimeZone(timeZone);
                return df.format(utc);

            case DATE_TIMEZONE:
                df = Formatter.dateTimeZone;
                df.setTimeZone(timeZone);
                return df.format(utc);

            case DATETIME:
                df = Formatter.dateTime;
                df.setTimeZone(timeZone);
                return df.format(toNearestWholeMinute(utc));

            case DATETIME_TIMEZONE:
                df = Formatter.dateTimeTimeZone;
                df.setTimeZone(timeZone);
                return df.format(toNearestWholeMinute(utc));

            case DATETIME_SEC:
                df = Formatter.dateTimeSec;
                df.setTimeZone(timeZone);
                return df.format(utc);

            case DATETIME_SEC_TIMEZONE:
                df = Formatter.dateTimeSecTimeZone;
                df.setTimeZone(timeZone);
                return df.format(utc);

            default:
                df = Formatter.dateTime;
                df.setTimeZone(timeZone);
                return df.format(utc.getTime());
        }
    }
}

