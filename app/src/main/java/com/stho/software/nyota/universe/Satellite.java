package com.stho.software.nyota.universe;

import com.stho.software.nyota.utilities.Moment;
import com.stho.software.nyota.R;
import com.stho.software.nyota.utilities.Vector;

/**
 * Created by shoedtke on 20.01.2017.
 */
public class Satellite extends AbstractSatellite {

    private double julianDay;

    @Override
    public int getImageId() { return R.drawable.iss; }


    public Satellite(String name, String elements) {
        this.name = name;
        this.tle = new TLE();
        this.tle.deserialize(elements);
    }


    public void update(Moment moment) {
        double julianDay = moment.getUTC().getJulianDay();
        this.update(julianDay);
        Vector eci = Algorithms.getECI(moment.getLocation(), julianDay);
        Vector relative = position.minus(eci);
        this.topocentric = Algorithms.getTopocentricFromPosition(moment, position);
    }

    @Override
    public void update(double julianDay) {
        SatelliteAlgorithms.GetSatellite(tle, julianDay, position, velocity);
        this.location = Algorithms.getLocationForECI(position, julianDay);
    }
}

