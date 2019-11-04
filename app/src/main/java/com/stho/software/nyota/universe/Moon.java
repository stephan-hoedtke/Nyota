package com.stho.software.nyota.universe;

import com.stho.software.nyota.utilities.Degree;
import com.stho.software.nyota.utilities.Formatter;
import com.stho.software.nyota.utilities.Hour;
import com.stho.software.nyota.utilities.IconNameValueList;
import com.stho.software.nyota.utilities.IconValueList;
import com.stho.software.nyota.utilities.JulianDay;
import com.stho.software.nyota.utilities.Moment;
import com.stho.software.nyota.utilities.UTC;

/**
 * Created by shoedtke on 30.08.2016.
 */
public class Moon extends AbstractSolarSystemElement {

    final static double RADIUS = 1737; // in km

    public double parallax; // lunar parallax
    public double diameter; // apparent  diameter of the moon

    public UTC prevNewMoon;
    public UTC fullMoon;
    public UTC nextNewMoon;
    public double age;

    public double nuclearShadow;
    public double halfShadow;
    public double far;
    public double near;

    @Override
    public boolean isMoon() {
        return true;
    }

    @Override
    public String getName() {
        return "Moon";
    }

    @Override
    public int getImageId() { return com.stho.software.nyota.R.mipmap.moon; }

    @Override
    public int getLargeImageId() { return com.stho.software.nyota.R.drawable.moon; }

    @Override
    public void updateBasics(double d) {

        N = Degree.normalize(125.1228 - 0.0529538083 * d);
        i = 5.1454;
        w = Degree.normalize(318.0634 + 0.1643573223 * d);
        a = 60.2666; // Earth radius
        e = 0.054900;
        M = Degree.normalize(115.3654 + 13.0649929509 * d);
    }

    public void applyPerturbations(Sun sun) {

        double Ls = sun.M + sun.w;  // Mean longitude of the Sun (N=0)
        double Lm = M + w + N;      // Mean longitude of the Moon
        double D = Lm - Ls;         // Mean elongation of the Moon
        double F = Lm - N;          // Argument of latitude for the Moon

        // Add these terms to the Moon's longitude (degrees):
        double lon_corr =
                -1.274 * Degree.sin(M - 2*D)    // (the Evection)
                +0.658 * Degree.sin(2*D)        // (the Variation)
                -0.186 * Degree.sin(sun.M)      // (the Yearly Equation)
                -0.059 * Degree.sin(2*M - 2*D)
                -0.057 * Degree.sin(M - 2*D + sun.M)
                +0.053 * Degree.sin(M + 2*D)
                +0.046 * Degree.sin(2*D - sun.M)
                +0.041 * Degree.sin(M - sun.M)
                -0.035 * Degree.sin(D)          // (the Parallactic Equation)
                -0.031 * Degree.sin(M + sun.M)
                -0.015 * Degree.sin(2*F - 2*D)
                +0.011 * Degree.sin(M - 4*D);

        // Add these terms to the Moon's latitude (degrees):
        double lat_corr =
                -0.173 * Degree.sin(F - 2*D)
                -0.055 * Degree.sin(M - F - 2*D)
                -0.046 * Degree.sin(M + F - 2*D)
                +0.033 * Degree.sin(F + 2*D)
                +0.017 * Degree.sin(2*M + F);

        // Add these terms to the Moon's distance (Earth radii):
        double r_corr =
                -0.58 * Degree.cos(M - 2*D)
                -0.46 * Degree.cos(2*D);

        longitude = Degree.normalize(longitude + lon_corr);
        latitude = Degree.normalizeTo180(latitude + lat_corr);
        r += r_corr;
    }

    // The Moon's topocentric position
    protected void applyTopocentricCorrection(Moment moment) {

        parallax = Degree.arcSin(1 / r);
        diameter = 31.2283333333333 / r;
        double latitude = moment.getLocation().getLatitude();
        double geocentricLatitude = latitude - 0.1924 * Degree.sin(2 * latitude);
        double rho   = 0.99833 + 0.00167 * Degree.cos(2 * latitude);
        double HA = Degree.normalize(15 * moment.LST - RA);
        double g = Degree.arcTan2( Degree.tan(geocentricLatitude), Degree.cos(HA) );

        if (Math.abs(geocentricLatitude) > 0.001 && Math.abs(Decl) > 0.001) {
            double RA_corr = - parallax * rho * Degree.cos(geocentricLatitude) * Degree.sin(HA) / Degree.cos(Decl);
            double Decl_corr =  - parallax * rho * Degree.sin(geocentricLatitude) * Degree.sin(g - Decl) / Degree.sin(g);
            RA = RA + RA_corr;
            Decl = Decl + Decl_corr;
        }
    }


