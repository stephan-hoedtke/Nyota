package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.*
import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.Degree.Companion.arcCos
import com.stho.nyota.sky.utilities.Degree.Companion.arcTan2
import com.stho.nyota.sky.utilities.Degree.Companion.normalize
import com.stho.nyota.sky.utilities.Degree.Companion.normalizeTo180
import com.stho.nyota.sky.utilities.Degree.Companion.sin
import com.stho.nyota.sky.utilities.Degree.Companion.cos
import com.stho.nyota.sky.utilities.Degree.Companion.tan
import java.util.concurrent.TimeUnit

/**
 * Orbital SolarSystemElement
 * Created by shoedtke on 30.08.2016.
 */
abstract class AbstractSolarSystemElement : AbstractElement() {
    internal var N = 0.0 // longitude of the ascending node = 0.0
    internal var i = 0.0 // inclination to the ecliptic (plane of the Earth's orbit) = 0.0
    internal var w = 0.0 // argument of perihelion (nearest to sun in orbit) = 0.0
    internal var a = 0.0 // semi-major axis or mean distance from the Sun = 0.0
    internal var e = 0.0 // eccentricity = 0.0
    internal var M = 0.0 // mean anomaly (0 at perihelion) = 0.0
    internal var mr = 0.0 // mean radius = 0.0
    internal var w1 = 0.0 // longitude of perihelion = 0.0
    internal var pq = 0.0 // perihelion distance = 0.0
    internal var Q = 0.0 // aphelion distance = 0.0
    internal var P = 0.0 // orbital period (years of a is in AU) = 0.0
    internal var T = 0.0 // time of perihelion = 0.0
    internal var v = 0.0 // true anomaly (angle between position and perihelion) = 0.0
    internal var ecl = 0.0 // obliquity of the ecliptic, i.e. the "tilt" of the earths axis of rotation = 0.0
    internal var R = 0.0 // geocentric distance = 0.0
    internal var L = 0.0 // mean longitude = 0.0
    internal var EA: Double = 0.0 // eccentric anomaly = 0.0
    internal var longitude = 0.0 // elliptic heliocentric longitude = 0.0
    internal var latitude = 0.0 // elliptic heliocentric latitude = 0.0
    internal var x = 0.0
    internal var y = 0.0
    internal var z = 0.0
    internal var elongation = 0.0 // the apparent angular distance of the planet from the sun = 0.0
    internal var FV = 0.0
    internal var phase = 0.0
    internal var phaseAngle = 0.0
    internal var parallacticAngle = 0.0
    internal var zenithAngle = 0.0

    abstract fun updateBasics(d: Double)

    open val isSun: Boolean
        get() = false

    open val isMoon: Boolean
        get() = false

    open val isPlanet: Boolean
        get() = false

    open fun updateHeliocentricLatitudeLongitude(d: Double) {
        w1 = N + w
        pq = a * (1 - e)
        Q = a * (1 + e)
        P = Math.pow(a, 1.5)
        ecl = 23.4393 - 3.563E-7 * d
        L = normalize(M + w + N)

        // eccentric anomaly with iteration for "larger" eccentric
        var E0 = M + Degree.RADEG * e * sin(M) * (1.0 + e * cos(M))
        var E1 = E0 - (E0 - Degree.RADEG * e * sin(E0) - M) / (1 - e * cos(E0))
        run {
            var x = 0
            while (x < 10 && Math.abs(E1 - E0) > 0.001) {
                E0 = E1
                E1 = E0 - (E0 - Degree.RADEG * e * sin(E0) - M) / (1 - e * cos(E0))
                x++
            }
        }
        EA = normalize(E1)
        val xv = a * (cos(EA) - e)
        val yv = a * (Math.sqrt(1.0 - e * e) * sin(EA))
        v = normalize(arcTan2(yv, xv))
        mr = Math.sqrt(xv * xv + yv * yv)

        // mean longitude
        val lon = v + w

        // position int the 3 dimensional space
        // for the moon: geocentric position in the ecliptic coordinate system
        // for planets: heliocentric position in the ecliptic coordinate system
        val xh = mr * (cos(N) * cos(lon) - sin(N) * sin(lon) * cos(i))
        val yh = mr * (sin(N) * cos(lon) + cos(N) * sin(lon) * cos(i))
        val zh = mr * (sin(lon) * sin(i))

        // ecliptic longitude, latitude
        longitude = normalize(arcTan2(yh, xh))
        latitude = normalizeTo180(arcTan2(zh, Math.sqrt(xh * xh + yh * yh)))
    }

