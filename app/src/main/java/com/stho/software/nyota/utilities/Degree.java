package com.stho.software.nyota.utilities;

import java.security.InvalidParameterException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by shoedtke on 30.08.2016.
 */
public class Degree {

    final double angleInDegree;
    final int sign;
    final int degree;
    final int minute;
    final double seconds;

    public static Degree fromDegree(double angleInDegree) {
        return new Degree(angleInDegree);
    }

    public static Degree fromPositive(int degree, int minute, double seconds) {
        return new Degree(1, Math.abs(degree), minute, seconds);
    }

    public static Degree fromNegative(int degree, int minute, double seconds) {
        return new Degree(-1, Math.abs(degree), minute, seconds);
    }

    final static private Pattern pattern = Pattern.compile("^([+|−|-|–|-])(\\d+)[°]\\s(\\d+)[′|']\\s(\\d+[.]*\\d*)$"); // for:  −11° 09′ 40.5

    public static Degree fromDegree(String str) {
        Matcher m = pattern.matcher(str);
        if (m.find() && m.groupCount() == 4) {
            int degree = Integer.parseInt(m.group(2));
            int minute = Integer.parseInt(m.group(3));
            double seconds = Double.parseDouble(m.group(4));
            if (m.group(1).equals("+"))
                return Degree.fromPositive(degree, minute, seconds);
            else
                return Degree.fromNegative(degree, minute, seconds);
        }
        throw new InvalidParameterException("Invalid degree " + str);
    }


    private Degree(double angleInDegree) {
        this.angleInDegree = Degree.normalizeTo180(angleInDegree);
        this.sign = (int)Math.signum(this.angleInDegree);
        double degrees = Math.abs(this.angleInDegree);
        this.degree = (int)degrees;
        double minutes = 60 * (degrees - this.degree);
        this.minute = (int)minutes;
        this.seconds = 60 * (minutes - minute);
    }

    private Degree(int sign, int degree, int minute, double seconds) {
        this.sign = sign;
        this.degree = degree;
        this.minute = minute;
        this.seconds = seconds;
        this.angleInDegree = this.sign * (this.degree + this.minute / 60.0 + this.seconds / 3600.0);
    }


    /* convert from degree to radian */
    public static final double DEGRAD = Math.PI / 180.0;

    /* convert from radian to degree */
    public static final double RADEG = 180.0 / Math.PI;


    public static double sin(double degree) {
        return Math.sin(degree * DEGRAD);
    }

    public static double tan(double degree) {
        return Math.tan(degree * DEGRAD);
    }

    public static double cos(double degree) {
        return Math.cos(degree * DEGRAD);
    }

    public static double arcTan2(double y, double x) {
        return RADEG * Math.atan2(y, x);
    }

    public static double arcSin(double x) {
        return RADEG * Math.asin(x);
    }

    public static double arcCos(double x) { return RADEG * Math.acos(x); }

    public static double normalize(double degree) {
        double a = Math.IEEEremainder(degree, 360.0);
        if (a < 0)
            a += 360.0;
        return a;
    }

    public static double normalizeTo180(double degree) {
        double a = Math.IEEEremainder(degree, 360.0);
        if (a > 180)
            a -= 360;
        if (a < -180)
            a += 360;
        return a;
    }

    @Override
    public String toString() {
        return (sign < 0 ? "-" : "") + degree + "° " + minute + "' " + Formatter.df0.format(seconds) + "''";
    }

    public String toShortString() {
        return  (sign < 0 ? "-" : "") + degree + "° " + minute + "'";
    }

    public double toDegree() {
        return this.angleInDegree;
    }
}

