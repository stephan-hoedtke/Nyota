package com.stho.software.nyota.universe;

import com.stho.software.nyota.utilities.Degree;
import com.stho.software.nyota.utilities.Formatter;
import com.stho.software.nyota.utilities.Hour;
import com.stho.software.nyota.utilities.IconNameValueList;
import com.stho.software.nyota.utilities.IconValueList;
import com.stho.software.nyota.utilities.Moment;
import com.stho.software.nyota.utilities.UTC;

import java.util.concurrent.TimeUnit;

/**
 * Orbital SolarSystemElement
 * Created by shoedtke on 30.08.2016.
 */
public abstract class AbstractSolarSystemElement extends AbstractElement {

    public double N; // longitude of the ascending node
    public double i; // inclination to the ecliptic (plane of the Earth's orbit)
    public double w; // argument of perihelion (nearest to sun in orbit)
    public double a; // semi-major axis or mean distance from the Sun
    public double e; // eccentricity
    public double M; // mean anomaly (0 at perihelion)

    protected double r; // mean radius
    protected double w1; // longitude of perihelion
    protected double q; // perihelion distance
    protected double Q; // aphelion distance
    protected double P; // orbital period (years of a is in AU)
    protected double T; // time of perihelion
    protected double v; // true anomaly (angle between position and perihelion)
    protected double ecl; // obliquity of the ecliptic, i.e. the "tilt" of the earths axis of rotation
    protected double R; // geocentric distance
    protected double L; // mean longitude
    protected double E; // eccentric anomaly

    protected double longitude; // elliptic heliocentric longitude
    protected double latitude; // elliptic heliocentric latitude

    protected double x;
    protected double y;
    protected double z;

    protected double elongation; // the apparent angular distance of the planet from the sun
    protected double FV;
    public double phase;
    public double phaseAngle;
    public double parallacticAngle;
    public double zenithAngle;

    abstract public String getName();
    abstract public void updateBasics(double d);

    public boolean isSun() { return false; }
    public boolean isMoon() { return false; }
    public boolean isPlanet() { return false; }


    public void updateHeliocentricLatitudeLongitude(double d) {

        w1 = N + w;
        q = a * (1 - e);
        Q = a * (1 + e);
        P = Math.pow(a, 1.5);
        ecl = 23.4393 - 3.563E-7 * d;
        L = Degree.normalize(M + w + N);

        // eccentric anomaly with iteration for "larger" eccentric
        double E0 = M + Degree.RADEG * e * Degree.sin(M) * (1.0 + e * Degree.cos(M));
        double E1 = E0 - (E0 - Degree.RADEG * e * Degree.sin(E0) - M) / (1 - e * Degree.cos(E0));

        for (int x = 0; x < 10 && Math.abs(E1 - E0) > 0.001; x++) {
            E0 = E1;
            E1 = E0 - (E0 - Degree.RADEG * e * Degree.sin(E0) - M) / (1 - e * Degree.cos(E0));
        }

        E = Degree.normalize(E1);

        double xv = a * (Degree.cos(E) - e);
        double yv = a * (Math.sqrt(1.0 - e * e) * Degree.sin(E));

        v = Degree.normalize(Degree.arcTan2(yv, xv));
        r = Math.sqrt(xv * xv + yv * yv);

        // mean longitude
        double lon = v + w;

        // position int the 3 dimensional space
        // for the moon: geocentric position in the ecliptic coordinate system
        // for planets: heliocentric position in the ecliptic coordinate system
        double xh = r * (Degree.cos(N) * Degree.cos(lon) - Degree.sin(N) * Degree.sin(lon) * Degree.cos(i));
        double yh = r * (Degree.sin(N) * Degree.cos(lon) + Degree.cos(N) * Degree.sin(lon) * Degree.cos(i));
        double zh = r * (Degree.sin(lon) * Degree.sin(i));

        // ecliptic longitude, latitude
        longitude = Degree.normalize(Degree.arcTan2(yh, xh));
        latitude = Degree.normalizeTo180(Degree.arcTan2(zh, Math.sqrt(xh * xh + yh * yh)));
    }


    public void applyPrecession(double Epoch, double d) {

        double lon_corr = 3.82394E-5 * (365.2422 * (Epoch - 2000.0) - d);
        longitude = Degree.normalize(longitude + lon_corr);
    }

