package com.stho.software.nyota.utilities;

import java.security.InvalidParameterException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by shoedtke on 01.09.2016.
 */
public class Hour {

    final double angleInHours;
    final int hour; // [0..24]
    final int minute;
    final double seconds;


    public static Hour fromDegree(double angleInDegree) {
        return new Hour(angleInDegree / 15);
    }

    static Hour fromHour(double angleInHours) {
        return new Hour(angleInHours);
    }

    static Hour fromHour(int hour, int minute, double seconds) {
        return new Hour(hour, minute, seconds);
    }

    final static private Pattern pattern = Pattern.compile("^(\\d+)[h]\\s(\\d+)[m]\\s(\\d+[.]*\\d*)$"); // for:  13h 25m 11.60s

    public static Hour fromHour(String str) {
        Matcher m = pattern.matcher(str);
        if (m.find() && m.groupCount() == 3) {
            int hour = Integer.parseInt(m.group(1));
            int minute = Integer.parseInt(m.group(2));
            double seconds = Double.parseDouble(m.group(3));
            return fromHour(hour, minute, seconds);
        }
        throw new InvalidParameterException("Invalid hour " + str);
    }

    private Hour(double angleInHours) {
        this.angleInHours = Hour.normalize(angleInHours);
        this.hour = (int)this.angleInHours;
        double minutes = 60 * (this.angleInHours - this.hour);
        this.minute = (int)minutes;
        this.seconds = 60 * (minutes - minute);
    }

    private Hour(int hour, int minute, double seconds) {
        this.hour = Math.abs(hour);
        this.minute = minute;
        this.seconds = seconds;
        this.angleInHours = hour + minute / 60.0 + seconds / 3600.0;
    }

    @Override
    public String toString() {
        return hour + "h " + minute + "m " + Formatter.df0.format(seconds) + "s";
    }

    String toShortString() {
        return hour + "h " + minute + "m";
    }

    public double toDegree() {
        return this.angleInHours * 15;
    }

    public static double normalize(double hour) {
        while (hour < 0)
            hour += 24;
        while (hour > 24)
            hour -= 24;
        return hour;
    }
 }
