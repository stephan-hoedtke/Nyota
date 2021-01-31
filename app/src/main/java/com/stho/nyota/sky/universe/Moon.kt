package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.*
import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.Degree.Companion.arcTan2
import com.stho.nyota.sky.utilities.Degree.Companion.cos
import com.stho.nyota.sky.utilities.Degree.Companion.sin
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sign

/**
 * Created by shoedtke on 30.08.2016.
 */
@Suppress("LocalVariableName")
class Moon : AbstractSolarSystemElement() {

    override val key: String =
        "MOON"

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
    val fullMoonBrightness = -12.7

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
        val lon_corr = (((-1.274 * sin(M - 2 * D) // (the Evection)
                + 0.658 * sin(2 * D) // (the Variation)
                ) - 0.186 * sin(sun.M) // (the Yearly Equation)
                - 0.059 * sin(2 * M - 2 * D) - 0.057 * sin(M - 2 * D + sun.M)
                ) + 0.053 * sin(M + 2 * D) + 0.046 * sin(2 * D - sun.M) + 0.041 * sin(M - sun.M) - 0.035 * sin(D) // (the Parallactic Equation)
                - 0.031 * sin(M + sun.M) - 0.015 * sin(2 * F - 2 * D)
                + 0.011 * sin(M - 4 * D))

        // Add these terms to the Moon's latitude (degrees):
        val lat_corr = -0.173 * sin(F - 2 * D) - 0.055 * sin(M - F - 2 * D) - 0.046 * sin(M + F - 2 * D) + 0.033 * sin(F + 2 * D) + 0.017 * sin(2 * M + F)

        // Add these terms to the Moon's distance (Earth radii):
        val r_corr = (-0.58 * cos(M - 2 * D)
                - 0.46 * cos(2 * D))
        longitude = Degree.normalize(longitude + lon_corr)
        latitude = Degree.normalizeTo180(latitude + lat_corr)
        mr += r_corr
    }

    // The Moon's topocentric position
    fun applyTopocentricCorrection(moment: IMoment) {
        parallax = Degree.arcSin(1 / mr)
        diameter = 31.2283333333333 / mr
        val latitude = moment.location.latitude
        val geocentricLatitude = latitude - 0.1924 * sin(2 * latitude)
        val rho = 0.99833 + 0.00167 * cos(2 * latitude)
        val HA = Degree.normalize(15 * moment.lst - RA)
        val g = arcTan2(Degree.tan(geocentricLatitude), cos(HA))
        if (Math.abs(geocentricLatitude) > 0.001 && Math.abs(Decl) > 0.001) {
            val RA_corr = -parallax * rho * cos(geocentricLatitude) * sin(HA) / cos(Decl)
            val Decl_corr = -parallax * rho * sin(geocentricLatitude) * sin(g - Decl) / sin(g)
            RA = RA + RA_corr
            Decl = Decl + Decl_corr
        }
    }

    fun calculateMagnitude() {
        magn = -21.62 + 5 * Math.log10(mr * R) + 0.026 * FV + 4.0E-9 * Math.pow(FV, 4.0)
    }

    // see also: AbstractSolarSystemElement.calculateSetRiseTimes
    //           Moon.calculateSetRiseTimes

    override fun calculateSetRiseTimes(moment: IMoment) =
        calculateSetRiseTimes(moment, 24.45)


