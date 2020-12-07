package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.*
import com.stho.nyota.sky.utilities.Vector
import java.util.*

/**
 * Jean Meeus: Astronomical Algorithms
 */
object Algorithms {
    const val ASTRONOMIC_UNIT = 149597870.7 // in km
    const val SECONDS_PER_HOUR = 3600.0
    const val EARTH_RADIUS = 6378.137000 // in km
    const val EARTH_FLATTENING = 1 / 298.257223563

    /**
     * Universal Time in angleInHours (without year / month / day)
     * @param utc a date in UTC
     * @return Universal Time
     */
    @JvmStatic
    fun UT(utc: Calendar): Double {
        val H = utc[Calendar.HOUR_OF_DAY]
        val M = utc[Calendar.MINUTE]
        val S = utc[Calendar.SECOND]
        val X = utc[Calendar.MILLISECOND]
        return H + M / 60.0 + S / 3600.0 + X / 3600000.0
    }

    /**
     * Universal Time in angleInHours (without year / month / day)
     * @param JD Julian Day
     * @return Universal Time
     */
    @JvmStatic
    fun UT(JD: Double): Double {
        return 24.0 * decimals(JD - 0.5)
    }

    /**
     * Julian Day from the beginning of the day 0:00 GMT
     * @param JD Julian Day
     * @return Julian Day
     */
    @JvmStatic
    fun JD0(JD: Double): Double {
        return truncate(JD - 0.5) + 0.5
    }

    // TODO: remove @JvmStatic
    @JvmStatic
    fun truncate(value: Double): Double =
        kotlin.math.truncate(value)

    @JvmStatic
    fun decimals(value: Double): Double =
        value - truncate(value)

    /**
     * Sidereal Time at Greenwich /saɪˈdɪə.ri.əl ˌtaɪm
     * "time based on the movement of the earth in relation to the stars"
     * @param JD Julian Day
     * @return Sidereal Time at Greenwich [0 .. 24]
     */
    @JvmStatic
    fun GMST0(JD: Double): Double {
        // (2) Paul Schlyter, Computing planetary positions
        // Sun's position
        // longitude of perihelion:  282.9404° + 0.0000470935 * d
        // mean anomaly:             356.0470° + 0.9856002585 * d
        // L = w + M
        // GMST0 = L/15 + 12h
        val d = JD - 2451543.5
        val L = 278.9874 + 0.985647352 * d
        return Hour.normalize(L / 15 + 12)
    }

    private fun GMST_JeanMeeus(JD: Double): Double {
        // (1) Jean Meeus, Astronomical Algorithms
        val T = (JD - 2451545.0) / 36525
        val degree = Degree.normalize(280.46061837 + 360.98564736629 * (JD - 2451545.0) + T * T * (0.000387933 - T / 38710000))
        return degree / 15
    }

    private fun GMST_PaulSchlyter(JD: Double): Double {
        // (2) Paul Schlyter, Computing planetary positions
        val GMST0 = GMST0(JD)
        val UT = UT(JD)
        return Hour.normalize(GMST0 + UT)
    }

    /// <summary>
    /// Greenwich Mean Sideral Time, the Local Sidereal Time at Greenwich - The time based on the rotation of the earth in relation to the stars (vernal equinox crosses greenwich meridian).
    /// </summary>
    /// <param name="julianDay">Julian Day</param>
    /// <returns>[G]reenwich [M]ean [S]ideral [T]ime in hours</returns>
    @JvmStatic
    fun GMST(julianDay: Double): Double {
        return GMST_RummelPeters(julianDay)
    }

    /// <summary>
    /// Greenwich Mean Sidereal time = Right ascension of the sun at the given Julian Day.
    /// Following the explanation of Rummel and Peters
    /// https://de.wikipedia.org/wiki/Sternzeit
    /// </summary>
    /// <param name="julianDay">Julian Day</param>
    /// <returns>Greenwich Mean Sidereal Time (in hours)</returns>
    private fun GMST_RummelPeters(julianDay: Double): Double {
        val omega = 1.00273790935
        val UT1 = decimals(julianDay - 0.5)
        val r = (julianDay - UT1 - 2451545.0) / 36525
        val s = 24110.54841 + r * (8640184.812866 + r * (0.093104 + r * 0.0000062))
        return Hour.normalize(s / SECONDS_PER_HOUR + 24 * UT1 * omega)
    }

