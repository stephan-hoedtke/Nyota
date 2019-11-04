package com.stho.software.nyota.universe;

import com.stho.software.nyota.utilities.ILocation;
import com.stho.software.nyota.utilities.Moment;
import com.stho.software.nyota.utilities.Radian;
import com.stho.software.nyota.utilities.Degree;
import com.stho.software.nyota.utilities.Hour;
import com.stho.software.nyota.utilities.Location;
import com.stho.software.nyota.utilities.Topocentric;
import com.stho.software.nyota.utilities.Vector;

import java.util.Calendar;

/**
 * Jean Meeus: Astronomical Algorithms
 */
public abstract class Algorithms {

    final static double ASTRONOMIC_UNIT = 149597870.7; // in km
    final static double SECONDS_PER_HOUR = 3600;
    final static double EARTH_RADIUS = 6378.137000; // in km
    final static double EARTH_FLATTENING = 1 / 298.257223563;

    /**
     * Universal Time in angleInHours (without year / month / day)
     * @param utc a date in UTC
     * @return Universal Time
     */
    public static double UT(Calendar utc) {
        int H = utc.get(Calendar.HOUR_OF_DAY);
        int M = utc.get(Calendar.MINUTE);
        int S = utc.get(Calendar.SECOND);
        return H + M / 60.0 + S / 3600.0;
    }

    /**
     * Universal Time in angleInHours (without year / month / day)
     * @param JD Julian Day
     * @return Universal Time
     */
    public static double UT(double JD) {
        return 24.0 * Algorithms.getDecimals(JD - 0.5);
    }


    /**
     * Julian Day from the beginning of the day 0:00 GMT
     * @param JD Julian Day
     * @return Julian Day
     */
    public static double JD0(double JD) {
        return Algorithms.truncate(JD - 0.5) + 0.5;
    }

    public static double truncate(double value) {
        return (value >= 0) ? Math.floor(value) : - Math.floor(Math.abs(value));
    }

    public static double getDecimals(double value) {
        return (value >= 0) ? value - Math.floor(value) : value + Math.floor(Math.abs(value));
    }

    /**
     * Sidereal Time at Greenwich /saɪˈdɪə.ri.əl ˌtaɪm
     * "time based on the movement of the earth in relation to the stars"
     * @param JD Julian Day
     * @return Sidereal Time at Greenwich [0 .. 24]
     */
    public static double GMST0(double JD) {
        // (2) Paul Schlyter, Computing planetary positions
        // Sun's position
        // longitude of perihelion:  282.9404° + 0.0000470935 * d
        // mean anomaly:             356.0470° + 0.9856002585 * d
        // L = w + M
        // GMST0 = L/15 + 12h
        final double d = JD - 2451543.5;
        final double L = 278.9874 + 0.985647352 * d;
        return Hour.normalize(L / 15 + 12);
    }

    private static double GMST_JeanMeeus(double JD) {
        // (1) Jean Meeus, Astronomical Algorithms
        final double T = (JD - 2451545.0) / 36525;
        final double degree = Degree.normalize(280.46061837 + 360.98564736629 * (JD - 2451545.0) + T * T * (0.000387933 - T / 38710000));
        return degree / 15;
    }

    private static double GMST_PaulSchlyter(double JD) {
        // (2) Paul Schlyter, Computing planetary positions
        final double GMST0 = GMST0(JD);
        final double UT = Algorithms.UT(JD);
        return Hour.normalize(GMST0 + UT);
    }

    /// <summary>
    /// Greenwich Mean Sideral Time, the Local Sidereal Time at Greenwich - The time based on the rotation of the earth in relation to the stars (vernal equinox crosses greenwich meridian).
    /// </summary>
    /// <param name="julianDay">Julian Day</param>
    /// <returns>[G]reenwich [M]ean [S]ideral [T]ime in hours</returns>
    public static double GMST(double julianDay)
    {
        return Algorithms.GMST_RummelPeters(julianDay);
    }