    public void calculateMagnitude() {
        magn = -21.62 + 5*Math.log10(r * R) + 0.026 * FV + 4.0E-9 * Math.pow(FV, 4);
    }


    public void calculateSetRiseTimes(Moment moment) {

        double inSouth = getTimeInSouth(moment);
        double cos_LHA = getHourAngle(moment.getLocation().getLatitude());

        position.inSouth = moment.getUTC().setHours(inSouth);
        position.culmination = getHeightFor(moment.forUTC(position.inSouth));


        // cos_LHA < 0 ---> always up and visible
        // cos_LHA > 0 ---> always down
        if (cos_LHA > -1 && cos_LHA < 1) {

            double LHA = Degree.arcCos(cos_LHA) / 15;
            double tolerance = moment.getLocation().getLongitude() / 15 + 1.5; // current timezone with potential daylight savings?

            position.riseTime = iterate(moment, moment.getUTC().setHours(inSouth - LHA), true);
            position.setTime = iterate(moment, moment.getUTC().setHours(inSouth + LHA), false);

            if (inSouth - LHA < 0.0 + tolerance)
                position.nextRiseTime = iterate(moment, position.riseTime.addHours(24.45), true);

            if (inSouth + LHA > 24.0 - tolerance)
                position.prevSetTime = iterate(moment, position.setTime.addHours(-24.45), false);
        }
    }

    public void calculateNewFullMoon(Moment moment) {
        int shift = Moon.getCurrentShift(moment);
        prevNewMoon = Moon.getNewMoon(moment, shift);
        fullMoon = Moon.getFullMoon(moment, shift);
        nextNewMoon = Moon.getNewMoon(moment, shift + 1);
        age = (moment.getUTC().getJulianDay() - prevNewMoon.getJulianDay()) / (nextNewMoon.getJulianDay() - prevNewMoon.getJulianDay());
    }


    private UTC iterate(Moment moment, UTC x, boolean isRise) {

        if (x == null)
            return null;

        double f = getHeightFor(moment.forUTC(x));

        UTC x1;
        UTC x2;
        double f1;
        double f2;

        if ((isRise && f < 0) || (!isRise && f > 0)) {
            x1 = x;
            x2 = x.addHours(2);
            f1 = f;
            f2 = getHeightFor(moment.forUTC(x2));
        } else {
            x1 = x.addHours(-2);
            x2 = x;
            f1 = getHeightFor(moment.forUTC(x1));
            f2 = f;
        }

        return iterate(x1, x2, f1, f2, moment);
    }

    private final static double TOLERANCE = 0.001;

    private UTC iterate(UTC x1, UTC x2, double f1, double f2, Moment moment) {

        for (int n = 0; n < 100; n++) {

            double dx = UTC.gapInHours(x1, x2);

            UTC x = x1.addHours(dx * f1 / (f1 - f2));

            double f = getHeightFor(moment.forUTC(x));

            if (Math.abs(f) < TOLERANCE)
                return x;

            if (Math.signum(f) == Math.signum(f1)) {
                x1 = x;
                f1 = f;
            } else {
                x2 = x;
                f2 = f;
            }
        }

        return null;
    }

    protected double getHeightFor(Moment moment) {
        Moon moon = getMoonFor(moment);
        return moon.getHeight();
    }

    private double getHeight() {
        return position.altitude - H0();
    }

    protected double H0() {
        // Moon's upper limb touches the horizon; atmospheric refraction accounted for
        // 0.583 atmospheric refraction
        // Moon's parallax, the apparent size of the (equatorial) radius of the Earth, as seen from the Moon: asin(1 / r), r = distance in earth radii ~ 0.95
        // The parallax is accounted for the call of applyTopocentricCorrection()
        // Moon's semi diameter: 1873.7" * 30 / r ~ 0.5 between 29′20″ and 34′6″
        return -0.583 - 0.5 * diameter;
    }

    // The cos of the hour angle for sunrise and sunset
    private double getHourAngle(double observerLatitude) {
        return (Degree.sin(H0()) - Degree.sin(observerLatitude) * Degree.sin(Decl)) / (Degree.cos(observerLatitude) * Degree.cos(Decl));
    }

