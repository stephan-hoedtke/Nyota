package com.stho.software.nyota.universe;

import com.stho.software.nyota.utilities.Angle;
import com.stho.software.nyota.utilities.Degree;
import com.stho.software.nyota.utilities.Formatter;
import com.stho.software.nyota.utilities.Hour;
import com.stho.software.nyota.utilities.IconNameValueList;
import com.stho.software.nyota.utilities.IconValueList;
import com.stho.software.nyota.utilities.Location;
import com.stho.software.nyota.utilities.Moment;
import com.stho.software.nyota.R;
import com.stho.software.nyota.utilities.Topocentric;
import com.stho.software.nyota.utilities.Vector;

/**
 * Created by shoedtke on 24.01.2017.
 */

public abstract class AbstractSatellite implements IElement {

    protected TLE tle;
    protected String name;
    protected Topocentric topocentric;
    protected Vector position = new Vector();
    protected Vector velocity = new Vector();
    protected Location location = new Location(0, 0, 0);

    public TLE getTLE() {
        return this.tle;
    }
    
    @Override
    public String getName() { return this.name; }

    abstract public int getImageId();

    @Override
    public int getLargeImageId() { return this.getImageId(); }

    abstract public void update(double julianDay);

    @Override
    public Topocentric getPosition() {
        return this.topocentric;
    }

    public Location getLocation() {
        return this.location;
    }

    /**
     * Speed in km/h
     * @return
     */
    public double getSpeed() { return 60 * this.velocity.getLength(); }


    public IconValueList getBasics(Moment moment) {
        IconValueList basics = new IconValueList();

        basics.add(R.drawable.horizontal, Angle.toString(topocentric.azimuth, Angle.AngleType.AZIMUTH) + Formatter.SPACE + Angle.toString(topocentric.altitude, Angle.AngleType.ALTITUDE));

        return basics;
    }

    public IconNameValueList getDetails(Moment moment) {
        IconNameValueList details = new IconNameValueList();

        details.add(R.drawable.horizontal, "Azimuth", Hour.fromDegree(topocentric.azimuth));
        details.add(R.drawable.horizontal, "Altitude", Degree.fromDegree(topocentric.altitude));
        details.add(R.drawable.equatorial, "Latitude", Degree.fromDegree(location.getLatitude()));
        details.add(R.drawable.equatorial, "Longitude", Degree.fromDegree(location.getLongitude()));
        details.add(R.drawable.equatorial, "Height", Formatter.df0.format(location.getAltitude()) + " km");
        details.add(R.drawable.distance, "Distance", Formatter.df0.format(topocentric.distance) + " km");
        details.add(R.drawable.star, "Speed", Formatter.df0.format(this.getSpeed()) + " km/h");
        details.add(R.drawable.time, "TLE", Formatter.date.format(this.tle.getDate()));

        return details;
    }


    public int getVisibility() {
        if (position != null && (topocentric.altitude > 5 && topocentric.altitude < 175))
            return R.drawable.visible;

        if (position != null && (topocentric.altitude < 0 || topocentric.altitude > 180))
            return R.drawable.invisible;

        return R.drawable.dizzy;
    }
}
