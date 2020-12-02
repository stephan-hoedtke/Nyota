package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.*
import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.Degree.Companion.arcTan2
import com.stho.nyota.sky.utilities.Degree.Companion.cosines
import com.stho.nyota.sky.utilities.Degree.Companion.sinus
import com.stho.nyota.sky.utilities.JulianDay.toUTC
import kotlin.math.abs
import kotlin.math.sign

/**
 * Created by shoedtke on 30.08.2016.
 */
class Moon : AbstractSolarSystemElement() {
    var parallax = 0.0 // lunar parallax
    var diameter = 0.0 // apparent  diameter of the moon
    var prevNewMoon: UTC? = null
    var fullMoon: UTC? = null
    var nextNewMoon: UTC? = null
    var age = 0.0
    var nuclearShadow = 0.0
    var halfShadow = 0.0
    var far = 0.0
    var near = 0.0
    override val isMoon: Boolean
        get() = true

    override val name: String
        get() = "Moon"

    override val imageId: Int
        get() = com.stho.nyota.R.mipmap.moon

    override val largeImageId: Int
        get() = com.stho.nyota.R.drawable.moon

    override fun updateBasics(d: Double) {
        N = Degree.normalize(125.1228 - 0.0529538083 * d)
        i = 5.1454
        w = Degree.normalize(318.0634 + 0.1643573223 * d)
        a = 60.2666 // Earth radius
        e = 0.054900
        M = Degree.normalize(115.3654 + 13.0649929509 * d)
    }

    fun applyPerturbations(sun: Sun) {
        val Ls = sun.M + sun.w // Mean longitude of the Sun (N=0)
        val Lm = M + w + N // Mean longitude of the Moon
        val D = Lm - Ls // Mean elongation of the Moon
        val F = Lm - N // Argument of latitude for the Moon

        // Add these terms to the Moon's longitude (degrees):
        val lon_corr = (((-1.274 * sinus(M - 2 * D) // (the Evection)
                + 0.658 * sinus(2 * D) // (the Variation)
                ) - 0.186 * sinus(sun.M) // (the Yearly Equation)
                - 0.059 * sinus(2 * M - 2 * D) - 0.057 * sinus(M - 2 * D + sun.M)
                ) + 0.053 * sinus(M + 2 * D) + 0.046 * sinus(2 * D - sun.M) + 0.041 * sinus(M - sun.M) - 0.035 * sinus(D) // (the Parallactic Equation)
                - 0.031 * sinus(M + sun.M) - 0.015 * sinus(2 * F - 2 * D)
                + 0.011 * sinus(M - 4 * D))

        // Add these terms to the Moon's latitude (degrees):
        val lat_corr = -0.173 * sinus(F - 2 * D) - 0.055 * sinus(M - F - 2 * D) - 0.046 * sinus(M + F - 2 * D) + 0.033 * sinus(F + 2 * D) + 0.017 * sinus(2 * M + F)

        // Add these terms to the Moon's distance (Earth radii):
        val r_corr = (-0.58 * cosines(M - 2 * D)
                - 0.46 * cosines(2 * D))
        longitude = Degree.normalize(longitude + lon_corr)
        latitude = Degree.normalizeTo180(latitude + lat_corr)
        mr += r_corr
    }

    // The Moon's topocentric position
    fun applyTopocentricCorrection(moment: IMoment) {
        parallax = Degree.arcSin(1 / mr)
        diameter = 31.2283333333333 / mr
        val latitude = moment.location.latitude
        val geocentricLatitude = latitude - 0.1924 * sinus(2 * latitude)
        val rho = 0.99833 + 0.00167 * cosines(2 * latitude)
        val HA = Degree.normalize(15 * moment.lst - RA)
        val g = arcTan2(Degree.tangent(geocentricLatitude), cosines(HA))
        if (Math.abs(geocentricLatitude) > 0.001 && Math.abs(Decl) > 0.001) {
            val RA_corr = -parallax * rho * cosines(geocentricLatitude) * sinus(HA) / cosines(Decl)
            val Decl_corr = -parallax * rho * sinus(geocentricLatitude) * sinus(g - Decl) / sinus(g)
            RA = RA + RA_corr
            Decl = Decl + Decl_corr
        }
    }