    public void updateGeocentricAscensionDeclination(Sun sun) {

        // we may ignore precession! (all values are calculated for the moment of d)
        // ecliptic rectangular geocentric coordinates
        x = r * Degree.cos(longitude) * Degree.cos(latitude);
        y = r * Degree.sin(longitude) * Degree.cos(latitude);
        z = r * Degree.sin(latitude);

        // equatorial rectangular geocentric coordinates
        double xg = x;
        double yg = y;
        double zg = z;

        if (isPlanet()) {
            xg += sun.x;
            yg += sun.y;
            zg += sun.z;
        }

        double xe = xg;
        double ye = yg * Degree.cos(ecl) - zg * Degree.sin(ecl);
        double ze = yg * Degree.sin(ecl) + zg * Degree.cos(ecl);

        // Finally, compute the planet's Right Ascension (RA) and Declination (Decl):
        RA  = Degree.normalize(Degree.arcTan2(ye, xe));
        Decl = Degree.normalizeTo180(Degree.arcTan2(ze, Math.sqrt(xe*xe + ye*ye)));

        // Compute the geocentric distance:
        R = Math.sqrt(xg*xg + yg*yg + zg*zg);
    }

    public void calculatePhase(Sun sun) {
        elongation = Degree.arcCos(Degree.cos(sun.longitude - this.longitude) * Degree.cos(this.latitude));
        FV = 180 - elongation;
        phase = (1 + Degree.cos(FV)) / 2;
        phaseAngle = getPhaseAngle(sun);
    }

    public void calculateParallacticAngle(Moment moment) {
        parallacticAngle = getParallacticAngle(moment);
        zenithAngle = Degree.normalizeTo180(phaseAngle - parallacticAngle);
    }

    protected double getPhaseAngle(Sun sun) {
        // Meeus pp.347
        // Further correction: zenith-angle := phase-angle - parallactic-angle
        return Degree.arcTan2(
                Degree.cos(sun.Decl) * Degree.sin(sun.RA - this.RA),
                Degree.sin(sun.Decl) * Degree.cos(this.Decl) - Degree.cos(sun.Decl) * Degree.sin(this.Decl) * Degree.cos(sun.RA - this.RA));
    }

    protected double getZenithAngle(Sun sun) {
        // Calculation from altitude doesn't require correction by the parallactic-angle, but the current altitude and azimuth
        return Degree.arcTan2(
                Degree.cos(sun.position.altitude) * Degree.sin(this.position.azimuth - sun.position.azimuth),
                Degree.sin(sun.position.altitude) * Degree.cos(this.position.altitude) - Degree.cos(sun.position.altitude) * Degree.sin(this.position.altitude) * Degree.cos(this.position.azimuth - sun.position.azimuth));
    }

    protected double getParallacticAngle(Moment moment) {
        double HA = Degree.normalize(15 * moment.LST - this.RA);
        return Degree.arcTan2(Degree.sin(HA), Degree.tan(moment.getLocation().getLatitude()) * Degree.cos(this.Decl) - Degree.sin(this.Decl) * Degree.cos(HA));
    }

    protected double getDistanceInKm() {
        return Algorithms.ASTRONOMIC_UNIT * R;
    }

    @Override
    public IconValueList getBasics(Moment moment) {
        IconValueList basics = super.getBasics(moment);

        basics.add(com.stho.software.nyota.R.drawable.distance, Formatter.df0.format(getDistanceInKm()) + " km");

        return basics;
    }

    @Override
    public IconNameValueList getDetails(Moment moment) {
        IconNameValueList details = super.getDetails(moment);

        details.add(com.stho.software.nyota.R.drawable.distance, "Distance", Formatter.df0.format(getDistanceInKm()) + " km");
        details.add(com.stho.software.nyota.R.drawable.angle, "Elongation", Degree.fromDegree(elongation));

        return details;
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

            if (inSouth - LHA < 0.0 + tolerance || (position.riseTime.isLessThan(position.setTime) && position.setTime.isLessThan(moment.getUTC())))
                position.nextRiseTime = iterate(moment, position.riseTime.addHours(24.45), true);

            if (inSouth + LHA > 24.0 - tolerance || (position.riseTime.isGreaterThan(moment.getUTC()) && position.setTime.isGreaterThan(position.riseTime)))
                position.prevSetTime = iterate(moment, position.setTime.addHours(-24.45), false);
        }
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

    // The cos of the hour angle for sunrise and sunset
    private double getHourAngle(double observerLatitude) {
        return (Degree.sin(H0()) - Degree.sin(observerLatitude) * Degree.sin(Decl)) / (Degree.cos(observerLatitude) * Degree.cos(Decl));
    }

    // Calculate the time (in UTC) when the sun will be in south at this position (longitude is defined by LST)
    private double getTimeInSouth(Moment moment) {
        double hour = Hour.normalize(RA / 15 - moment.getUTC().getGMST0() - moment.getLocation().getLongitude() / 15);

        double offsetInHours = TimeUnit.HOURS.convert(moment.getTimeZone().getOffset(moment.getUTC().getTimeInMillis()), TimeUnit.MILLISECONDS);
        double ut = moment.getUTC().getUT();
        double lt = ut + offsetInHours;

        if (lt > 24)
            hour += 24;
        else if (lt < 0)
            hour -= 24;

        return hour;
    }

    protected abstract double H0();

    protected abstract double getHeightFor(Moment moment);
}

