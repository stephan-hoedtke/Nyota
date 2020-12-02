package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.IMoment
import com.stho.nyota.sky.utilities.Moment

/**
 * Created by shoedtke on 31.08.2016.
 */
class Neptune : AbstractPlanet() {
    override val name: String
        get() = "Neptune"

    override val imageId: Int
        get() = com.stho.nyota.R.mipmap.neptune

    override val largeImageId: Int
        get() = com.stho.nyota.R.drawable.planet_neptune

    override fun updateBasics(d: Double) {
        N = Degree.normalize(131.7806 + 3.0173E-5 * d)
        i = Degree.normalize(1.7700 - 2.55E-7 * d)
        w = Degree.normalize(272.8461 - 6.027E-6 * d)
        a = Degree.normalize(30.05826 + 3.313E-8 * d) //  (AU)
        e = Degree.normalize(0.008606 + 2.15E-9 * d)
        M = Degree.normalize(260.2471 + 0.005995147 * d)
    }

    override fun calculateMagnitude() {
        magn = -6.90 + 5 * Math.log10(mr * R) + 0.001 * FV
    }

    override fun getPlanetFor(moment: IMoment): AbstractPlanet {
        val sun = Sun()
        sun.updateBasics(moment.d)
        sun.updateHeliocentricLatitudeLongitude(moment.d)
        sun.updateGeocentricAscensionDeclination()
        val neptune = Neptune()
        neptune.updateBasics(moment.d)
        neptune.updateHeliocentricLatitudeLongitude(moment.d)
        neptune.updateGeocentricAscensionDeclination(sun)
        neptune.updateAzimuthAltitude(moment)
        return neptune
    }
}