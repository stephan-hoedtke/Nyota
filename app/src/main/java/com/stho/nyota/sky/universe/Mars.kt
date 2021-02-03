package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.IMoment
import kotlin.math.log10

/**
 * Created by shoedtke on 30.08.2016.
 */
class Mars : AbstractPlanet("Mars") {

    override val imageId: Int
        get() = com.stho.nyota.R.drawable.planet_mars

    override val largeImageId
        get() = com.stho.nyota.R.drawable.planet_mars

    override fun updateBasics(d: Double) {
        N = Degree.normalize(49.5574 + 2.11081E-5 * d)
        i = Degree.normalize(1.8497 - 1.78E-8 * d)
        w = Degree.normalize(286.5016 + 2.92961E-5 * d)
        a = Degree.normalize(1.523688) //  (AU)
        e = Degree.normalize(0.093405 + 2.516E-9 * d)
        M = Degree.normalize(18.6021 + 0.5240207766 * d)
    }

    override fun calculateMagnitude() {
        magn = -1.51 + 5 * log10(mr * R) + 0.016 * FV
    }

    override fun getPlanetFor(moment: IMoment): AbstractPlanet {
        val sun = Sun()
        sun.updateBasics(moment.d)
        sun.updateHeliocentricLatitudeLongitude(moment.d)
        sun.updateGeocentricAscensionDeclination()
        val mars = Mars()
        mars.updateBasics(moment.d)
        mars.updateHeliocentricLatitudeLongitude(moment.d)
        mars.updateGeocentricAscensionDeclination(sun)
        mars.updateAzimuthAltitude(moment)
        return mars
    }
}