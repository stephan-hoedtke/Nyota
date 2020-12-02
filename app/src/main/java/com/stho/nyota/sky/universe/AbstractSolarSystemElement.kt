package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.*
import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.Degree.Companion.arcCos
import com.stho.nyota.sky.utilities.Degree.Companion.arcTan2
import com.stho.nyota.sky.utilities.Degree.Companion.normalize
import com.stho.nyota.sky.utilities.Degree.Companion.normalizeTo180
import com.stho.nyota.sky.utilities.Degree.Companion.sinus
import com.stho.nyota.sky.utilities.Degree.Companion.cosines
import com.stho.nyota.sky.utilities.Degree.Companion.tangent
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
    protected var mr = 0.0 // mean radius = 0.0
    protected var w1 = 0.0 // longitude of perihelion = 0.0
    protected var pq = 0.0 // perihelion distance = 0.0
    protected var Q = 0.0 // aphelion distance = 0.0
    protected var P = 0.0 // orbital period (years of a is in AU) = 0.0
    protected var T = 0.0 // time of perihelion = 0.0
    protected var v = 0.0 // true anomaly (angle between position and perihelion) = 0.0
    protected var ecl = 0.0 // obliquity of the ecliptic, i.e. the "tilt" of the earths axis of rotation = 0.0
    var R = 0.0 // geocentric distance = 0.0
    protected var L = 0.0 // mean longitude = 0.0
    protected var E: Double = 0.0 // eccentric anomaly = 0.0
    protected var longitude = 0.0 // elliptic heliocentric longitude = 0.0
    protected var latitude = 0.0 // elliptic heliocentric latitude = 0.0
    protected var x = 0.0
    protected var y = 0.0
    protected var z = 0.0
    protected var elongation = 0.0 // the apparent angular distance of the planet from the sun = 0.0
    protected var FV = 0.0
    var phase = 0.0
    var phaseAngle = 0.0
    var parallacticAngle = 0.0
    var zenithAngle = 0.0

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
        var E0 = M + Degree.RADEG * e * sinus(M) * (1.0 + e * cosines(M))
        var E1 = E0 - (E0 - Degree.RADEG * e * sinus(E0) - M) / (1 - e * cosines(E0))
        run {
            var x = 0
            while (x < 10 && Math.abs(E1 - E0) > 0.001) {
                E0 = E1
                E1 = E0 - (E0 - Degree.RADEG * e * sinus(E0) - M) / (1 - e * cosines(E0))
                x++
            }
        }
        E = normalize(E1)
        val xv = a * (cosines(E) - e)
        val yv = a * (Math.sqrt(1.0 - e * e) * sinus(E))
        v = normalize(arcTan2(yv, xv))
        mr = Math.sqrt(xv * xv + yv * yv)

        // mean longitude
        val lon = v + w

        // position int the 3 dimensional space
        // for the moon: geocentric position in the ecliptic coordinate system
        // for planets: heliocentric position in the ecliptic coordinate system
        val xh = mr * (cosines(N) * cosines(lon) - sinus(N) * sinus(lon) * cosines(i))
        val yh = mr * (sinus(N) * cosines(lon) + cosines(N) * sinus(lon) * cosines(i))
        val zh = mr * (sinus(lon) * sinus(i))

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
        x = mr * cosines(longitude) * cosines(latitude)
        y = mr * sinus(longitude) * cosines(latitude)
        z = mr * sinus(latitude)

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
        val ye = yg * cosines(ecl) - zg * sinus(ecl)
        val ze = yg * sinus(ecl) + zg * cosines(ecl)

        // Finally, compute the planet's Right Ascension (RA) and Declination (Decl):
        RA = normalize(arcTan2(ye, xe))
        Decl = normalizeTo180(arcTan2(ze, Math.sqrt(xe * xe + ye * ye)))

        // Compute the geocentric distance:
        R = Math.sqrt(xg * xg + yg * yg + zg * zg)
    }

    open fun calculatePhase(sun: Sun) {
        elongation = arcCos(cosines(sun.longitude - longitude) * cosines(latitude))
        FV = 180 - elongation
        phase = (1 + cosines(FV)) / 2
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
                cosines(sun.Decl) * sinus(sun.RA - RA),
                sinus(sun.Decl) * cosines(Decl) - cosines(sun.Decl) * sinus(Decl) * cosines(sun.RA - RA))
    }

    protected fun getZenithAngle(sun: Sun): Double {
        // Calculation from altitude doesn't require correction by the parallactic-angle, but the current altitude and azimuth
        return if (sun.position != null) {
            arcTan2(
                    cosines(sun.position!!.altitude) * sinus(position!!.azimuth - sun.position!!.azimuth),
                    sinus(sun.position!!.altitude) * cosines(position!!.altitude) - cosines(sun.position!!.altitude) * sinus(position!!.altitude) * cosines(position!!.azimuth - sun.position!!.azimuth))
        } else {
            0.0;
        }
    }

    protected fun getParallacticAngle(moment: IMoment): Double {
        val HA = normalize(15 * moment.lst - RA)
        return arcTan2(sinus(HA), tangent(moment.location.latitude) * cosines(Decl) - sinus(Decl) * cosines(HA))
    }

    open val distanceInKm: Double
        get() = Algorithms.ASTRONOMIC_UNIT * R

    override fun getBasics(moment: Moment): PropertyList {
        val basics = super.getBasics(moment)
        basics.add(com.stho.nyota.R.drawable.distance, Formatter.df0.format(distanceInKm) + " km")
        return basics
    }

    override fun getDetails(moment: Moment): PropertyList {
        val details = super.getDetails(moment)
        details.add(com.stho.nyota.R.drawable.distance, "Distance", Formatter.df0.format(distanceInKm) + " km")
        details.add(com.stho.nyota.R.drawable.angle, "Elongation", Degree.fromDegree(elongation))
        return details
    }

    open fun calculateSetRiseTimes(moment: IMoment) {
        val inSouth = getTimeInSouth(moment)
        val cos_LHA = getHourAngle(moment.location.latitude)
        position!!.inSouth = moment.utc.setHours(inSouth)
        position!!.culmination = getHeightFor(moment.forUTC(position!!.inSouth!!))

        // cos_LHA < 0 ---> always up and visible
        // cos_LHA > 0 ---> always down
        if (cos_LHA > -1 && cos_LHA < 1) {
            val LHA = arcCos(cos_LHA) / 15
            val tolerance = moment.location.longitude / 15 + 1.5 // current timezone with potential daylight savings?
            position!!.riseTime = iterate(moment, moment.utc.setHours(inSouth - LHA), true)
            position!!.setTime = iterate(moment, moment.utc.setHours(inSouth + LHA), false)
            if (inSouth - LHA < 0.0 + tolerance || position!!.riseTime!!.isLessThan(position!!.setTime!!) && position!!.setTime!!.isLessThan(moment.utc)) position!!.nextRiseTime = iterate(moment, position!!.riseTime!!.addHours(24.45), true)
            if (inSouth + LHA > 24.0 - tolerance || position!!.riseTime!!.isGreaterThan(moment.utc) && position!!.setTime!!.isGreaterThan(position!!.riseTime!!)) position!!.prevSetTime = iterate(moment, position!!.setTime!!.addHours(-24.45), false)
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
        return (sinus(H0()) - sinus(observerLatitude) * sinus(Decl)) / (cosines(observerLatitude) * cosines(Decl))
    }

    // Calculate the time (in UTC) when the sun will be in south at this position (longitude is defined by LST)
    private fun getTimeInSouth(moment: IMoment): Double {
        var hour = Hour.normalize(RA / 15 - moment.utc.gMST0 - moment.location.longitude / 15)
        val offsetInHours = TimeUnit.HOURS.convert(moment.timeZone.getOffset(moment.utc.timeInMillis).toLong(), TimeUnit.MILLISECONDS).toDouble()
        val ut: Double = moment.utc.uT
        val lt = ut + offsetInHours
        if (lt > 24) hour += 24.0 else if (lt < 0) hour -= 24.0
        return hour
    }

    protected abstract fun H0(): Double
    protected abstract fun getHeightFor(moment: IMoment): Double

    companion object {
        private const val TOLERANCE = 0.001
    }
}