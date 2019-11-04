package com.stho.software.nyota.utilities;

import java.io.Serializable;

/**
 * Topocentric, that is, horizontal coordinates, in relation to an observer at the surface of the earth, with azimuth in degrees, altitude in degrees and, if applicable, distance in km.
 */
public class Topocentric implements Serializable {

    public double azimuth; // horizontal coordinates, from North (0°) through East (90°), South (180°), West (270°) and back to North.
    public double altitude; // horizontal coordinates, 0° at horizon, 90° in the zenith
    public double distance; // if applicable, or 0

    public UTC nextRiseTime = null;
    public UTC riseTime = null;
    public UTC setTime = null;
    public UTC prevSetTime = null;
    public UTC inSouth;
    public double culmination;

    /**
     * Create a topocentric position by azimuth in degrees (from North, clockwise), altitude in degrees (from horizon to North)
     * @param azimuth
     * @param altitude
     */
    public Topocentric(double azimuth, double altitude) {
        this.azimuth = azimuth;
        this.altitude = altitude;
        this.distance = 0;
    }

    /**
     * Create a topocentric position by azimuth in degrees (from North, clockwise), altitude in degrees (from horizon to North) and the distance in km
     * @param azimuth
     * @param altitude
     * @param distance
     */
    public Topocentric(double azimuth, double altitude, double distance) {
        this.azimuth = azimuth;
        this.altitude = altitude;
        this.distance = distance;
    }

    @Override
    public String toString() {
        return Angle.toString(azimuth, Angle.AngleType.AZIMUTH) + Formatter.SPACE + Angle.toString(altitude, Angle.AngleType.ALTITUDE);
    }
}