    /// <summary>
    /// Greenwich Mean Sidereal time = Right ascension of the sun at the given Julian Day.
    /// Following the explanation of Rummel and Peters
    /// https://de.wikipedia.org/wiki/Sternzeit
    /// </summary>
    /// <param name="julianDay">Julian Day</param>
    /// <returns>Greenwich Mean Sidereal Time (in hours)</returns>
    private static double GMST_RummelPeters(double julianDay)
    {
        final double omega = 1.00273790935;
        double UT1 = Algorithms.getDecimals(julianDay - 0.5);
        double r = (julianDay - UT1 - 2451545.0) / 36525;
        double s = 24110.54841 + r * (8640184.812866 + r * (0.093104 + r * 0.0000062));
        return Hour.normalize(s / SECONDS_PER_HOUR + 24 * UT1 * omega);
    }

    /// <summary>
    /// Local Sidereal Time = Greenwich Mean Siderial Time + Longitude = The time based on the rotation of the earth in relation to the stars (vernal equinox crosses local meridian).
    /// </summary>
    /// <param name="JD">Julian Day</param>
    /// <param name="observerLongitude">Observer's longitude in degree</param>
    /// <returns>[L]ocal [S]idereal [T]ime in hours</returns>
    public static double LST(double julianDay, double observerLongitude)
    {
        double GMST = Algorithms.GMST(julianDay);
        return Hour.normalize(GMST + observerLongitude / 15);
    }

    /// <summary>
    /// Calculates the epoch time in days since 1950 Jan 0.0 UTC, and returns the right ascension of Greenwich at epoch.
    /// Reference: SPACETRACK REPORT NO. 3, 1988
    /// See also: 1992 Astronomical Almanac, page B6
    /// </summary>
    /// <param name="jd">Epoch as Julian Day</param>
    /// <returns>Right ascension of Greenwich at this epoch in radian</returns>
    static double ThetaG(double julianDay)
    {
        double GMST = Algorithms.GMST(julianDay);
        return Radian.fromHour(GMST);
    }

    /// <summary>
    /// Return ECI (Earth centered coordinates), Reference: The 1992 Astronomical Almanac, page K11.
    /// </summary>
    static Vector getECI(Location location, double julianDay)
    {
        double latitude = Radian.fromDegrees(location.getLatitude());
        double longitude = Radian.fromDegrees(location.getLongitude());

        double thetaG = Algorithms.ThetaG(julianDay);
        double theta = Radian.normalize(thetaG + longitude);

        return getECI_EllipticalEarth(latitude, theta, location.getAltitude());
    }

    private static Vector getECI_EllipticalEarth(double phi, double theta, double altitude)
    {
        double a = EARTH_RADIUS + altitude;
        double f = EARTH_FLATTENING;

        double C = 1 / Math.sqrt(1 + f * (f - 2) * Math.sin(phi) * Math.sin(phi));
        double S = (1 - f) * (1 - f) * C;

        return new Vector(
            a * C * Math.cos(phi) * Math.cos(theta),
            a * C * Math.cos(phi) * Math.sin(theta),
            a * S * Math.sin(phi)
        );
    }

    static Topocentric getTopocentricFromPosition(Moment moment, Vector position) {
        return getTopocentricFromPosition(moment.getLocation(), moment.getUTC().getJulianDay(), position);
    }

    static Topocentric getTopocentricFromPosition(Location observer, double julianDay, Vector position) {
        Vector base = Algorithms.getECI(observer, julianDay);
        Vector difference = position.minus(base);
        return getTopocentricFromRelativeECI(observer, julianDay, difference);
    }

    private static Topocentric getTopocentricFromRelativeECI(Location observer, double julianDay, Vector difference)
    {
        double latitude = Radian.fromDegrees(observer.getLatitude());
        double longitude = Radian.fromDegrees(observer.getLongitude());

        double thetaG = Algorithms.ThetaG(julianDay);
        double theta = Radian.normalize(thetaG + longitude);

        return getTopocentricFromRelativeECI(latitude, theta, difference);
    }