    /// <summary>
    /// Local Sidereal Time = Greenwich Mean Siderial Time + Longitude = The time based on the rotation of the earth in relation to the stars (vernal equinox crosses local meridian).
    /// </summary>
    /// <param name="JD">Julian Day</param>
    /// <param name="observerLongitude">Observer's longitude in degree</param>
    /// <returns>[L]ocal [S]idereal [T]ime in hours</returns>
    @JvmStatic
    fun LST(julianDay: Double, observerLongitude: Double): Double {
        val GMST = GMST(julianDay)
        return Hour.normalize(GMST + observerLongitude / 15)
    }

    /// <summary>
    /// Calculates the epoch time in days since 1950 Jan 0.0 UTC, and returns the right ascension of Greenwich at epoch.
    /// Reference: SPACETRACK REPORT NO. 3, 1988
    /// See also: 1992 Astronomical Almanac, page B6
    /// </summary>
    /// <param name="jd">Epoch as Julian Day</param>
    /// <returns>Right ascension of Greenwich at this epoch in radian</returns>
    @JvmStatic
    fun ThetaG(julianDay: Double): Double {
        val GMST = GMST(julianDay)
        return Radian.fromHour(GMST)
    }

    /// <summary>
    /// Return ECI (Earth centered coordinates), Reference: The 1992 Astronomical Almanac, page K11.
    /// </summary>
    @JvmStatic
    fun getECI(location: Location, julianDay: Double): Vector {
        val latitude = Radian.fromDegrees(location.latitude)
        val longitude = Radian.fromDegrees(location.longitude)
        val thetaG = ThetaG(julianDay)
        val theta = Radian.normalize(thetaG + longitude)
        return getECI_EllipticalEarth(latitude, theta, location.altitude)
    }

    private fun getECI_EllipticalEarth(phi: Double, theta: Double, altitude: Double): Vector {
        val a = EARTH_RADIUS + altitude
        val f = EARTH_FLATTENING
        val C = 1 / Math.sqrt(1 + f * (f - 2) * Math.sin(phi) * Math.sin(phi))
        val S = (1 - f) * (1 - f) * C
        return Vector(
                a * C * Math.cos(phi) * Math.cos(theta),
                a * C * Math.cos(phi) * Math.sin(theta),
                a * S * Math.sin(phi)
        )
    }

    @JvmStatic
    fun getTopocentricFromPosition(moment: Moment, position: Vector): Topocentric {
        return getTopocentricFromPosition(moment.location, moment.utc.julianDay, position)
    }

    @JvmStatic
    fun getTopocentricFromPosition(observer: Location, julianDay: Double, position: Vector): Topocentric {
        val base = getECI(observer, julianDay)
        val difference = position.minus(base)
        return getTopocentricFromRelativeECI(observer, julianDay, difference)
    }

    private fun getTopocentricFromRelativeECI(observer: Location, julianDay: Double, difference: Vector): Topocentric {
        val latitude = Radian.fromDegrees(observer.latitude)
        val longitude = Radian.fromDegrees(observer.longitude)
        val thetaG = ThetaG(julianDay)
        val theta = Radian.normalize(thetaG + longitude)
        return getTopocentricFromRelativeECI(latitude, theta, difference)
    }