//        position?.also {
//            val inSouth = getTimeInSouth(moment)
//            val cos_LHA = getHourAngle(moment.location.latitude)
//            it.inSouth = moment.utc.setHours(inSouth)
//            it.culmination = getHeightFor(moment.forUTC(it.inSouth!!))
//
//            // case 1 - ideal: rise0 (now) set0
//            //                      --> previous set , rise0, (now), set0, next rise
//            // case 2: (now) rise0 set0
//            //      2.1: previous rise, previous set, (now), rise0, set0
//            //      2.2: before set, previous rise, (now), previous set, rise0, set0
//            // case 3: rise0 set0 (now)
//            //      3.1 rise0, set0, (now), next rise, next set
//            //      3.2 rise0, set0, next rise, (now), next set, after rise
//            //
//            //
//            // cos_LHA < 0 ---> always up and visible
//            // cos_LHA > 0 ---> always down
//            if (cos_LHA > -1 && cos_LHA < 1) {
//                val LHA = Degree.arcCos(cos_LHA) / 15
//                val riseTime0 = iterate(moment, moment.utc.setHours(inSouth - LHA), true)
//                val setTime0 = iterate(moment, moment.utc.setHours(inSouth + LHA), false)
//                when {
//                    moment.utc.isLessThan(riseTime0) -> {
//                        Log.d("MOON", "utc: ${moment.utc} rise: $riseTime0 set: $setTime0")
//                        val riseTime1 = iterate(moment, moment.utc.setHours(inSouth - LHA - 24.45),true)
//                        val setTime1 = iterate(moment, moment.utc.setHours(inSouth + LHA - 24.45), false)
//                        it.riseTime = riseTime0
//                        it.setTime = setTime0
//                    }
//                    moment.utc.isGreaterThan(setTime0) -> {
//                        Log.d("MOON", "rise: $riseTime0 set: $setTime0 utc: ${moment.utc}")
//                        it.riseTime = riseTime0
//                        it.setTime = setTime0
//                        it.nextRiseTime = iterate(moment, moment.utc.setHours(inSouth - LHA + 24.45),true)
//                        it.nextSetTime = iterate(moment, moment.utc.setHours(inSouth + LHA + 24.45), false)
//                    }
//                    else -> {
//                        Log.d("MOON", "rise: $riseTime0 utc: ${moment.utc} set: $setTime0")
//                        it.prevSetTime = iterate(moment, moment.utc.setHours(inSouth + LHA - 24.45), false)
//                        it.riseTime = riseTime0
//                        it.setTime = setTime0
//                        it.nextRiseTime = iterate(moment, moment.utc.setHours(inSouth - LHA + 24.45),true)
//                    }
//                }
//            }
//        }
//    }

    fun calculateNewFullMoon(utc: UTC) {
        val newMoon0 = getNewMoon(utc, 0)
        val newMoon1 = getNewMoon(utc, 1)
        when {
            utc.isBefore(newMoon0) -> {
                // CURRENT SHIFT = -1
                prevNewMoon = getNewMoon(utc, -1)
                fullMoon = getFullMoon(utc, -1)
                nextNewMoon = newMoon0
            }
            utc.isAfter(newMoon1) -> {
                // CURRENT SHIFT = 1
                prevNewMoon = newMoon1
                fullMoon = getFullMoon(utc, 1)
                nextNewMoon = getNewMoon(utc, 2)
            }
            else -> {
                // CURRENT SHIFT = 0
                prevNewMoon = newMoon0
                fullMoon = getFullMoon(utc, 0)
                nextNewMoon = newMoon1
            }
        }
        age = (utc.julianDay - prevNewMoon!!.julianDay) / (nextNewMoon!!.julianDay - prevNewMoon!!.julianDay)
    }

    private fun iterate(moment: IMoment, x: UTC?, isRise: Boolean): UTC? {
        if (x == null) {
            return null
        }
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
        return (sin(H0()) - sin(observerLatitude) * sin(Decl)) / (cos(observerLatitude) * cos(Decl))
    }

    private enum class Phase {
        FULL, NEW
    }

    // Calculate the time (in UTC) when the sun will be in south at this position (longitude is defined by LST)
    private fun getTimeInSouth(moment: IMoment): Double {
        return Hour.normalize(RA / 15 - moment.utc.GMST0 - moment.location.longitude / 15)
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
        val x = d * cos(FV)
        val y = d * sin(FV)
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

    override fun getBasics(moment: Moment): PropertyList =
        super.getBasics(moment).apply {
            if (position?.isUp == true) {
                add(com.stho.nyota.R.drawable.sunset, "Previous Set", position?.prevSetTime, moment.timeZone)
                add(com.stho.nyota.R.drawable.sunrise, "Rise", position?.riseTime, moment.timeZone)
                add(com.stho.nyota.R.drawable.sunset, "Set ", position?.setTime, moment.timeZone)
                add(com.stho.nyota.R.drawable.sunrise, "Next Rise", position?.nextRiseTime, moment.timeZone)
            } else {
                add(com.stho.nyota.R.drawable.sunrise, "Previous Rise", position?.prevRiseTime, moment.timeZone)
                add(com.stho.nyota.R.drawable.sunset, "Set ", position?.setTime, moment.timeZone)
                add(com.stho.nyota.R.drawable.sunrise, "Rise", position?.riseTime, moment.timeZone)
                add(com.stho.nyota.R.drawable.sunset, "Next Set", position?.nextSetTime, moment.timeZone)
            }
            add(com.stho.nyota.R.drawable.empty, "Age", Formatter.df2.format(age))
            add(com.stho.nyota.R.drawable.angle, "Diameter", Degree.fromDegree(diameter))
            add(com.stho.nyota.R.drawable.empty, "Magnitude", Formatter.df2.format(magn))
        }

    override fun getDetails(moment: Moment): PropertyList =
        super.getDetails(moment).apply {
            add(com.stho.nyota.R.drawable.empty, "FV", Degree.fromDegree(FV))
            add(com.stho.nyota.R.drawable.empty, "Phase", Formatter.df3.format(phase))
            add(com.stho.nyota.R.drawable.empty, "Phase angle", Formatter.df0.format(phaseAngle))
            add(com.stho.nyota.R.drawable.empty, "New", prevNewMoon, moment.timeZone)
            add(com.stho.nyota.R.drawable.empty, "Full", fullMoon, moment.timeZone)
            add(com.stho.nyota.R.drawable.empty, "New+", nextNewMoon, moment.timeZone)
            add(com.stho.nyota.R.drawable.empty, "Parallax", Degree.fromDegree(parallax))
            add(com.stho.nyota.R.drawable.empty, "Parallactic", Degree.fromDegree(parallacticAngle))
            add(com.stho.nyota.R.drawable.empty, "Meridian", position?.inSouth, moment.timeZone)
            add(com.stho.nyota.R.drawable.empty, "Culmination", Degree.fromDegree(position!!.culmination))
            add(com.stho.nyota.R.drawable.empty, "Shadow", Formatter.df0.format(nuclearShadow) + " - " + Formatter.df0.format(halfShadow) + " km")
            add(com.stho.nyota.R.drawable.empty, "Shadow Distance", Formatter.df0.format(near) + " - " + Formatter.df0.format(far) + " km")
        }

    companion object {
        const val RADIUS = 1737.0 // in km

        private const val TOLERANCE = 0.001

        // TODO better usage of "getTimeFor(), store values in a dictionary ??

        fun getNewMoon(utc: UTC, shift: Int): UTC =
            UTC.forJulianDay(getJulianDayFor(utc, Phase.NEW, shift))

        fun getFullMoon(utc: UTC, shift: Int): UTC =
            UTC.forJulianDay(getJulianDayFor(utc, Phase.FULL, shift))

        private const val HALF_A_MONTH = 0.5 / 12.3685

        /**
         * Julian Days for the new moon or full moon, Meeus chapter 47
         * @param utc current time and location
         * @param phase FULL or NEW
         * @param shift Phases before (shift < 0) or after (shift > 0)
         * @return Julian (Ephemeris) Day (in Dynamic Time)
         */
        private fun getJulianDayFor(utc: UTC, phase: Phase, shift: Int): Double {
            // Meeus chapter 47
            // k is an integer for new moon incremented by 0.25 for first quarter 0.5 for full moon. k=0 corresponds to the New Moon 2000 Jan 6th.
            val years: Double = utc.yearsSince2000 - HALF_A_MONTH
            var k = floor(years * 12.3685) + shift
            if (phase == Phase.FULL) {
                k += 0.5
            }
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
                        - 0.40720 * sin(MP)
                        ) + 0.17241 * E * sin(M) + 0.01608 * sin(2 * MP) + 0.01039 * sin(2 * F) + 0.00739 * E * sin(MP - M)
                        - 0.00514 * E * sin(MP + M)
                        + 0.00208 * E * E * sin(2 * M)
                        ) - 0.00111 * sin(MP - 2 * F) - 0.00057 * sin(MP + 2 * F)
                        + 0.00056 * E * sin(2 * MP + M)
                        - 0.00042 * sin(3 * MP)
                        ) + 0.00042 * E * sin(M + 2 * F) + 0.00038 * E * sin(M - 2 * F) - 0.00024 * E * sin(2 * MP - M) - 0.00017 * sin(Omega) - 0.00007 * sin(MP + 2 * M) + 0.00004 * sin(2 * MP - 2 * F) + 0.00004 * sin(3 * M) + 0.00003 * sin(MP + M - 2 * F) + 0.00003 * sin(2 * MP + 2 * F)
                        - 0.00003 * sin(MP + M + 2 * F)
                        + 0.00003 * sin(MP - M + 2 * F)
                        ) - 0.00002 * sin(MP - M - 2 * F) - 0.00002 * sin(3 * MP + M)
                        + 0.00002 * sin(4 * MP))
            }
            if (phase == Phase.FULL) {
                JDE = (((((JDE
                        - 0.40614 * sin(MP)
                        ) + 0.17302 * E * sin(M) + 0.01614 * sin(2 * MP) + 0.01043 * sin(2 * F) + 0.00734 * E * sin(MP - M)
                        - 0.00515 * E * sin(MP + M)
                        + 0.00209 * E * E * sin(2 * M)
                        ) - 0.00111 * sin(MP - 2 * F) - 0.00057 * sin(MP + 2 * F)
                        + 0.00056 * E * sin(2 * MP + M)
                        - 0.00042 * sin(3 * MP)
                        ) + 0.00042 * E * sin(M + 2 * F) + 0.00038 * E * sin(M - 2 * F) - 0.00024 * E * sin(2 * MP - M) - 0.00017 * sin(Omega) - 0.00007 * sin(MP + 2 * M) + 0.00004 * sin(2 * MP - 2 * F) + 0.00004 * sin(3 * M) + 0.00003 * sin(MP + M - 2 * F) + 0.00003 * sin(2 * MP + 2 * F)
                        - 0.00003 * sin(MP + M + 2 * F)
                        + 0.00003 * sin(MP - M + 2 * F)
                        ) - 0.00002 * sin(MP - M - 2 * F) - 0.00002 * sin(3 * MP + M)
                        + 0.00002 * sin(4 * MP))
            }
            JDE = (JDE
                    + 0.000325 * sin(A1) + 0.000165 * sin(A2) + 0.000164 * sin(A3) + 0.000126 * sin(A4) + 0.000110 * sin(A5) + 0.000062 * sin(A6) + 0.000060 * sin(A7) + 0.000056 * sin(A8) + 0.000047 * sin(A9) + 0.000042 * sin(A10) + 0.000040 * sin(A11) + 0.000037 * sin(A12) + 0.000035 * sin(A13) + 0.000023 * sin(A14))
            return JDE
        }
    }
}