    // Call this function always with eci as relative vector to the observer at the specified location
    /// <param name="phi">Latitude in radian</param>
    /// <param name="theta">Local Sidereal Time, LST = GMST + longitude in radian</param>
    /// <param name="eci">Earth centered cartesian coordinates in km</param>
    /// <returns>Topocentric coordinates (azimuth, altitude, distance)
    private static Topocentric getTopocentricFromRelativeECI(double phi, double theta, Vector eci)
    {
        double S = Math.sin(phi) * Math.cos(theta) * eci.x + Math.sin(phi) * Math.sin(theta) * eci.y - Math.cos(phi) * eci.z;
        double Z = Math.cos(phi) * Math.cos(theta) * eci.x + Math.cos(phi) * Math.sin(theta) * eci.y + Math.sin(phi) * eci.z;
        double E = Math.cos(theta) * eci.y - Math.sin(theta) * eci.x;

        double distance = Math.sqrt(S * S + Z * Z + E * E);
        double altitude = Radian.toDegrees180(Math.asin(Z / distance));
        double azimuth = Radian.toDegrees180(Math.PI - Math.atan2(E, S));

        return new Topocentric(azimuth, altitude, distance);
    }


    /// <summary>
    /// Get the geographic location from an ECI
    /// </summary>
    static Location getLocationForECI(Vector eci, double julianDay)
    {
        double thetaG = Algorithms.ThetaG(julianDay);

        return getLocationForECI_EllipticalEarth(eci, thetaG);
    }

    private static Location getLocationForECI_EllipticalEarth(Vector eci, double thetaG)
    {
        final double TOLERANCE = 0.001;

        double a = EARTH_RADIUS;
        double f = EARTH_FLATTENING;
        double R = Math.sqrt(eci.x * eci.x + eci.y * eci.y);
        double e2 = f * (2 - f);
        double phi1 = Math.atan2(eci.z, R);

        for (; ; )
        {
            double sinphi = Math.sin(phi1);
            double C = 1 / Math.sqrt(1 - e2 * sinphi * sinphi);
            double phi = Math.atan2(eci.z + a * C * e2 * sinphi, R);

            if (Math.abs(phi - phi1) < TOLERANCE)
            {
                double lambda = Math.atan2(eci.y, eci.x) - thetaG;
                double height = R / Math.cos(phi) - a * C;

                return new Location(Radian.toDegrees180(phi), Radian.toDegrees180(lambda), height);
            }

            phi1 = phi;
        }
    }

    /// <summary>
    /// Calculates the radius of the circle where the elevation is higher than the minimal elevation
    /// </summary>
    /// <param name="altitude">Height of the satellite in km</param>
    /// <param name="elevation">Minimal elevation in degree</param>
    /// <returns>Radius of visibility in km</returns>
    static double getVisibilityRadius(double height, double elevation)
    {
        // SINUSSATZ --> R / cos(alpha + elevation) = (R+H) / cos(elevation)
        double beta = Radian.fromDegrees(elevation);
        double alpha = Math.acos(Math.cos(beta) * EARTH_RADIUS / (EARTH_RADIUS + height)) - beta;
        return alpha * EARTH_RADIUS;
    }

    /// <summary>
    /// Move by distance into the direction of the bearing angle
    /// </summary>
    /// <param name="location">Location in degree</param>
    /// <param name="bearing">Bearing angle in degree</param>
    /// <param name="distance">Distance in km</param>
    /// <returns></returns>
    static Location calculateNewLocationFromBearingDistance(ILocation location, double bearing, double distance)
    {
        double latitude = Radian.fromDegrees(location.getLatitude());
        double longitude = Radian.fromDegrees(location.getLongitude());
        double angle = Radian.fromDegrees(bearing);
        double ratio = distance / EARTH_RADIUS;

        double newLatitude = Math.asin(Math.sin(latitude) * Math.cos(ratio) + Math.cos(latitude) * Math.sin(ratio) * Math.cos(angle));
        double newLongitude = Math.atan2(Math.sin(angle) * Math.sin(ratio) * Math.cos(latitude), Math.cos(ratio) - Math.sin(latitude) * Math.sin(newLatitude));

        return new Location(Radian.toDegrees180(newLatitude), Radian.toDegrees180(longitude + newLongitude), location.getAltitude());
    }
}


