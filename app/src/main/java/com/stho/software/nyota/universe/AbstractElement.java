package com.stho.software.nyota.universe;

import com.stho.software.nyota.utilities.Angle;
import com.stho.software.nyota.utilities.Degree;
import com.stho.software.nyota.utilities.Formatter;
import com.stho.software.nyota.utilities.Hour;
import com.stho.software.nyota.utilities.IconNameValueList;
import com.stho.software.nyota.utilities.IconValueList;
import com.stho.software.nyota.utilities.Moment;
import com.stho.software.nyota.R;
import com.stho.software.nyota.utilities.Topocentric;

/**
 * Created by shoedtke on 31.08.2016.
 */
public abstract class AbstractElement implements IElement {

    public double RA; // right ascension, counted "countersunwise" along the celestial equator, zero at the Vernal Point
    public double Decl; // Declination, positive north of the celestial equator
    public double magn = 1.0; // brightness

    abstract public String getName();
    abstract public int getImageId();

    public int getLargeImageId() { return this.getImageId(); }

    protected Topocentric position; // Azimuth+Altitude for a local observer

    public Topocentric getPosition() { return this.position; }


    // forUTC the position (azimuth and altitude) for a given location and Local Sidereal Time (in angleInHours)
    public void updateAzimuthAltitude(Moment moment) {

        final double HA = Degree.normalizeTo180(15 * moment.LST - RA); // Hour Angle (HA) is usually given in the interval -12 to +12 angleInHours, or -180 to +180 degrees
        final double x = Degree.cos(HA) * Degree.cos(Decl);
        final double y = Degree.sin(HA) * Degree.cos(Decl);
        final double z = Degree.sin(Decl);

        final double latitude = moment.getLocation().getLatitude();
        final double xhor = x * Degree.sin(latitude) - z * Degree.cos(latitude);
        final double yhor = y;
        final double zhor = x * Degree.cos(latitude) + z * Degree.sin(latitude);

        final double azimuth = Degree.arcTan2(yhor, xhor) + 180; // measure from north eastward
        final double altitude = Degree.arcTan2(zhor, Math.sqrt(xhor * xhor + yhor * yhor));

        // This completes our calculation of the local azimuth and altitude.
        // Note that azimuth is 0 at North, 90 deg at East, 180 deg at South and 270 deg at West.
        // Altitude is of course 0 at the (mathematical) horizon, 90 deg at zenith, and negative below the horizon.

        position = new Topocentric(azimuth, altitude);
    }

    public IconValueList getBasics(Moment moment) {
        IconValueList basics = new IconValueList();

        basics.add(R.drawable.horizontal, Angle.toString(position.azimuth, Angle.AngleType.AZIMUTH) + Formatter.SPACE + Angle.toString(position.altitude, Angle.AngleType.ALTITUDE));

        return basics;
    }

    public IconNameValueList getDetails(Moment moment) {
        IconNameValueList details = new IconNameValueList();

        details.add(R.drawable.horizontal, "Azimuth", Hour.fromDegree(position.azimuth));
        details.add(R.drawable.horizontal, "Altitude", Degree.fromDegree(position.altitude));

        details.add(R.drawable.equatorial, "Ascension", Hour.fromDegree(RA));
        details.add(R.drawable.equatorial, "Declination", Degree.fromDegree(Decl));

        return details;
    }

    public int getVisibility() {
        if (position != null && (position.altitude > 5 && position.altitude < 175))
            return R.drawable.visible;

        if (position != null && (position.altitude < 0 || position.altitude > 180))
            return R.drawable.invisible;

        return R.drawable.dizzy;
    }
}
