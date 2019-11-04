package com.stho.software.nyota.utilities;

import android.location.LocationManager;

import com.stho.software.nyota.universe.Earth;

import java.util.TimeZone;

/*
    Remark: for the timeZone we can use the timezone ids
    public static Set<String> getAvailableIDs (TimeZone.SystemTimeZoneType zoneType,
                String region,
                Integer rawOffset)
 */
public class City implements ILocation {

    private static int idCounter = 1;

    static final String CITY_DELIMITER = "§";
    static final String CITY_FIELD_DELIMITER = ";";
    static final String SPACE = " ";
    private static final int CITY_IS_SELECTED = 0x01;
    private static final int CITY_IS_AUTOMATIC = 0x02;

    private int id;
    private String name;
    private Location location;
    private TimeZone timeZone;
    private int flags = 0;

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name.replace(CITY_DELIMITER, SPACE).replace(CITY_FIELD_DELIMITER, SPACE);
    }

    public String getName() {
        return name;
    }

    public String getNameEx() {
        return isAutomatic() ? name + "*" : name;
    }

    void select() {
        flags |= CITY_IS_SELECTED;
    }

    void unselect() {

        flags &= ~CITY_IS_SELECTED;
    }

    public boolean isSelected() {
        return ((flags & CITY_IS_SELECTED) == CITY_IS_SELECTED);
    }

    public void setAutomatic(boolean automatic) {
        if (automatic)
            flags |= CITY_IS_AUTOMATIC;
        else
            flags &= ~CITY_IS_AUTOMATIC;
    }

    public boolean isAutomatic() {
        return ((flags & CITY_IS_AUTOMATIC) == CITY_IS_AUTOMATIC);
    }

    public void setLocation(double latitude, double longitude, double altitude) {
        this.location = new Location(latitude, longitude, altitude);
    }

    public void setLocation(android.location.Location location) {
        this.location = new Location(location.getLatitude(), location.getLongitude(), location.getAltitude());
    }

    public Location getLocation() {
        return location;
    }

    void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = TimeZone.getTimeZone(timeZone);
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    private City(int id, String name, Location location, TimeZone timeZone, int flags) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.timeZone = timeZone;
        this.flags = flags;
        updateIdCounter(id);
    }

    private City(int id, String name, double latitude, double longitude, double altitude, TimeZone timeZone, int flags) {
        this(id, name, new Location(latitude, longitude, altitude), timeZone, flags);
    }

    private void updateIdCounter(int id) {
        if (idCounter < id)
            idCounter = id;
    }

    /*
        altitude in km
     */
    public City(String name, double latitude, double longitude, double altitude, String timeZone) {
        this((++idCounter), name, latitude, longitude, altitude, TimeZone.getTimeZone(timeZone), 0);
    }

    /*
        altitude in km
     */
    City(String name, double latitude, double longitude, double altitude, TimeZone timeZone) {
        this((++idCounter), name, latitude, longitude, altitude, timeZone, 0);
    }

    private static final double ONE_METER_IN_KM = 0.001;
    private static final double TEN_METERS_IN_KM = 0.01;
    private static final double BERLIN_LATITUDE = 52.6425;
    private static final double BERLIN_LONGITUDE = 13.4925;
    private static final double BERLIN_ALTITUDE = 0.1037; // in km
    private static final String CENTRAL_EUROPEAN_TIMEZONE = "CET";

    City(String name, android.location.Location location, String timeZone) {
        this(name, location.getLatitude(), location.getLongitude(), ONE_METER_IN_KM * location.getAltitude(), timeZone);
    }

    City(String name, android.location.Location location, TimeZone timeZone) {
        this(name, location.getLatitude(), location.getLongitude(), ONE_METER_IN_KM * location.getAltitude(), timeZone);
    }

    public City(String name, ILocation location, TimeZone timeZone) {
        this(name, location.getLatitude(), location.getLongitude(), location.getAltitude(), timeZone);
    }


    @Override
    public String toString() {
        return name +
                SPACE +
                getLocation().toString() +
                SPACE +
                timeZone.getID();
    }

    public String serialize() {
        return id +
                CITY_FIELD_DELIMITER +
                name +
                CITY_FIELD_DELIMITER +
                Formatter.df3.format(getLatitude()) +
                CITY_FIELD_DELIMITER +
                Formatter.df3.format(getLongitude()) +
                CITY_FIELD_DELIMITER +
                Formatter.df3.format(getAltitude()) +
                CITY_FIELD_DELIMITER +
                timeZone.getID() +
                CITY_FIELD_DELIMITER +
                flags;
    }

    public static City deserialize(String str) {
        try {
            if (str != null) {
                String[] parts = str.split(CITY_FIELD_DELIMITER);
                if (parts.length == 7) {
                    int id = Formatter.parseInt(parts[0]);
                    String name = Formatter.parseString(parts[1]);
                    double latitude = Formatter.parseDouble(parts[2]);
                    double longitude = Formatter.parseDouble(parts[3]);
                    double altitude = Formatter.parseDouble(parts[4]);
                    TimeZone timeZone = Formatter.parseTimeZone(parts[5]);
                    int flags = Formatter.parseInt(parts[6]);
                    return new City(id, name, latitude, longitude, altitude, timeZone, flags);
                }
            }
            return null;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public static City createDefault(LocationManager locationManager) {
        android.location.Location location = getCurrentLocation(locationManager);
        return createDefault(location);
    }

    public static City createNewDefaultBerlin() {
        return new City("Berlin", BERLIN_LATITUDE, BERLIN_LONGITUDE, BERLIN_ALTITUDE, CENTRAL_EUROPEAN_TIMEZONE);
    }

    private static City createDefault(android.location.Location location) {
        if (location == null) {
            location = getBerlinAsLocation();
        }
        City city = new City(0, "You", new Location(location), TimeZone.getDefault(), CITY_IS_AUTOMATIC);
        city.setAutomatic(true);
        return city;
    }

    private static android.location.Location getBerlinAsLocation() {
        android.location.Location location = new android.location.Location("Berlin");
        location.setLatitude(BERLIN_LATITUDE);
        location.setLongitude(BERLIN_LONGITUDE);
        location.setAltitude(BERLIN_ALTITUDE);
        return location;
    }

    static android.location.Location getCurrentLocation(LocationManager locationManager) {
        android.location.Location location = null;
        try {
            if (locationManager != null)
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        catch (SecurityException ex) {
            // ignore
        }
        return location;
    }

    boolean isNear(ILocation otherLocation) {
        return (getDistanceTo(otherLocation) < TEN_METERS_IN_KM);
    }

    public boolean isNear(android.location.Location otherLocation) {
        return (getDistanceTo(otherLocation) < TEN_METERS_IN_KM);
    }

    /*
        distance in km on the surface of the earth ignoring the altitude
     */
    private double getDistanceTo(ILocation otherLocation) {
        return getDistanceTo(getLatitude(), getLongitude(), otherLocation.getLatitude(), otherLocation.getLongitude());
    }

    /**
     * Distance in km on the surface of the earth ignoring the altitude
     * @param otherLocation
     * @return
     */
    private double getDistanceTo(android.location.Location otherLocation) {
        return getDistanceTo(getLatitude(), getLongitude(), otherLocation.getLatitude(), otherLocation.getLongitude());
    }

    private static double getDistanceTo(double latitude, double longitude, double otherLatitude, double otherLongitude) {
        double sin_delta_phi = Math.sin(Radian.fromDegrees(otherLatitude - latitude) / 2);
        double sin_delta_lambda =  Math.sin(Radian.fromDegrees(otherLongitude - longitude) /2);
        double a = sin_delta_phi * sin_delta_phi + Math.cos(Radian.fromDegrees(latitude)) * Math.cos(Radian.fromDegrees(otherLatitude)) * sin_delta_lambda * sin_delta_lambda;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Earth.RADIUS * c;
    }

    @Override
    public double getLatitude() {
        return location.getLatitude();
    }

    @Override
    public double getLongitude() {
        return location.getLongitude();
    }

    @Override
    public double getAltitude() {
        return location.getAltitude();
    }

    public void copyFrom(City city) {
        name = city.name;
        location = city.location;
        timeZone = city.timeZone;
        flags = city.flags;
    }

    boolean matches(City city) {
        if (this.isAutomatic() == city.isAutomatic()) {

            if (this.isAutomatic() && city.isAutomatic())
                return true;

            if (this.getName().compareToIgnoreCase(city.getName()) == 0)
                return true;

            //noinspection RedundantIfStatement
            if (this.isNear(city.getLocation()))
                return true;

        }
        return false;
    }

    public static String[] getTimeZones() {
        return TimeZone.getAvailableIDs();
    }
}
