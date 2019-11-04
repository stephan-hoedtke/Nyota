package com.stho.software.nyota.universe;

import com.stho.software.nyota.utilities.Degree;
import com.stho.software.nyota.utilities.Moment;

/**
 * Created by shoedtke on 30.08.2016.
 */
public class Mercury extends AbstractPlanet {

    @Override
    public String getName() {
        return "Mercury";
    }

    @Override
    public int getImageId() { return com.stho.software.nyota.R.mipmap.mercury; }

    @Override
    public int getLargeImageId() { return com.stho.software.nyota.R.drawable.mercury; }

    @Override
    public void updateBasics(double d) {

        N = Degree.normalize(48.3313 + 3.24587E-5 * d);
        i = Degree.normalize(7.0047 + 5.00E-8 * d);
        w = Degree.normalize(29.1241 + 1.01444E-5 * d);
        a = Degree.normalize(0.387098); //  (AU)
        e = Degree.normalize(0.205635 + 5.59E-10 * d);
        M = Degree.normalize(168.6562 + 4.0923344368 * d);
    }

    @Override
    public void calculateMagnitude() {
        magn = -0.36 + 5 * Math.log10(r * R) + 0.027 * FV + 2.2E-13 * Math.pow(FV, 6);
    }

    @Override
    protected AbstractPlanet getPlanetFor(Moment moment) {

        Sun sun = new Sun();
        sun.updateBasics(moment.d);
        sun.updateHeliocentricLatitudeLongitude(moment.d);
        sun.updateGeocentricAscensionDeclination();

        Mercury mercury = new Mercury();
        mercury.updateBasics(moment.d);
        mercury.updateHeliocentricLatitudeLongitude(moment.d);
        mercury.updateGeocentricAscensionDeclination(sun);
        mercury.updateAzimuthAltitude(moment);

        return mercury;
    }
}