    fun calculateMagnitude() {
        magn = -21.62 + 5 * Math.log10(mr * R) + 0.026 * FV + 4.0E-9 * Math.pow(FV, 4.0)
    }

    override fun calculateSetRiseTimes(moment: IMoment) {
        val inSouth = getTimeInSouth(moment)
        val cos_LHA = getHourAngle(moment.location.latitude)
        position!!.inSouth = moment.utc.setHours(inSouth)
        position!!.culmination = getHeightFor(moment.forUTC(position!!.inSouth!!))


        // cos_LHA < 0 ---> always up and visible
        // cos_LHA > 0 ---> always down
        if (cos_LHA > -1 && cos_LHA < 1) {
            val LHA = Degree.arcCos(cos_LHA) / 15
            val tolerance = moment.location.longitude / 15 + 1.5 // current timezone with potential daylight savings?
            position!!.riseTime = iterate(moment, moment.utc.setHours(inSouth - LHA), true)
            position!!.setTime = iterate(moment, moment.utc.setHours(inSouth + LHA), false)
            if (inSouth - LHA < 0.0 + tolerance) position!!.nextRiseTime = iterate(moment, position!!.riseTime!!.addHours(24.45), true)
            if (inSouth + LHA > 24.0 - tolerance) position!!.prevSetTime = iterate(moment, position!!.setTime!!.addHours(-24.45), false)
        }
    }

    fun calculateNewFullMoon(moment: IMoment) {
        val shift = getCurrentShift(moment)
        prevNewMoon = getNewMoon(moment, shift)
        fullMoon = getFullMoon(moment, shift)
        nextNewMoon = getNewMoon(moment, shift + 1)
        age = (moment.utc.julianDay - prevNewMoon!!.julianDay) / (nextNewMoon!!.julianDay - prevNewMoon!!.julianDay)
    }

    private fun iterate(moment: IMoment, x: UTC?, isRise: Boolean): UTC? {
        if (x == null) return null
        val f = getHeightFor(moment.forUTC(x))
        val x1: UTC
        val x2: UTC
        val f1: Double
        val f2: Double
        if (isRise && f < 0 || !isRise && f > 0) {
            x1 = x
            x2 = x.addHours(2.0)
            f1 = f
            f2 = getHeightFor(moment.forUTC(x2))
        } else {
            x1 = x.addHours(-2.0)
            x2 = x
            f1 = getHeightFor(moment.forUTC(x1))
            f2 = f
        }
        return iterate(x1, x2, f1, f2, moment)
    }

    private fun iterate(_x1: UTC, _x2: UTC, _f1: Double, _f2: Double, moment: IMoment): UTC? {
        var x1 = _x1
        var x2: UTC = _x2
        var f1 = _f1
        var f2 = _f2
        for (n in 0..99) {
            val dx = UTC.gapInHours(x1, x2)
            val x = x1.addHours(dx * f1 / (f1 - f2))
            val f = getHeightFor(moment.forUTC(x))
            if (abs(f) < TOLERANCE) return x
            if (sign(f) == sign(f1)) {
                x1 = x
                f1 = f
            } else {
                x2 = x
                f2 = f
            }
        }
        return null
    }

    override fun getHeightFor(moment: IMoment): Double {
        val moon = getMoonFor(moment)
        return moon.height
    }

    private val height: Double
        get() = position!!.altitude - H0()

    override fun H0(): Double {
        // Moon's upper limb touches the horizon; atmospheric refraction accounted for
        // 0.583 atmospheric refraction
        // Moon's parallax, the apparent size of the (equatorial) radius of the Earth, as seen from the Moon: asin(1 / r), r = distance in earth radii ~ 0.95
        // The parallax is accounted for the call of applyTopocentricCorrection()
        // Moon's semi diameter: 1873.7" * 30 / r ~ 0.5 between 29′20″ and 34′6″
        return -0.583 - 0.5 * diameter
    }

