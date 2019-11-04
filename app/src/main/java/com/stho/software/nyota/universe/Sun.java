package com.stho.software.nyota.universe;

import com.stho.software.nyota.utilities.Degree;
import com.stho.software.nyota.utilities.IconNameValueList;
import com.stho.software.nyota.utilities.IconValueList;
import com.stho.software.nyota.utilities.Moment;
import com.stho.software.nyota.utilities.UTC;

/**
 * Created by shoedtke on 30.08.2016.
 */
public class Sun extends AbstractSolarSystemElement {

    final static double RADIUS = 695700; // in km

    @Override
    public boolean isSun() {
        return true;
    }

    @Override
    public String getName() {
        return "Sun";
    }

    @Override
    public int getImageId() { return com.stho.software.nyota.R.mipmap.sun; }

    @Override
    public int getLargeImageId() { return com.stho.software.nyota.R.drawable.sun; }

    @Override
    public void updateBasics(double d) {

        N = 0.0;
        i = 0.0;
        w = Degree.normalize(282.9404 + 4.70935E-5 * d);
        a = 1.0; // AU
        e = 0.016709 - 1.151E-9 * d;
        M = Degree.normalize(356.0470 + 0.9856002585 * d);
    }

    @Override
    public void updateHeliocentricLatitudeLongitude(double d) {

        ecl = 23.4393 - 3.563E-7 * d;

        E = Degree.normalize(M + e * Degree.RADEG * Degree.sin(M) * (1.0 + e * Degree.cos(M)));

        double xv = a * (Degree.cos(E) - e);
        double yv = a * (Math.sqrt(1.0 - e * e) * Degree.sin(E));

        v = Degree.arcTan2(yv, xv);
        r = Math.sqrt(xv * xv + yv * yv);

        // mean longitude
        L = Degree.normalize(M + w);

        // The suns longitude
        longitude = Degree.normalize(v + w);
        latitude = 0;
    }

    public void updateGeocentricAscensionDeclination() {

        // ecliptic rectangular geocentric coordinates
        x = r * Degree.cos(longitude);
        y = r * Degree.sin(longitude);
        z = 0;

        // equatorial rectangular geocentric coordinates
        double xe = x;
        double ye = y * Degree.cos(ecl);
        double ze = y * Degree.sin(ecl);

        // Sun's right ascension and declination
        RA = Degree.normalize(Degree.arcTan2(ye, xe));
        Decl = Degree.normalizeTo180(Degree.arcTan2(ze, Math.sqrt(xe * xe + ye * ye)));

        // Compute the geocentric distance:
        R = Math.sqrt(xe*xe + ye*ye + ze*ze);
    }

    private final double ALTITUDE_SUNSET = -0.833; // Sun's upper limb touches the horizon; atmospheric refraction accounted for, in degree
    private final double ALTITUDE_CIVIL_TWILIGHT = -6;
    private final double ALTITUDE_NAUTICAL_TWILIGHT_ALTITUDE = -12;

    /**
     * Returns true if the sun's upper limb is over the horizon
     * @return
     */
    public boolean isDayLight() {
        return (position.altitude >= ALTITUDE_SUNSET);
    }

    /**
     * Returns true if the sun is below the civil twilight altitude
     * @return
     */
    public boolean isDark() { return (position.altitude < ALTITUDE_CIVIL_TWILIGHT); }

    /**
     * Returns true if the object is not in the shadow of the earth
     * @param height in km
     * @return
     */
    public boolean isVisibleAt(double height) {
        final double angle = Degree.arcCos(Earth.RADIUS / (Earth.RADIUS + height));
        return (position.altitude + angle > 0);
    }

    protected double getHeightFor(Moment moment) {
        Sun sun = getSunFor(moment);
        return sun.getHeight();
    }

    private static Sun getSunFor(Moment moment) {
        Sun sun = getSunFor(moment.getUTC());
        sun.updateAzimuthAltitude(moment);
        return sun;
    }

    // calculate RA and Decl for the sun at this time (independend of the current observer)
    private static Sun getSunFor(UTC utc) {
        final double d = utc.getDayNumber();

        Sun sun = new Sun();
        sun.updateBasics(d);
        sun.updateHeliocentricLatitudeLongitude(d);
        sun.updateGeocentricAscensionDeclination();

        return sun;
    }

    private double getHeight() {
        return position.altitude - H0();
    }

    protected double H0() {
        return ALTITUDE_SUNSET;
    }

    @Override
    public IconValueList getBasics(Moment moment) {
        IconValueList basics = super.getBasics(moment);

        if (position != null && position.riseTime != null)
            basics.add(com.stho.software.nyota.R.drawable.sunrise, position.riseTime, moment.getTimeZone());

        if (position != null && position.setTime != null)
            basics.add(com.stho.software.nyota.R.drawable.sunset, position.setTime, moment.getTimeZone());

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

        details.add(com.stho.software.nyota.R.drawable.star, "In south", position.inSouth, moment.getTimeZone());
        details.add(com.stho.software.nyota.R.drawable.star, "Culmination angle", Degree.fromDegree(position.culmination));

        return details;
    }
}

