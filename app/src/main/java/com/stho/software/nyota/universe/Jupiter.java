package com.stho.software.nyota.universe;

import com.stho.software.nyota.utilities.Degree;
import com.stho.software.nyota.utilities.Moment;

/**
 * Created by shoedtke on 30.08.2016.
 */
public class Jupiter extends AbstractPlanet {

    @Override
    public String getName() {
        return "Jupiter";
    }

    @Override
    public int getImageId() { return com.stho.software.nyota.R.mipmap.jupiter; }

    @Override
    public int getLargeImageId() { return com.stho.software.nyota.R.drawable.jupiter; }

    @Override
    public void updateBasics(double d) {

        N = Degree.normalize(100.4542 + 2.76854E-5 * d);
        i = Degree.normalize(1.3030 - 1.557E-7 * d);
        w = Degree.normalize(273.8777 + 1.64505E-5 * d);
        a = 5.20256; //  (AU)
        e = Degree.normalize(0.048498 + 4.469E-9 * d);
        M = Degree.normalize(19.8950 + 0.0830853001 * d);
    }

    public void applyPerturbations(Saturn saturn) {

        // Add these terms to the longitude
        double lon_corr =
                -0.332 * Degree.sin(2*M - 5*saturn.M - 67.6)
                -0.056 * Degree.sin(2*M - 2*saturn.M + 21)
                +0.042 * Degree.sin(3*M - 5*saturn.M + 21)
                -0.036 * Degree.sin(M - 2*saturn.M)
                +0.022 * Degree.cos(M - saturn.M)
                +0.023 * Degree.sin(2*M - 3*saturn.M + 52)
                -0.016 * Degree.sin(M - 5*saturn.M - 69);

        longitude += lon_corr;
    }

    @Override
    public void calculateMagnitude() {
        magn = -9.25 + 5 * Math.log10(r * R) + 0.014 * FV;
    }


    @Override
    protected AbstractPlanet getPlanetFor(Moment moment) {

        Sun sun = new Sun();
        sun.updateBasics(moment.d);
        sun.updateHeliocentricLatitudeLongitude(moment.d);
        sun.updateGeocentricAscensionDeclination();

        Saturn saturn = new Saturn();
        saturn.updateBasics(moment.d);
        saturn.updateHeliocentricLatitudeLongitude(moment.d);

        Jupiter jupiter = new Jupiter();
        jupiter.updateBasics(moment.d);
        jupiter.updateHeliocentricLatitudeLongitude(moment.d);
        jupiter.applyPerturbations(saturn);
        jupiter.updateGeocentricAscensionDeclination(sun);
        jupiter.updateAzimuthAltitude(moment);

        return jupiter;
    }
}

