package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.IMoment
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.sky.utilities.UTC

/**
 * Created by shoedtke on 16.09.2016.
 */
interface ISolarSystem {
    /**
     * Calculates the geocentric coordinates of all elements: RA (right ascension) Decl (declination)
     * @param utc current time
     */
    fun update(utc: UTC)

    /**
     * Calculates the coordinates of all elements, azimuth and altitude, with respect to the observer
     * @param moment current time and observer position
     */
    fun updateAzimuthAltitude(moment: IMoment)

    /**
     * Calculates the phase, the phase angle and the set and rise times
     * @param moment current time and observer position
     */
    fun updatePhase(moment: IMoment)
}