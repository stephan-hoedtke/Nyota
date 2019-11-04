package com.stho.software.nyota.universe;

import com.stho.software.nyota.utilities.Moment;
import com.stho.software.nyota.utilities.UTC;

/**
 * Created by shoedtke on 16.09.2016.
 */
public abstract class AbstractSolarSystem implements ISolarSystem {

    protected UTC utc;

    // TODO: public final or abstact !
    public AbstractSolarSystemElement[] elements;

    public Sun sun;
    public Moon moon;
    public Mercury mercury;
    public Venus venus;
    public Mars mars;
    public Jupiter jupiter;
    public Saturn saturn;
    public Uranus uranus;
    public Neptune neptune;


    /**
     * calculates the geocentric coordinates of the celestial object: RA (right ascension) Decl (declination)
     * @param utc the current time
     */
    @Override
    public void update(UTC utc) {

        this.utc = utc;

        double d = utc.getDayNumber();

        if (sun != null) {
            sun.updateBasics(d);
            sun.updateHeliocentricLatitudeLongitude(d);
            sun.updateGeocentricAscensionDeclination();
        }

        for (AbstractSolarSystemElement element : elements) {
            if (element.isMoon() || element.isPlanet()) {
                element.updateBasics(d);
                element.updateHeliocentricLatitudeLongitude(d);
            }
        }

        if (moon != null && sun != null)
            moon.applyPerturbations(sun);

        if (jupiter != null && saturn != null) {
            jupiter.applyPerturbations(saturn);
            saturn.applyPerturbations(jupiter);
        }

        if (uranus != null && jupiter != null && saturn != null)
            uranus.applyPerturbations(jupiter, saturn);

        for (AbstractSolarSystemElement element : elements) {
            if (element.isMoon() || element.isPlanet()) {
                element.updateGeocentricAscensionDeclination(sun);
            }
        }
    }

    @Override
    public void updateAzimuthAltitude(Moment moment) {

        if (moon != null && sun != null)
            moon.applyTopocentricCorrection(moment);

        for (AbstractSolarSystemElement element : elements)
            element.updateAzimuthAltitude(moment);
    }

    @Override
    public void updatePhase(Moment moment) {
        if (sun != null) {
            sun.calculateSetRiseTimes(moment);

            if (moon != null) {
                moon.calculatePhase(sun);
                moon.calculateParallacticAngle(moment);
                moon.calculateMagnitude();
                moon.calculateSetRiseTimes(moment);
                moon.calculateShadows(sun);
            }

            for (AbstractSolarSystemElement element: this.elements) {
                if (element.isPlanet()) {
                    AbstractPlanet planet = (AbstractPlanet)element;
                    planet.calculatePhase(sun);
                    planet.calculateMagnitude();
                    planet.calculateParallacticAngle(moment);
                    planet.calculateSetRiseTimes(moment);
                }
            }
        }
    }

    /**
     * Calcuate geocentric position and horizontal position of the sun for the given moment
     * @param moment as UTC and observer location
     * @return Sun
     */
    public static Sun getSunFor(Moment moment) {
        Sun sun = new Sun();

        double d = moment.getUTC().getDayNumber();

        sun.updateBasics(d);
        sun.updateHeliocentricLatitudeLongitude(d);
        sun.updateGeocentricAscensionDeclination();
        sun.updateAzimuthAltitude(moment);

        return sun;
    }
}

