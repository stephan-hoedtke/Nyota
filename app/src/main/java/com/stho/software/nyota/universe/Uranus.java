package com.stho.software.nyota.universe;

import com.stho.software.nyota.utilities.Degree;
import com.stho.software.nyota.utilities.Moment;

/**
 * Created by shoedtke on 31.08.2016.
 */
public class Uranus extends AbstractPlanet {

    @Override
    public String getName() {
        return "Uranus";
    }

    @Override
    public int getImageId() { return com.stho.software.nyota.R.mipmap.uranus; }

    @Override
    public int getLargeImageId() { return com.stho.software.nyota.R.drawable.uranus; }

    @Override
    public void updateBasics(double d) {

        N =  Degree.normalize(74.0005 + 1.3978E-5 * d);
        i = Degree.normalize(0.7733 + 1.9E-8 * d);
        w =  Degree.normalize(96.6612 + 3.0565E-5 * d);
        a = Degree.normalize(19.18171 - 1.55E-8 * d); //  (AU)
        e = Degree.normalize(0.047318 + 7.45E-9 * d);
        M = Degree.normalize(142.5905 + 0.011725806 * d);
    }

    public void applyPerturbations(Jupiter jupiter, Saturn saturn) {

        // Add these terms to the longitude:
        double lon_corr =
                +0.040 * Degree.sin(saturn.M - 2*M + 6)
                +0.035 * Degree.sin(saturn.M - 3*M + 33)
                -0.015 * Degree.sin(jupiter.M - M + 20);

        longitude += lon_corr;
    }

    @Override
    public void calculateMagnitude() {
        magn = -7.15 + 5 * Math.log10(r * R) + 0.001 * FV;
    }

    @Override
    protected AbstractPlanet getPlanetFor(Moment moment) {

        Sun sun = new Sun();
        sun.updateBasics(moment.d);
        sun.updateHeliocentricLatitudeLongitude(moment.d);
        sun.updateGeocentricAscensionDeclination();

        Uranus uranus = new Uranus();
        uranus.updateBasics(moment.d);
        uranus.updateHeliocentricLatitudeLongitude(moment.d);
        uranus.updateGeocentricAscensionDeclination(sun);
        uranus.updateAzimuthAltitude(moment);

        return uranus;
    }
}