    fun applyPrecession(Epoch: Double, d: Double) {
        val lon_corr = 3.82394E-5 * (365.2422 * (Epoch - 2000.0) - d)
        longitude = normalize(longitude + lon_corr)
    }

    fun updateGeocentricAscensionDeclination(sun: Sun) {

        // we may ignore precession! (all values are calculated for the moment of d)
        // ecliptic rectangular geocentric coordinates
        x = mr * cos(longitude) * cos(latitude)
        y = mr * sin(longitude) * cos(latitude)
        z = mr * sin(latitude)

        // equatorial rectangular geocentric coordinates
        var xg = x
        var yg = y
        var zg = z
        if (isPlanet) {
            xg += sun.x
            yg += sun.y
            zg += sun.z
        }
        val xe = xg
        val ye = yg * cos(ecl) - zg * sin(ecl)
        val ze = yg * sin(ecl) + zg * cos(ecl)

        // Finally, compute the planet's Right Ascension (RA) and Declination (Decl):
        RA = normalize(arcTan2(ye, xe))
        Decl = normalizeTo180(arcTan2(ze, Math.sqrt(xe * xe + ye * ye)))

        // Compute the geocentric distance:
        R = Math.sqrt(xg * xg + yg * yg + zg * zg)
    }

    open fun calculatePhase(sun: Sun) {
        elongation = arcCos(cos(sun.longitude - longitude) * cos(latitude))
        FV = 180 - elongation
        phase = (1 + cos(FV)) / 2
        phaseAngle = getPhaseAngle(sun)
    }

    fun calculateParallacticAngle(moment: IMoment) {
        parallacticAngle = getParallacticAngle(moment)
        zenithAngle = normalizeTo180(phaseAngle - parallacticAngle)
    }

    protected fun getPhaseAngle(sun: Sun): Double {
        // Meeus pp.347
        // Further correction: zenith-angle := phase-angle - parallactic-angle
        return arcTan2(
                cos(sun.Decl) * sin(sun.RA - RA),
                sin(sun.Decl) * cos(Decl) - cos(sun.Decl) * sin(Decl) * cos(sun.RA - RA))
    }

    protected fun getZenithAngle(sun: Sun): Double {
        // Calculation from altitude doesn't require correction by the parallactic-angle, but the current altitude and azimuth
        return if (sun.position != null) {
            arcTan2(
                    cos(sun.position!!.altitude) * sin(position!!.azimuth - sun.position!!.azimuth),
                    sin(sun.position!!.altitude) * cos(position!!.altitude) - cos(sun.position!!.altitude) * sin(position!!.altitude) * cos(position!!.azimuth - sun.position!!.azimuth))
        } else {
            0.0
        }
    }

    protected fun getParallacticAngle(moment: IMoment): Double {
        val HA = normalize(15 * moment.lst - RA)
        return arcTan2(sin(HA), tan(moment.location.latitude) * cos(Decl) - sin(Decl) * cos(HA))
    }

    open val distanceInKm: Double
        get() = Algorithms.ASTRONOMIC_UNIT * R

    override fun getBasics(moment: Moment): PropertyList =
        super.getBasics(moment).apply {
            add(com.stho.nyota.R.drawable.distance, "Distance", Formatter.df0.format(distanceInKm) + " km")
        }

