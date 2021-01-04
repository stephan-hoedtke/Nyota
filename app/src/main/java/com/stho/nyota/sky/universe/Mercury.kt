package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.IMoment
import kotlin.math.log10
import kotlin.math.pow

/**
 * Created by shoedtke on 30.08.2016.
 */
class Mercury : AbstractPlanet() {
    override val name: String
        get() = "Mercury"

    override val imageId: Int
        get() = com.stho.nyota.R.mipmap.mercury

    override val largeImageId: Int
        get() = com.stho.nyota.R.drawable.planet_mercury

    override fun updateBasics(d: Double) {
        N = Degree.normalize(48.3313 + 3.24587E-5 * d)
        i = Degree.normalize(7.0047 + 5.00E-8 * d)
        w = Degree.normalize(29.1241 + 1.01444E-5 * d)
        a = Degree.normalize(0.387098) //  (AU)
        e = Degree.normalize(0.205635 + 5.59E-10 * d)
        M = Degree.normalize(168.6562 + 4.0923344368 * d)
    }

    override fun calculateMagnitude() {
        magn = -0.36 + 5 * log10(mr * R) + 0.027 * FV + 2.2E-13 * FV.pow(6.0)
    }

    // TODO: find out, if this calculation is "better" than the calculation above.
    // Computing Apparent Planetary Magnitudes for The Astronomical Almanac
    // https://arxiv.org/ftp/arxiv/papers/1808/1808.01973.pdf
    //      alpha: in degree, illumination phase angle (0 = full view, 90 = half view, ...)
    //      r: planets distance from sun
    //      d: planets distance from earth
    private fun calculateApparentMagnitude(r: Double, d: Double, alpha: Double): Double =
        5 * log10 ( r * d ) + alpha.polynomial(-0.613, 6.3280E-2, -1.6336E-3, +3.3644E-5, -3.4265E-7, 1.6893E-9, -3.0334E-12)


    override fun getPlanetFor(moment: IMoment): AbstractPlanet {
        val sun = Sun()
        sun.updateBasics(moment.d)
        sun.updateHeliocentricLatitudeLongitude(moment.d)
        sun.updateGeocentricAscensionDeclination()
        val mercury = Mercury()
        mercury.updateBasics(moment.d)
        mercury.updateHeliocentricLatitudeLongitude(moment.d)
        mercury.updateGeocentricAscensionDeclination(sun)
        mercury.updateAzimuthAltitude(moment)
        return mercury
    }
}

