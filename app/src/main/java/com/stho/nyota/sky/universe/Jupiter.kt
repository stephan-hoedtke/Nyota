package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.Degree.Companion.cosines
import com.stho.nyota.sky.utilities.Degree.Companion.sinus
import com.stho.nyota.sky.utilities.IMoment

/**
 * Created by shoedtke on 30.08.2016.
 */
class Jupiter : AbstractPlanet() {
    override val name: String
        get() = "Jupiter"

    override val imageId: Int
        get() = com.stho.nyota.R.mipmap.jupiter

    override val largeImageId: Int
        get() = com.stho.nyota.R.drawable.planet_jupiter

    override fun updateBasics(d: Double) {
        N = Degree.normalize(100.4542 + 2.76854E-5 * d)
        i = Degree.normalize(1.3030 - 1.557E-7 * d)
        w = Degree.normalize(273.8777 + 1.64505E-5 * d)
        a = 5.20256 //  (AU)
        e = Degree.normalize(0.048498 + 4.469E-9 * d)
        M = Degree.normalize(19.8950 + 0.0830853001 * d)
    }

    fun applyPerturbations(saturn: Saturn) {

        // Add these terms to the longitude
        val lon_corr = ((-0.332 * sinus(2 * M - 5 * saturn.M - 67.6)
                - 0.056 * sinus(2 * M - 2 * saturn.M + 21)
                + 0.042 * sinus(3 * M - 5 * saturn.M + 21)
                - 0.036 * sinus(M - 2 * saturn.M)
                ) + 0.022 * cosines(M - saturn.M) + 0.023 * sinus(2 * M - 3 * saturn.M + 52)
                - 0.016 * sinus(M - 5 * saturn.M - 69))
        longitude += lon_corr
    }

    override fun calculateMagnitude() {
        magn = -9.25 + 5 * Math.log10(mr * R) + 0.014 * FV
    }

    override fun getPlanetFor(moment: IMoment): AbstractPlanet {
        val sun = Sun()
        sun.updateBasics(moment.d)
        sun.updateHeliocentricLatitudeLongitude(moment.d)
        sun.updateGeocentricAscensionDeclination()
        val saturn = Saturn()
        saturn.updateBasics(moment.d)
        saturn.updateHeliocentricLatitudeLongitude(moment.d)
        val jupiter = Jupiter()
        jupiter.updateBasics(moment.d)
        jupiter.updateHeliocentricLatitudeLongitude(moment.d)
        jupiter.applyPerturbations(saturn)
        jupiter.updateGeocentricAscensionDeclination(sun)
        jupiter.updateAzimuthAltitude(moment)
        return jupiter
    }
}