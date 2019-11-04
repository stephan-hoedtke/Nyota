package com.stho.software.nyota.universe;

import com.stho.software.nyota.R;
import com.stho.software.nyota.universe.AbstractSolarSystem;
import com.stho.software.nyota.universe.Satellite;
import com.stho.software.nyota.universe.SatelliteAlgorithms;
import com.stho.software.nyota.universe.Sun;
import com.stho.software.nyota.universe.Algorithms;
import com.stho.software.nyota.utilities.City;
import com.stho.software.nyota.utilities.Formatter;
import com.stho.software.nyota.utilities.Location;
import com.stho.software.nyota.utilities.Moment;
import com.stho.software.nyota.utilities.Topocentric;
import com.stho.software.nyota.utilities.UTC;
import com.stho.software.nyota.utilities.Vector;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by shoedtke on 31.03.2017.
 */

public class SatellitePreview {

    private final static double MINIMAL_VISIBILITY_ELONGATION = 20;

    public static class PreviewPoint extends GeoPoint {

        private final int level;
        private final UTC utc;
        private final double distance;
        private final double altitude;
        private final double sunHeightObserver;
        private final double sunHeightSatellite;
        private final boolean visible;

        PreviewPoint(double aLatitude, double aLongitude, int level, UTC utc, double distance, double altitude, double sunHeightObserver, double sunHeightSatellite, boolean visible) {
            super(aLatitude, aLongitude);
            this.level = level;
            this.utc = utc;
            this.distance = distance;
            this.altitude = altitude;
            this.sunHeightObserver = sunHeightObserver;
            this.sunHeightSatellite = sunHeightSatellite;
            this.visible = visible;
        }

        UTC getUTC() {
            return utc;
        }

        public String getTitle() {
            return utc.toString()
                    + "\nAltitude from your location: " + Formatter.df0.format(altitude) + "°"
                    + "\nSun at your location: " + Formatter.df0.format(sunHeightObserver) + "°"
                    + "\nSun from satellite: " + Formatter.df0.format(sunHeightSatellite) + "°"
                    + "\nDistance: " + Formatter.df0.format(distance) + " km";
        }

        public int getResourceId() {

            if (visible)
                return R.drawable.visible;

            switch (level) {
                case 0:
                    return R.drawable.p1;
                case 1:
                    return R.drawable.p2;
                case 2:
                    return R.drawable.p3;
                case 3:
                    return R.drawable.p4;
                case 4:
                    return R.drawable.p5;
                case 5:
                    return R.drawable.p6;
                case 6:
                    return R.drawable.p7;
                case 7:
                    return R.drawable.p8;
                case 8:
                    return R.drawable.p9;
                case 9:
                    return R.drawable.p10;
                case 10:
                    return R.drawable.p11;
                case 11:
                    return R.drawable.p12;
                default:
                    return R.drawable.p13;
            }
        }
    }


    public static List<PreviewPoint> getPreviewPoints(Satellite satellite, Location observer) {
        List<PreviewPoint> points = new ArrayList<>();
        final int COUNT = 300;
        final int LIMIT = 4000;
        final double PREVIEW_INTERVAL_IN_HOURS = 0.01666666666666666666666666666667;
        UTC utc = UTC.forNow();

        boolean visible = false;

        for (int i = 0; i < LIMIT; i++, utc = utc.addHours(PREVIEW_INTERVAL_IN_HOURS)) {
            int level = (13 * i) / COUNT;
            PreviewPoint point = getPreviewPoint(satellite, observer, utc, level);
            if (i < COUNT || point.visible) {
                points.add(point);
                visible |= point.visible;
            } else {
                if (i > COUNT && visible)
                    break;
            }
        }

        return points;
    }


    private static PreviewPoint getPreviewPoint(Satellite satellite, Location observer, UTC utc, int level) {

        double julianDay = utc.getJulianDay();
        Vector position = new Vector();
        SatelliteAlgorithms.GetSatellite(satellite.tle, julianDay, position, null);
        Location location = Algorithms.getLocationForECI(position, julianDay);
        Topocentric topocentric = Algorithms.getTopocentricFromPosition(observer, julianDay, position);

        Sun sunForObserver = getSunFor(utc, observer);
        Sun sunForSatellite = getSunFor(utc, location);

        boolean isDark = sunForObserver.isDark();
        boolean isReflecting = sunForSatellite.isVisibleAt(satellite.location.getAltitude());

        boolean visible = (isDark && isReflecting && topocentric.altitude > MINIMAL_VISIBILITY_ELONGATION);

        return new PreviewPoint(location.getLatitude(), location.getLongitude(), level, utc, topocentric.distance, topocentric.altitude, sunForObserver.position.altitude, sunForSatellite.position.altitude, visible);
    }


    private static Sun getSunFor(UTC utc, Location location) {
        City city = new City("", location, TimeZone.getDefault());
        return AbstractSolarSystem.getSunFor(Moment.forUTC(city, utc));
    }

    public static List<GeoPoint> getVisibilityPoints(Satellite satellite) {
        List<GeoPoint> points = new ArrayList<>();

        double radius = Algorithms.getVisibilityRadius(satellite.location.getAltitude(), MINIMAL_VISIBILITY_ELONGATION);
        double alpha = 360 / 90;

        for (double bearing = 0; bearing < 360; bearing += alpha) {
            Location location = Algorithms.calculateNewLocationFromBearingDistance(satellite.location, bearing, radius);
            points.add(new GeoPoint(location.getLatitude(), location.getLongitude()));
        }

        return points;
    }

}
