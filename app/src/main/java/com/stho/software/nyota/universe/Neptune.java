package com.stho.software.nyota.universe;

import com.stho.software.nyota.utilities.Degree;
import com.stho.software.nyota.utilities.Moment;

/**
 * Created by shoedtke on 31.08.2016.
 */
public class Neptune extends AbstractPlanet {

    @Override
    public String getName() {
        return "Neptune";
    }

    @Override
    public int getImageId() { return com.stho.software.nyota.R.mipmap.neptune; }

    @Override
    public int getLargeImageId() { return com.stho.software.nyota.R.drawable.neptune; }

    @Override
    public void updateBasics(double d) {

        N = Degree.normalize(131.7806 + 3.0173E-5 * d);
        i = Degree.normalize(1.7700 - 2.55E-7 * d);
        w = Degree.normalize(272.8461 - 6.027E-6 * d);
        a = Degree.normalize(30.05826 + 3.313E-8 * d); //  (AU)
        e = Degree.normalize(0.008606 + 2.15E-9 * d);
        M = Degree.normalize(260.2471 + 0.005995147 * d);
    }

    @Override
    public void calculateMagnitude() {
        magn = -6.90 + 5 * Math.log10(r * R) + 0.001 * FV;
    }

    @Override
    protected AbstractPlanet getPlanetFor(Moment moment) {

        Sun sun = new Sun();
        sun.updateBasics(moment.d);
        sun.updateHeliocentricLatitudeLongitude(moment.d);
        sun.updateGeocentricAscensionDeclination();

        Neptune neptune = new Neptune();
        neptune.updateBasics(moment.d);
        neptune.updateHeliocentricLatitudeLongitude(moment.d);
        neptune.updateGeocentricAscensionDeclination(sun);
        neptune.updateAzimuthAltitude(moment);

        return neptune;
    }
}
