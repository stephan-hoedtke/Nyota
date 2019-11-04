package com.stho.software.nyota.utilities;

import com.stho.software.nyota.utilities.Degree;

/**
 * Created by shoedtke on 20.01.2017.
 */
public class Radian {

    private static final double TWO_PI = Math.PI * 2;


    public static double fromDegrees(double degree) {
        return Math.toRadians(degree);
    }

    public static double toDegrees(double radian) {
        return Math.toDegrees(radian);
    }

    public static double toDegrees180(double radian) {
        return Degree.normalizeTo180(Math.toDegrees(radian));
    }

    public static double fromHour(double hour) {
        return hour * Math.PI / 12;
    }

    public static double normalize(double radian) {
        radian = Math.IEEEremainder(radian, TWO_PI);

        if (radian < 0)
            radian += TWO_PI;

        return radian;
    }
}