    // The cos of the hour angle for sunrise and sunset
    private fun getHourAngle(observerLatitude: Double): Double {
        return (sinus(H0()) - sinus(observerLatitude) * sinus(Decl)) / (cosines(observerLatitude) * cosines(Decl))
    }

    private enum class Phase {
        FULL, NEW
    }

    // Calculate the time (in UTC) when the sun will be in south at this position (longitude is defined by LST)
    private fun getTimeInSouth(moment: IMoment): Double {
        return Hour.normalize(RA / 15 - moment.utc.gMST0 - moment.location.longitude / 15)
    }

    // calculate RA and Decl for the moon at this time
    private fun getMoonFor(moment: IMoment): Moon {
        val sun = Sun()
        sun.updateBasics(moment.d)
        sun.updateHeliocentricLatitudeLongitude(moment.d)
        sun.updateGeocentricAscensionDeclination()
        val moon = Moon()
        moon.updateBasics(moment.d)
        moon.updateHeliocentricLatitudeLongitude(moment.d)
        moon.updateGeocentricAscensionDeclination(sun)
        moon.applyPerturbations(sun)
        moon.applyTopocentricCorrection(moment)
        moon.updateAzimuthAltitude(moment)
        return moon
    }

    fun calculateShadows(sun: Sun) {
        // use FV = 180 - elongation;
        val R = Sun.RADIUS
        val r = Earth.RADIUS
        val D = sun.distanceInKm
        val d = distanceInKm
        val x = d * cosines(FV)
        val y = d * sinus(FV)
        if (FV < 45) {
            nuclearShadow = r - (R - r) * x / D
            halfShadow = (R + r) * (D + x) / D - R
        } else {
            nuclearShadow = 0.0
            halfShadow = 0.0
        }
        far = y + RADIUS
        near = y - RADIUS
    }

    override val distanceInKm: Double
        get() = Algorithms.EARTH_RADIUS * R

    override fun getBasics(moment: Moment): PropertyList {
        val basics = super.getBasics(moment)
        basics.add(com.stho.nyota.R.drawable.sunset, position?.prevSetTime, moment.timeZone)
        basics.add(com.stho.nyota.R.drawable.sunrise, position?.riseTime, moment.timeZone)
        basics.add(com.stho.nyota.R.drawable.sunset, position?.setTime, moment.timeZone)
        basics.add(com.stho.nyota.R.drawable.sunrise, position?.nextRiseTime, moment.timeZone)
        basics.add(com.stho.nyota.R.drawable.angle, Degree.fromDegree(diameter))
        return basics
    }

    override fun getDetails(moment: Moment): PropertyList {
        val details = super.getDetails(moment)
        details.add(com.stho.nyota.R.drawable.sunset, "Set", position?.prevSetTime, moment.timeZone)
        details.add(com.stho.nyota.R.drawable.sunrise, "Rise", position?.riseTime, moment.timeZone)
        details.add(com.stho.nyota.R.drawable.sunset, "Set ", position?.setTime, moment.timeZone)
        details.add(com.stho.nyota.R.drawable.sunrise, "Rise", position?.nextRiseTime, moment.timeZone)
        details.add(com.stho.nyota.R.drawable.star, "Age", Formatter.df2.format(age))
        details.add(com.stho.nyota.R.drawable.angle, "Diameter", Degree.fromDegree(diameter))
        details.add(com.stho.nyota.R.drawable.star, "Magnitude", Formatter.df2.format(magn))
        details.add(com.stho.nyota.R.drawable.star, "FV", Degree.fromDegree(FV))
        details.add(com.stho.nyota.R.drawable.star, "Phase", Formatter.df3.format(phase))
        details.add(com.stho.nyota.R.drawable.star, "Phase angle", Formatter.df0.format(phaseAngle))
        details.add(com.stho.nyota.R.drawable.star, "New", prevNewMoon, moment.timeZone)
        details.add(com.stho.nyota.R.drawable.star, "Full", fullMoon, moment.timeZone)
        details.add(com.stho.nyota.R.drawable.star, "New+", nextNewMoon, moment.timeZone)
        details.add(com.stho.nyota.R.drawable.star, "Parallax", Degree.fromDegree(parallax))
        details.add(com.stho.nyota.R.drawable.star, "Parallactic", Degree.fromDegree(parallacticAngle))
        details.add(com.stho.nyota.R.drawable.star, "Meridian", position?.inSouth, moment.timeZone)
        details.add(com.stho.nyota.R.drawable.star, "Culmination", Degree.fromDegree(position!!.culmination))
        details.add(com.stho.nyota.R.drawable.star, "Shadow", Formatter.df0.format(nuclearShadow) + " - " + Formatter.df0.format(halfShadow) + " km")
        details.add(com.stho.nyota.R.drawable.star, "Shadow Distance", Formatter.df0.format(near) + " - " + Formatter.df0.format(far) + " km")
        return details
    }

