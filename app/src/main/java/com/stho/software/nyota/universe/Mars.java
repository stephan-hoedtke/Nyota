package com.stho.software.nyota.universe;

import com.stho.software.nyota.utilities.Degree;
import com.stho.software.nyota.utilities.Moment;

/**
 * Created by shoedtke on 30.08.2016.
 */
public class Mars extends AbstractPlanet {

    @Override
    public String getName() {
        return "Mars";
    }

    @Override
    public int getImageId() { return com.stho.software.nyota.R.mipmap.mars; }

    @Override
    public int getLargeImageId() { return com.stho.software.nyota.R.drawable.mars; }

    @Override
    public void updateBasics(double d) {

        N = Degree.normalize(49.5574 + 2.11081E-5 * d);
        i = Degree.normalize(1.8497 - 1.78E-8 * d);
        w = Degree.normalize(286.5016 + 2.92961E-5 * d);
        a = Degree.normalize(1.523688); //  (AU)
        e = Degree.normalize(0.093405 + 2.516E-9 * d);
        M = Degree.normalize(18.6021 + 0.5240207766 * d);
    }

    @Override
    public void calculateMagnitude() {
        magn = -1.51 + 5 * Math.log10(r * R) + 0.016 * FV;
    }

    @Override
    protected AbstractPlanet getPlanetFor(Moment moment) {

        Sun sun = new Sun();
        sun.updateBasics(moment.d);
        sun.updateHeliocentricLatitudeLongitude(moment.d);
        sun.updateGeocentricAscensionDeclination();

        Mars mars = new Mars();
        mars.updateBasics(moment.d);
        mars.updateHeliocentricLatitudeLongitude(moment.d);
        mars.updateGeocentricAscensionDeclination(sun);
        mars.updateAzimuthAltitude(moment);

        return mars;
    }

}
