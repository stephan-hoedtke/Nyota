package com.stho.software.nyota.universe;

import com.stho.software.nyota.utilities.Degree;
import com.stho.software.nyota.utilities.Moment;

/**
 * Created by shoedtke on 30.08.2016.
 */
public class Venus extends AbstractPlanet {

    @Override
    public String getName() {
        return "Venus";
    }

    @Override
    public int getImageId() { return com.stho.software.nyota.R.mipmap.venus; }

    @Override
    public int getLargeImageId() { return com.stho.software.nyota.R.drawable.venus; }

    @Override
    public void updateBasics(double d) {

        N =  Degree.normalize(76.6799 + 2.46590E-5 * d);
        i = Degree.normalize(3.3946 + 2.75E-8 * d);
        w =  Degree.normalize(54.8910 + 1.38374E-5 * d);
        a = 0.723330; // (AU)
        e = Degree.normalize(0.006773 - 1.302E-9 * d);
        M =  Degree.normalize(48.0052 + 1.6021302244 * d);
    }

    @Override
    public void calculateMagnitude() {
        magn = -4.34 + 5 * Math.log10(r * R) + 0.013 * FV + 4.2E-7 * Math.pow(FV, 3);
    }

    @Override
    protected AbstractPlanet getPlanetFor(Moment moment) {

        Sun sun = new Sun();
        sun.updateBasics(moment.d);
        sun.updateHeliocentricLatitudeLongitude(moment.d);
        sun.updateGeocentricAscensionDeclination();

        Venus venus = new Venus();
        venus.updateBasics(moment.d);
        venus.updateHeliocentricLatitudeLongitude(moment.d);
        venus.updateGeocentricAscensionDeclination(sun);
        venus.updateAzimuthAltitude(moment);

        return venus;
    }
}
