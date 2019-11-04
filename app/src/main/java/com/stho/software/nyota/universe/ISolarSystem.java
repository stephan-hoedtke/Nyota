package com.stho.software.nyota.universe;

import com.stho.software.nyota.utilities.Moment;
import com.stho.software.nyota.utilities.UTC;

/**
 * Created by shoedtke on 16.09.2016.
 */
public interface ISolarSystem {

    /**
     * Calculates the geocentric coordinates of all elements: RA (right ascension) Decl (declination)
     * @param utc current time
     */
    void update(UTC utc);

    /**
     * Calculates the coordinates of all elements, azimuth and altitude, with respect to the observer
     * @param moment current time and observer position
     */
    void updateAzimuthAltitude(Moment moment);

    /**
     * Calculates the phase, the phase angle and the set and rise times
     * @param moment current time and observer position
     */
    void updatePhase(Moment moment);
}