     private enum Phase {
        FULL,
        NEW,
    }

    public static int getCurrentShift(Moment moment) {
        if (moment.getUTC().getJulianDay() < getTimeFor(moment, Phase.NEW, 0))
            return -1;
        else if (moment.getUTC().getJulianDay()> getTimeFor(moment, Phase.NEW, 1))
            return 1;
        else
            return 0;
    }

    public static UTC getNewMoon(Moment moment, int shift) {
        double julianDay = getTimeFor(moment, Phase.NEW, shift);
        return JulianDay.toUTC(julianDay);
    }

    public static UTC getFullMoon(Moment moment, int shift) {
        double julianDay = getTimeFor(moment, Phase.FULL, shift);
        return JulianDay.toUTC(julianDay);
    }

    private static final double HALF_A_MONTH = 0.5 / 12.3685;

    /**
     * Julian Days for the new moon or full moon, Meeus chapter 47
     * @param moment current time and location
     * @param phase FULL or NEW
     * @param shift Phases before (shift < 0) or after (shift > 0)
     * @return Julian (Ephemeris) Day (in Dynamic Time)
     */
    private static double getTimeFor(Moment moment, Phase phase, int shift) {
        // Meeus chapter 47
        // k is an integer for new moon incremented by 0.25 for first quarter 0.5 for full moon. k=0 corresponds to the New Moon 2000 Jan 6th.

        double years = moment.getUTC().getYearsSince2000() - HALF_A_MONTH;
        double k = Math.floor(years * 12.3685);

        k += shift;

        if (phase == Phase.FULL)
            k += 0.5;

        double T = k / 1236.85;
        double T2 = T * T;
        double T3 = T * T2;
        double T4 = T * T3;

        double E = 1 - 0.002516 * T - 0.0000074 * T2;

        // Sun's mean anomaly
        double M = Degree.normalize(2.5534 + 29.10535670 * k - 0.00000014 * T2 - 0.00000011 * T3);

        // Moon's mean anomaly (M' in Meeus)
        double MP = Degree.normalize(201.5643 + 385.81693528 * k + 0.0107582 * T2 + 0.00001238 * T3 - 0.000000058 * T4);

        // Moons argument of latitude
        double F = Degree.normalize(160.7108 + 390.67050284 * k - 0.0016118 * T2 - 0.00000227 * T3 + 0.000000011 * T4);

        // Longitude of ascending node of lunar orbit
        double Omega = Degree.normalize(124.7746 - 1.56375588 * k + 0.0020672 * T2 + 0.00000215 * T3);

        // The full planetary arguments include 14 terms, only used the 7 most significant
        double A1 = Degree.normalize(299.77 + 0.107408 * k - 0.009173 * T2);
        double A2 = Degree.normalize(251.88 + 0.016321 * k);
        double A3 = Degree.normalize(251.83 + 26.651886 * k);
        double A4 = Degree.normalize(349.42 + 36.412478 * k);
        double A5 = Degree.normalize(84.88 + 18.206239 * k);
        double A6 = Degree.normalize(141.74 + 53.303771 * k);
        double A7 = Degree.normalize(207.14 + 2.453732 * k);
        double A8 = Degree.normalize(154.84 + 27.261239 * k);
        double A9 = Degree.normalize(34.52 + 27.261239 * k);
        double A10 = Degree.normalize(207.19 + 0.121824 * k);
        double A11 = Degree.normalize(291.34 + 1.844379 * k);
        double A12 = Degree.normalize(161.72 + 24.198154 * k);
        double A13 = Degree.normalize(239.56 + 25.513099 * k);
        double A14 = Degree.normalize(331.55 + 3.592518 * k);

        double JDE = 2451550.09766 + 29.530588861 * k + 0.00015437 * T2 - 0.000000150 * T3 + 0.00000000073 * T4;

        // New moon

        // Correct for TDT since 1 July 2015
        // JDE0=JDE0-58.184/(24*60*60);

        if (phase == Phase.NEW) {
            JDE = JDE
                    - 0.40720 * Degree.sin(MP)
                    + 0.17241 * E * Degree.sin(M)
                    + 0.01608 * Degree.sin(2 * MP)
                    + 0.01039 * Degree.sin(2 * F)
                    + 0.00739 * E * Degree.sin(MP - M)
                    - 0.00514 * E * Degree.sin(MP + M)
                    + 0.00208 * E * E * Degree.sin(2 * M)
                    - 0.00111 * Degree.sin(MP - 2 * F)
                    - 0.00057 * Degree.sin(MP + 2 * F)
                    + 0.00056 * E * Degree.sin(2 * MP + M)
                    - 0.00042 * Degree.sin(3 * MP)
                    + 0.00042 * E * Degree.sin(M + 2 * F)
                    + 0.00038 * E * Degree.sin(M - 2 * F)
                    - 0.00024 * E * Degree.sin(2 * MP - M)
                    - 0.00017 * Degree.sin(Omega)
                    - 0.00007 * Degree.sin(MP + 2 * M)
                    + 0.00004 * Degree.sin(2 * MP - 2 * F)
                    + 0.00004 * Degree.sin(3 * M)
                    + 0.00003 * Degree.sin(MP + M - 2 * F)
                    + 0.00003 * Degree.sin(2 * MP + 2 * F)
                    - 0.00003 * Degree.sin(MP + M + 2 * F)
                    + 0.00003 * Degree.sin(MP - M + 2 * F)
                    - 0.00002 * Degree.sin(MP - M - 2 * F)
                    - 0.00002 * Degree.sin(3 * MP + M)
                    + 0.00002 * Degree.sin(4 * MP);
        }

        if (phase == Phase.FULL) {
            JDE = JDE
                    - 0.40614 * Degree.sin(MP)
                    + 0.17302 * E * Degree.sin(M)
                    + 0.01614 * Degree.sin(2 * MP)
                    + 0.01043 * Degree.sin(2 * F)
                    + 0.00734 * E * Degree.sin(MP - M)
                    - 0.00515 * E * Degree.sin(MP + M)
                    + 0.00209 * E * E * Degree.sin(2 * M)
                    - 0.00111 * Degree.sin(MP - 2 * F)
                    - 0.00057 * Degree.sin(MP + 2 * F)
                    + 0.00056 * E * Degree.sin(2 * MP + M)
                    - 0.00042 * Degree.sin(3 * MP)
                    + 0.00042 * E * Degree.sin(M + 2 * F)
                    + 0.00038 * E * Degree.sin(M - 2 * F)
                    - 0.00024 * E * Degree.sin(2 * MP - M)
                    - 0.00017 * Degree.sin(Omega)
                    - 0.00007 * Degree.sin(MP + 2 * M)
                    + 0.00004 * Degree.sin(2 * MP - 2 * F)
                    + 0.00004 * Degree.sin(3 * M)
                    + 0.00003 * Degree.sin(MP + M - 2 * F)
                    + 0.00003 * Degree.sin(2 * MP + 2 * F)
                    - 0.00003 * Degree.sin(MP + M + 2 * F)
                    + 0.00003 * Degree.sin(MP - M + 2 * F)
                    - 0.00002 * Degree.sin(MP - M - 2 * F)
                    - 0.00002 * Degree.sin(3 * MP + M)
                    + 0.00002 * Degree.sin(4 * MP);
        }

        JDE = JDE
                + 0.000325 * Degree.sin(A1)
                + 0.000165 * Degree.sin(A2)
                + 0.000164 * Degree.sin(A3)
                + 0.000126 * Degree.sin(A4)
                + 0.000110 * Degree.sin(A5)
                + 0.000062 * Degree.sin(A6)
                + 0.000060 * Degree.sin(A7)
                + 0.000056 * Degree.sin(A8)
                + 0.000047 * Degree.sin(A9)
                + 0.000042 * Degree.sin(A10)
                + 0.000040 * Degree.sin(A11)
                + 0.000037 * Degree.sin(A12)
                + 0.000035 * Degree.sin(A13)
                + 0.000023 * Degree.sin(A14);

        return JDE;
    }

