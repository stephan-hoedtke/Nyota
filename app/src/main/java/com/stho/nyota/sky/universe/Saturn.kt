package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.IMoment

/**
 * Created by shoedtke on 30.08.2016.
 */
class Saturn : AbstractPlanet() {
    private var ir = 0.0
    private var Nr = 0.0

    override val name: String
        get() = "Saturn"

    override val imageId: Int
        get() = com.stho.nyota.R.mipmap.saturn

    override val largeImageId: Int
        get() = com.stho.nyota.R.drawable.planet_saturn

    override fun updateBasics(d: Double) {
        N = Degree.normalize(113.6634 + 2.38980E-5 * d)
        i = Degree.normalize(2.4886 - 1.081E-7 * d)
        w = Degree.normalize(339.3939 + 2.97661E-5 * d)
        a = 9.55475 //  (AU)
        e = Degree.normalize(0.055546 - 9.499E-9 * d)
        M = Degree.normalize(316.9670 + 0.0334442282 * d)
        ir = 28.06
        Nr = Degree.normalize(169.51 + 3.82E-5 * d) // Used for magnitude
    }

    fun applyPerturbations(jupiter: Jupiter) {

        // Add these terms to the longitude:
        val lon_corr = (+0.812 * Degree.sin(2 * jupiter.M - 5 * M - 67.6)
                - 0.229 * Degree.cos(2 * jupiter.M - 4 * M - 2)) + 0.119 * Degree.sin(jupiter.M - 2 * M - 3) + 0.046 * Degree.sin(2 * jupiter.M - 6 * M - 69) + 0.014 * Degree.sin(jupiter.M - 3 * M + 32)

        // For Saturn: also addHours these terms to the latitude:
        val lat_corr = (-0.020 * Degree.cos(2 * jupiter.M - 4 * M - 2)
                + 0.018 * Degree.sin(2 * jupiter.M - 6 * M - 49))
        longitude += lon_corr
        latitude += lat_corr
    }

    override fun calculateMagnitude() {
        val B = Degree.arcSin(Degree.sin(latitude) * Degree.cos(ir) - Degree.cos(latitude) * Degree.sin(ir) * Degree.sin(longitude - Nr))
        val ringMagn = -2.6 * Degree.sin(Math.abs(B)) + 1.2 * Math.pow(Degree.sin(B), 2.0)
        magn = -9.0 + 5 * Math.log10(mr * R) + 0.044 * FV + ringMagn
    }

    override fun getPlanetFor(moment: IMoment): AbstractPlanet {
        val sun = Sun()
        sun.updateBasics(moment.d)
        sun.updateHeliocentricLatitudeLongitude(moment.d)
        sun.updateGeocentricAscensionDeclination()
        val jupiter = Jupiter()
        jupiter.updateBasics(moment.d)
        jupiter.updateHeliocentricLatitudeLongitude(moment.d)
        val saturn = Saturn()
        saturn.updateBasics(moment.d)
        saturn.updateHeliocentricLatitudeLongitude(moment.d)
        saturn.applyPerturbations(jupiter)
        saturn.updateGeocentricAscensionDeclination(sun)
        saturn.updateAzimuthAltitude(moment)
        return saturn
    }
}