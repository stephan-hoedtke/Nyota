package com.stho.software.nyota.universe;

import com.stho.software.nyota.utilities.Degree;
import com.stho.software.nyota.utilities.Moment;

/**
 * Created by shoedtke on 30.08.2016.
 */
public class Saturn extends AbstractPlanet {

    private double ir;
    private double Nr;

    @Override
    public String getName() {
        return "Saturn";
    }

    @Override
    public int getImageId() { return com.stho.software.nyota.R.mipmap.saturn; }

    @Override
    public int getLargeImageId() { return com.stho.software.nyota.R.drawable.saturn; }

    @Override
    public void updateBasics(double d) {

        N = Degree.normalize(113.6634 + 2.38980E-5 * d);
        i = Degree.normalize(2.4886 - 1.081E-7 * d);
        w = Degree.normalize(339.3939 + 2.97661E-5 * d);
        a = 9.55475; //  (AU)
        e = Degree.normalize(0.055546 - 9.499E-9 * d);
        M = Degree.normalize(316.9670 + 0.0334442282 * d);

        ir = 28.06;
        Nr = Degree.normalize(169.51 + 3.82E-5 * d); // Used for magnitude
    }

    public void applyPerturbations(Jupiter jupiter) {

        // Add these terms to the longitude:
        double lon_corr =
                +0.812 * Degree.sin(2*jupiter.M - 5*M - 67.6)
                -0.229 * Degree.cos(2*jupiter.M - 4*M - 2)
                +0.119 * Degree.sin(jupiter.M - 2*M - 3)
                +0.046 * Degree.sin(2*jupiter.M - 6*M - 69)
                +0.014 * Degree.sin(jupiter.M - 3*M + 32);

        // For Saturn: also addHours these terms to the latitude:
        double lat_corr =
                -0.020 * Degree.cos(2*jupiter.M - 4*M - 2)
                +0.018 * Degree.sin(2*jupiter.M - 6*M - 49);

        longitude += lon_corr;
        latitude += lat_corr;
    }

    @Override
    public void calculateMagnitude() {
        double B = Degree.arcSin( Degree.sin(latitude) * Degree.cos(ir) - Degree.cos(latitude) * Degree.sin(ir) * Degree.sin(longitude - Nr) );
        double ringMagn = -2.6 * Degree.sin(Math.abs(B)) + 1.2 * Math.pow(Degree.sin(B), 2);
        magn = -9.0 + 5 * Math.log10(r * R) + 0.044 * FV + ringMagn;
    }

    @Override
    protected AbstractPlanet getPlanetFor(Moment moment) {

        Sun sun = new Sun();
        sun.updateBasics(moment.d);
        sun.updateHeliocentricLatitudeLongitude(moment.d);
        sun.updateGeocentricAscensionDeclination();

        Jupiter jupiter = new Jupiter();
        jupiter.updateBasics(moment.d);
        jupiter.updateHeliocentricLatitudeLongitude(moment.d);

        Saturn saturn = new Saturn();
        saturn.updateBasics(moment.d);
        saturn.updateHeliocentricLatitudeLongitude(moment.d);
        saturn.applyPerturbations(jupiter);
        saturn.updateGeocentricAscensionDeclination(sun);
        saturn.updateAzimuthAltitude(moment);

        return saturn;
    }
}