    // Calculate the time (in UTC) when the sun will be in south at this position (longitude is defined by LST)
    private double getTimeInSouth(Moment moment) {
        return Hour.normalize(RA / 15 - moment.getUTC().getGMST0() - moment.getLocation().getLongitude() / 15);
    }

    // calculate RA and Decl for the moon at this time
    private Moon getMoonFor(Moment moment) {

        Sun sun = new Sun();
        sun.updateBasics(moment.d);
        sun.updateHeliocentricLatitudeLongitude(moment.d);
        sun.updateGeocentricAscensionDeclination();

        Moon moon = new Moon();
        moon.updateBasics(moment.d);
        moon.updateHeliocentricLatitudeLongitude(moment.d);
        moon.updateGeocentricAscensionDeclination(sun);
        moon.applyPerturbations(sun);
        moon.applyTopocentricCorrection(moment);
        moon.updateAzimuthAltitude(moment);

        return moon;
    }

    void calculateShadows(Sun sun) {
        // use FV = 180 - elongation;
        double R = Sun.RADIUS;
        double r = Earth.RADIUS;
        double D = sun.getDistanceInKm();
        double d = getDistanceInKm();
        double x = d * Degree.cos(FV);
        double y = d * Degree.sin(FV);

        if (FV < 45) {
            nuclearShadow = r - (R - r) * x / D;
            halfShadow = (R + r) * (D + x) / D - R;
        }
        else {
            nuclearShadow = 0;
            halfShadow = 0;
        }
        far = y + Moon.RADIUS;
        near = y - Moon.RADIUS;
    }


