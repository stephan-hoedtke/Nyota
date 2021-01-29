package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.IMoment
import kotlin.math.log10
import kotlin.math.pow

/**
 * Created by shoedtke on 30.08.2016.
 */
class Venus : AbstractPlanet("Venus") {

    override val imageId: Int
        get() = com.stho.nyota.R.mipmap.venus

    override val largeImageId: Int
        get() = com.stho.nyota.R.drawable.planet_venus

    override fun updateBasics(d: Double) {
        N = Degree.normalize(76.6799 + 2.46590E-5 * d)
        i = Degree.normalize(3.3946 + 2.75E-8 * d)
        w = Degree.normalize(54.8910 + 1.38374E-5 * d)
        a = 0.723330 // (AU)
        e = Degree.normalize(0.006773 - 1.302E-9 * d)
        M = Degree.normalize(48.0052 + 1.6021302244 * d)
    }

    override fun calculateMagnitude() {
        magn = -4.34 + 5 * log10(mr * R) + 0.013 * FV + 4.2E-7 * FV.pow(3.0)
    }

    override fun getPlanetFor(moment: IMoment): AbstractPlanet {
        val sun = Sun()
        sun.updateBasics(moment.d)
        sun.updateHeliocentricLatitudeLongitude(moment.d)
        sun.updateGeocentricAscensionDeclination()
        val venus = Venus()
        venus.updateBasics(moment.d)
        venus.updateHeliocentricLatitudeLongitude(moment.d)
        venus.updateGeocentricAscensionDeclination(sun)
        venus.updateAzimuthAltitude(moment)
        return venus
    }
}