    // Call this function always with eci as relative vector to the observer at the specified location
    /// <param name="phi">Latitude in radian</param>
    /// <param name="theta">Local Sidereal Time, LST = GMST + longitude in radian</param>
    /// <param name="eci">Earth centered cartesian coordinates in km</param>
    /// <returns>Topocentric coordinates (azimuth, altitude, distance)
    private fun getTopocentricFromRelativeECI(phi: Double, theta: Double, eci: Vector): Topocentric {
        val S = Math.sin(phi) * Math.cos(theta) * eci.x + Math.sin(phi) * Math.sin(theta) * eci.y - Math.cos(phi) * eci.z
        val Z = Math.cos(phi) * Math.cos(theta) * eci.x + Math.cos(phi) * Math.sin(theta) * eci.y + Math.sin(phi) * eci.z
        val E = Math.cos(theta) * eci.y - Math.sin(theta) * eci.x
        val distance = Math.sqrt(S * S + Z * Z + E * E)
        val altitude = Radian.toDegrees180(Math.asin(Z / distance))
        val azimuth = Radian.toDegrees180(Math.PI - Math.atan2(E, S))
        return Topocentric(azimuth, altitude, distance)
    }

    /// <summary>
    /// Get the geographic location from an ECI
    /// </summary>
    @JvmStatic
    fun getLocationForECI(eci: Vector, julianDay: Double): Location {
        val thetaG = ThetaG(julianDay)
        return getLocationForECI_EllipticalEarth(eci, thetaG)
    }

    private fun getLocationForECI_EllipticalEarth(eci: Vector, thetaG: Double): Location {
        val TOLERANCE = 0.001
        val a = EARTH_RADIUS
        val f = EARTH_FLATTENING
        val R = Math.sqrt(eci.x * eci.x + eci.y * eci.y)
        val e2 = f * (2 - f)
        var phi1 = Math.atan2(eci.z, R)
        while (true) {
            val sinphi = Math.sin(phi1)
            val C = 1 / Math.sqrt(1 - e2 * sinphi * sinphi)
            val phi = Math.atan2(eci.z + a * C * e2 * sinphi, R)
            if (Math.abs(phi - phi1) < TOLERANCE) {
                val lambda = Math.atan2(eci.y, eci.x) - thetaG
                val height = R / Math.cos(phi) - a * C
                return Location(Radian.toDegrees180(phi), Radian.toDegrees180(lambda), height)
            }
            phi1 = phi
        }
    }

    /// <summary>
    /// Calculates the radius of the circle where the elevation is higher than the minimal elevation
    /// </summary>
    /// <param name="altitude">Height of the satellite in km</param>
    /// <param name="elevation">Minimal elevation in degree</param>
    /// <returns>Radius of visibility in km</returns>
    @JvmStatic
    fun getVisibilityRadius(height: Double, elevation: Double): Double {
        // SINUSSATZ --> R / cos(alpha + elevation) = (R+H) / cos(elevation)
        val beta = Radian.fromDegrees(elevation)
        val alpha = Math.acos(Math.cos(beta) * EARTH_RADIUS / (EARTH_RADIUS + height)) - beta
        return alpha * EARTH_RADIUS
    }

    /// <summary>
    /// Move by distance into the direction of the bearing angle
    /// </summary>
    /// <param name="location">Location in degree</param>
    /// <param name="bearing">Bearing angle in degree</param>
    /// <param name="distance">Distance in km</param>
    /// <returns></returns>
    fun calculateNewLocationFromBearingDistance(location: ILocation, bearing: Double, distance: Double): Location {
        val latitude = Radian.fromDegrees(location.latitude)
        val longitude = Radian.fromDegrees(location.longitude)
        val angle = Radian.fromDegrees(bearing)
        val ratio = distance / EARTH_RADIUS
        val newLatitude = Math.asin(Math.sin(latitude) * Math.cos(ratio) + Math.cos(latitude) * Math.sin(ratio) * Math.cos(angle))
        val newLongitude = Math.atan2(Math.sin(angle) * Math.sin(ratio) * Math.cos(latitude), Math.cos(ratio) - Math.sin(latitude) * Math.sin(newLatitude))
        return Location(Radian.toDegrees180(newLatitude), Radian.toDegrees180(longitude + newLongitude), location.altitude)
    }
}