    override fun getDetails(moment: Moment): PropertyList =
        super.getDetails(moment).apply {
            add(com.stho.nyota.R.drawable.angle, "Elongation", Degree.fromDegree(elongation))
        }

    open fun calculateSetRiseTimes(moment: IMoment) {
        position?.also {
            val inSouth = getTimeInSouth(moment)
            val cos_LHA = getHourAngle(moment.location.latitude)
            it.inSouth = moment.utc.setHours(inSouth)
            it.culmination = getHeightFor(moment.forUTC(position!!.inSouth!!))

            // cos_LHA < 0 ---> always up and visible
            // cos_LHA > 0 ---> always down
            if (cos_LHA > -1 && cos_LHA < 1) {
                val LHA = arcCos(cos_LHA) / 15
                val riseTime0 = iterate(moment, moment.utc.setHours(inSouth - LHA), true)
                val setTime0 = iterate(moment, moment.utc.setHours(inSouth + LHA), false)
                when {
                    moment.utc.isLessThan(riseTime0) -> {
                        it.prevRiseTime = iterate(moment, moment.utc.setHours(inSouth - LHA - 24.0), true)
                        it.prevSetTime = iterate(moment, moment.utc.setHours(inSouth + LHA - 24.0), false)
                        it.riseTime = riseTime0
                        it.setTime = setTime0
                    }
                    moment.utc.isGreaterThan(setTime0) -> {
                        it.riseTime = riseTime0
                        it.setTime = setTime0
                        it.nextRiseTime = iterate(moment, moment.utc.setHours(inSouth - LHA + 24.0), true)
                        it.nextSetTime = iterate(moment, moment.utc.setHours(inSouth + LHA + 24.0), false)
                    }
                    else -> {
                        it.prevSetTime = iterate(moment, moment.utc.setHours(inSouth + LHA - 24.0), false)
                        it.riseTime = riseTime0
                        it.setTime = setTime0
                        it.nextRiseTime = iterate(moment, moment.utc.setHours(inSouth - LHA + 24.0), true)
                    }
                }
            }
        }
    }

    private fun iterate(moment: IMoment, x: UTC, isRise: Boolean): UTC? {
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

    private fun iterate(x1: UTC, x2: UTC, f1: Double, f2: Double, moment: IMoment): UTC? {
        var lx1 = x1
        var lx2 = x2
        var lf1 = f1
        var lf2 = f2
        for (n in 0..99) {
            val dx = UTC.gapInHours(lx1, lx2)
            val x = lx1.addHours(dx * lf1 / (lf1 - lf2))
            val f = getHeightFor(moment.forUTC(x))
            if (Math.abs(f) < TOLERANCE) return x
            if (Math.signum(f) == Math.signum(lf1)) {
                lx1 = x
                lf1 = f
            } else {
                lx2 = x
                lf2 = f
            }
        }
        return null
    }

    // The cos of the hour angle for sunrise and sunset
    private fun getHourAngle(observerLatitude: Double): Double {
        return (sin(H0()) - sin(observerLatitude) * sin(Decl)) / (cos(observerLatitude) * cos(Decl))
    }

    // Calculate the time (in UTC) when the sun will be in south at this position (longitude is defined by LST)
    private fun getTimeInSouth(moment: IMoment): Double {
        val hour = Hour.normalize(RA / 15 - moment.utc.GMST0 - moment.location.longitude / 15)
        val offsetInHours = moment.timeZone.getOffset(moment.utc.timeInMillis) / MILLISECONDS_PER_HOUR
        val ut: Double = moment.utc.UT
        val lt = ut + offsetInHours
        return when {
            lt > 24 -> hour + 24.0
            lt < 0 -> hour - 24.0
            else -> hour
        }
    }

    protected abstract fun H0(): Double
    protected abstract fun getHeightFor(moment: IMoment): Double

    companion object {
        private const val TOLERANCE = 0.001
        private const val MILLISECONDS_PER_HOUR = 3600000.0
    }
}