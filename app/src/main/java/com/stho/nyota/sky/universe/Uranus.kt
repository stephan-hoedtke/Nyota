package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.Degree.Companion.sinus
import com.stho.nyota.sky.utilities.IMoment

/**
 * Created by shoedtke on 31.08.2016.
 */
class Uranus : AbstractPlanet() {
    override val name: String
        get() = "Uranus"

    override val imageId: Int
        get() = com.stho.nyota.R.mipmap.uranus

    override val largeImageId: Int
        get() = com.stho.nyota.R.drawable.planet_uranus

    override fun updateBasics(d: Double) {
        N = Degree.normalize(74.0005 + 1.3978E-5 * d)
        i = Degree.normalize(0.7733 + 1.9E-8 * d)
        w = Degree.normalize(96.6612 + 3.0565E-5 * d)
        a = Degree.normalize(19.18171 - 1.55E-8 * d) //  (AU)
        e = Degree.normalize(0.047318 + 7.45E-9 * d)
        M = Degree.normalize(142.5905 + 0.011725806 * d)
    }

    fun applyPerturbations(jupiter: Jupiter, saturn: Saturn) {

        // Add these terms to the longitude:
        val lon_corr = (+0.040 * sinus(saturn.M - 2 * M + 6)
                + 0.035 * sinus(saturn.M - 3 * M + 33)
                - 0.015 * sinus(jupiter.M - M + 20))
        longitude += lon_corr
    }

    override fun calculateMagnitude() {
        magn = -7.15 + 5 * Math.log10(mr * R) + 0.001 * FV
    }

    override fun getPlanetFor(moment: IMoment): AbstractPlanet {
        val sun = Sun()
        sun.updateBasics(moment.d)
        sun.updateHeliocentricLatitudeLongitude(moment.d)
        sun.updateGeocentricAscensionDeclination()
        val uranus = Uranus()
        uranus.updateBasics(moment.d)
        uranus.updateHeliocentricLatitudeLongitude(moment.d)
        uranus.updateGeocentricAscensionDeclination(sun)
        uranus.updateAzimuthAltitude(moment)
        return uranus
    }
}