    companion object {
        const val RADIUS = 1737.0 // in km

        private const val TOLERANCE = 0.001

        fun getCurrentShift(moment: IMoment): Int {
            return if (moment.utc.julianDay < getTimeFor(moment, Phase.NEW, 0)) -1 else if (moment.utc.julianDay > getTimeFor(moment, Phase.NEW, 1)) 1 else 0
        }

        fun getNewMoon(moment: IMoment, shift: Int): UTC {
            val julianDay = getTimeFor(moment, Phase.NEW, shift)
            return toUTC(julianDay)
        }

        fun getFullMoon(moment: IMoment, shift: Int): UTC {
            val julianDay = getTimeFor(moment, Phase.FULL, shift)
            return toUTC(julianDay)
        }

        private const val HALF_A_MONTH = 0.5 / 12.3685

        /**
         * Julian Days for the new moon or full moon, Meeus chapter 47
         * @param moment current time and location
         * @param phase FULL or NEW
         * @param shift Phases before (shift < 0) or after (shift > 0)
         * @return Julian (Ephemeris) Day (in Dynamic Time)
         */
        private fun getTimeFor(moment: IMoment, phase: Phase, shift: Int): Double {
            // Meeus chapter 47
            // k is an integer for new moon incremented by 0.25 for first quarter 0.5 for full moon. k=0 corresponds to the New Moon 2000 Jan 6th.
            val years: Double = moment.utc.yearsSince2000 - HALF_A_MONTH
            var k = Math.floor(years * 12.3685)
            k += shift.toDouble()
            if (phase == Phase.FULL) k += 0.5
            val T = k / 1236.85
            val T2 = T * T
            val T3 = T * T2
            val T4 = T * T3
            val E = 1 - 0.002516 * T - 0.0000074 * T2

            // Sun's mean anomaly
            val M = Degree.normalize(2.5534 + 29.10535670 * k - 0.00000014 * T2 - 0.00000011 * T3)

            // Moon's mean anomaly (M' in Meeus)
            val MP = Degree.normalize(201.5643 + 385.81693528 * k + 0.0107582 * T2 + 0.00001238 * T3 - 0.000000058 * T4)

            // Moons argument of latitude
            val F = Degree.normalize(160.7108 + 390.67050284 * k - 0.0016118 * T2 - 0.00000227 * T3 + 0.000000011 * T4)

            // Longitude of ascending node of lunar orbit
            val Omega = Degree.normalize(124.7746 - 1.56375588 * k + 0.0020672 * T2 + 0.00000215 * T3)

            // The full planetary arguments include 14 terms, only used the 7 most significant
            val A1 = Degree.normalize(299.77 + 0.107408 * k - 0.009173 * T2)
            val A2 = Degree.normalize(251.88 + 0.016321 * k)
            val A3 = Degree.normalize(251.83 + 26.651886 * k)
            val A4 = Degree.normalize(349.42 + 36.412478 * k)
            val A5 = Degree.normalize(84.88 + 18.206239 * k)
            val A6 = Degree.normalize(141.74 + 53.303771 * k)
            val A7 = Degree.normalize(207.14 + 2.453732 * k)
            val A8 = Degree.normalize(154.84 + 27.261239 * k)
            val A9 = Degree.normalize(34.52 + 27.261239 * k)
            val A10 = Degree.normalize(207.19 + 0.121824 * k)
            val A11 = Degree.normalize(291.34 + 1.844379 * k)
            val A12 = Degree.normalize(161.72 + 24.198154 * k)
            val A13 = Degree.normalize(239.56 + 25.513099 * k)
            val A14 = Degree.normalize(331.55 + 3.592518 * k)
            var JDE = 2451550.09766 + 29.530588861 * k + 0.00015437 * T2 - 0.000000150 * T3 + 0.00000000073 * T4

            // New moon

            // Correct for TDT since 1 July 2015
            // JDE0=JDE0-58.184/(24*60*60);
            if (phase == Phase.NEW) {
                JDE = (((((JDE
                        - 0.40720 * sinus(MP)
                        ) + 0.17241 * E * sinus(M) + 0.01608 * sinus(2 * MP) + 0.01039 * sinus(2 * F) + 0.00739 * E * sinus(MP - M)
                        - 0.00514 * E * sinus(MP + M)
                        + 0.00208 * E * E * sinus(2 * M)
                        ) - 0.00111 * sinus(MP - 2 * F) - 0.00057 * sinus(MP + 2 * F)
                        + 0.00056 * E * sinus(2 * MP + M)
                        - 0.00042 * sinus(3 * MP)
                        ) + 0.00042 * E * sinus(M + 2 * F) + 0.00038 * E * sinus(M - 2 * F) - 0.00024 * E * sinus(2 * MP - M) - 0.00017 * sinus(Omega) - 0.00007 * sinus(MP + 2 * M) + 0.00004 * sinus(2 * MP - 2 * F) + 0.00004 * sinus(3 * M) + 0.00003 * sinus(MP + M - 2 * F) + 0.00003 * sinus(2 * MP + 2 * F)
                        - 0.00003 * sinus(MP + M + 2 * F)
                        + 0.00003 * sinus(MP - M + 2 * F)
                        ) - 0.00002 * sinus(MP - M - 2 * F) - 0.00002 * sinus(3 * MP + M)
                        + 0.00002 * sinus(4 * MP))
            }
            if (phase == Phase.FULL) {
                JDE = (((((JDE
                        - 0.40614 * sinus(MP)
                        ) + 0.17302 * E * sinus(M) + 0.01614 * sinus(2 * MP) + 0.01043 * sinus(2 * F) + 0.00734 * E * sinus(MP - M)
                        - 0.00515 * E * sinus(MP + M)
                        + 0.00209 * E * E * sinus(2 * M)
                        ) - 0.00111 * sinus(MP - 2 * F) - 0.00057 * sinus(MP + 2 * F)
                        + 0.00056 * E * sinus(2 * MP + M)
                        - 0.00042 * sinus(3 * MP)
                        ) + 0.00042 * E * sinus(M + 2 * F) + 0.00038 * E * sinus(M - 2 * F) - 0.00024 * E * sinus(2 * MP - M) - 0.00017 * sinus(Omega) - 0.00007 * sinus(MP + 2 * M) + 0.00004 * sinus(2 * MP - 2 * F) + 0.00004 * sinus(3 * M) + 0.00003 * sinus(MP + M - 2 * F) + 0.00003 * sinus(2 * MP + 2 * F)
                        - 0.00003 * sinus(MP + M + 2 * F)
                        + 0.00003 * sinus(MP - M + 2 * F)
                        ) - 0.00002 * sinus(MP - M - 2 * F) - 0.00002 * sinus(3 * MP + M)
                        + 0.00002 * sinus(4 * MP))
            }
            JDE = (JDE
                    + 0.000325 * sinus(A1) + 0.000165 * sinus(A2) + 0.000164 * sinus(A3) + 0.000126 * sinus(A4) + 0.000110 * sinus(A5) + 0.000062 * sinus(A6) + 0.000060 * sinus(A7) + 0.000056 * sinus(A8) + 0.000047 * sinus(A9) + 0.000042 * sinus(A10) + 0.000040 * sinus(A11) + 0.000037 * sinus(A12) + 0.000035 * sinus(A13) + 0.000023 * sinus(A14))
            return JDE
        }
    }
}