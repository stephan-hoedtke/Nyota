package com.stho.software.nyota.utilities;

/**
 * GEO-Location with latitude in degrees, longitude in degrees, altitude in km
 */
public class Location implements ILocation {

    private final double latitude; // in degrees
    private final double longitude; // in degrees
    private final double altitude; // in km

    private final static double ONE_METER_IN_KM = 0.001;

    /**
     * Define a new GEO-location. Altitude is set to 0.
     * @param latitude in degrees
     * @param longitude in degrees.
     */
    Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = 0;
    }

    /**
     * Define a new GEO-location.
     * @param latitude in degrees
     * @param longitude in degrees
     * @param altitude in km
     */
    public Location(double latitude, double longitude, double altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    Location(android.location.Location location) {
        this(location.getLatitude(), location.getLongitude(), ONE_METER_IN_KM * location.getAltitude());
    }

    @Override
    public String toString() {
        return Location.toString(getLatitude(), getLongitude());
    }

    public static String toString(android.location.Location location) {
        return Location.toString(location.getLatitude(), location.getLongitude());
    }

    public static String toString(double latitude, double longitude) {
        return Angle.toString(longitude, Angle.AngleType.LONGITUDE) + Formatter.SPACE + Angle.toString(latitude, Angle.AngleType.LATITUDE);
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    @Override
    public double getAltitude() {
        return altitude;
    }
}

