package com.stho.software.nyota.utilities;

/**
 * Created by shoedtke on 28.08.2016.
 */
public final class Angle {

    private Angle() {
        // Not instantiable
    }

    public enum AngleType {
        DEGREE_NORTH_EAST_SOUTH_WEST,
        AZIMUTH,
        ALTITUDE,
        LATITUDE,
        LONGITUDE,
    }

    public static float getAngleDifference(float x, float y) {
        return normalizeTo180(x - y);
    }

    public static double getAngleDifference(double x, double y) {
        return normalizeTo180(x - y);
    }

    public static float normalize(float angle) {
        while (angle > 360)
            angle -= 360;

        while (angle < 0)
            angle += 360;

        return angle;
    }

    public static double normalize(double angle) {
        angle = Math.IEEEremainder(angle, 360);

        if (angle < 0)
            angle += 360;

        return angle;
    }

    public static float normalizeTo180(float angle) {
        while (angle > 180)
            angle -= 360;

        while (angle <= -180)
            angle += 360;

        return angle;
    }

    public static double normalizeTo180(double angle) {
        angle = Math.IEEEremainder(angle, 360);

        if (angle < -180)
            angle += 360;

        if (angle > 180)
            angle -= 360;

        return angle;
    }

    public static String toString(double angle, AngleType angleType) {
         double alpha;
         switch (angleType) {

            default:
                alpha = Angle.normalizeTo180(angle);
                return sign(alpha) + Formatter.df2.format(Math.abs(alpha)) + "°";

            case AZIMUTH:
                alpha = Angle.normalize(angle);
                return Formatter.df0.format(Math.abs(alpha)) + "° " + northEastSouthWest(alpha);

            case ALTITUDE:
                alpha = Angle.normalizeTo180(angle);
                return sign(alpha) + Formatter.df0.format(Math.abs(alpha)) + "°";

            case LATITUDE:
                alpha = Angle.normalizeTo180(angle);
                return Formatter.df2.format(Math.abs(alpha)) + "° " + ((angle >= 0) ? "N" : "S");

            case LONGITUDE:
                alpha = Angle.normalizeTo180(angle);
                return Formatter.df2.format(Math.abs(alpha)) + "° " + ((angle >= 0) ? "E" : "W");

            case DEGREE_NORTH_EAST_SOUTH_WEST:
                alpha = Angle.normalize(angle);
                return Formatter.df0.format(Math.abs(alpha)) + "° " + northEastSouthWest(alpha);
        }
    }

    private static String sign(double x) { return (x < 0) ? "-" : "+"; }

    private static String northEastSouthWest(double angle) {
        final int n = (int)(((angle < 0) ? (angle + 282.5) : (angle + 22.5)) / 45);
        final String[] x = new String[]{ "N", "NE", "E", "SE", "S", "SW", "W", "NW", "N" };
        return x[n];
    }
}