    @Override
    protected double getDistanceInKm() {
        return Algorithms.EARTH_RADIUS * R;
    }

    @Override
    public IconValueList getBasics(Moment moment) {
        IconValueList basics = super.getBasics(moment);


        if (position.prevSetTime != null)
            basics.add(com.stho.software.nyota.R.drawable.sunset, position.prevSetTime, moment.getTimeZone());

        if (position.riseTime != null)
            basics.add(com.stho.software.nyota.R.drawable.sunrise, position.riseTime, moment.getTimeZone());

        if (position.setTime != null)
            basics.add(com.stho.software.nyota.R.drawable.sunset, position.setTime, moment.getTimeZone());

        if (position.nextRiseTime != null)
            basics.add(com.stho.software.nyota.R.drawable.sunrise, position.nextRiseTime, moment.getTimeZone());

        basics.add(com.stho.software.nyota.R.drawable.angle, Degree.fromDegree(diameter));

        return basics;
    }



    @Override
    public IconNameValueList getDetails(Moment moment) {
        IconNameValueList details = super.getDetails(moment);

        if (position.prevSetTime != null)
            details.add(com.stho.software.nyota.R.drawable.sunset, "Set", position.prevSetTime, moment.getTimeZone());

        if (position.riseTime != null)
            details.add(com.stho.software.nyota.R.drawable.sunrise, "Rise", position.riseTime, moment.getTimeZone());

        if (position.setTime != null)
            details.add(com.stho.software.nyota.R.drawable.sunset, "Set ", position.setTime, moment.getTimeZone());

        if (position.nextRiseTime != null)
            details.add(com.stho.software.nyota.R.drawable.sunrise, "Rise", position.nextRiseTime, moment.getTimeZone());

        details.add(com.stho.software.nyota.R.drawable.star, "Age", Formatter.df2.format(age));
        details.add(com.stho.software.nyota.R.drawable.angle, "Diameter", Degree.fromDegree(diameter));
        details.add(com.stho.software.nyota.R.drawable.star, "Magnitude", Formatter.df2.format(magn));
        details.add(com.stho.software.nyota.R.drawable.star, "FV", Degree.fromDegree(FV));
        details.add(com.stho.software.nyota.R.drawable.star, "Phase", Formatter.df3.format(phase));
        details.add(com.stho.software.nyota.R.drawable.star, "Phase angle", Formatter.df0.format(phaseAngle));
        details.add(com.stho.software.nyota.R.drawable.star, "New", prevNewMoon, moment.getTimeZone());
        details.add(com.stho.software.nyota.R.drawable.star, "Full", fullMoon, moment.getTimeZone());
        details.add(com.stho.software.nyota.R.drawable.star, "New+",  nextNewMoon, moment.getTimeZone());
        details.add(com.stho.software.nyota.R.drawable.star, "Parallax", Degree.fromDegree(parallax));
        details.add(com.stho.software.nyota.R.drawable.star, "Parallactic", Degree.fromDegree(parallacticAngle));
        details.add(com.stho.software.nyota.R.drawable.star, "Meridian", position.inSouth, moment.getTimeZone());
        details.add(com.stho.software.nyota.R.drawable.star, "Culmination", Degree.fromDegree(position.culmination));
        details.add(com.stho.software.nyota.R.drawable.star, "Shadow", Formatter.df0.format(nuclearShadow) + " - " + Formatter.df0.format(halfShadow) + " km");
        details.add(com.stho.software.nyota.R.drawable.star, "Shadow Distance", Formatter.df0.format(near) + " - " + Formatter.df0.format(far) + " km");

        return details;
    }
}
