package com.stho.nyota.sky.universe


import com.stho.nyota.sky.utilities.*
import com.stho.nyota.sky.utilities.Topocentric.Companion.INVALID_DISTANCE

/**
 * Created by shoedtke on 31.08.2016.
 */
@Suppress("PropertyName")
abstract class AbstractElement(var RA: Double = 0.0, var Decl: Double = 0.0, var magn: Double = 0.0) : IElement {

    /**
     * Default implementation displays the name. Overridden for stars, as stars may have an empty name.
     */
    override fun toString(): String =
        name

    override var position: Topocentric? = null
        protected set

    // forUTC the position (azimuth and altitude) for a given location and Local Sidereal Time (in angleInHours)
    fun updateAzimuthAltitude(moment: IMoment) {
        val HA = Degree.normalizeTo180(15 * moment.lst - RA) // Hour Angle (HA) is usually given in the interval -12 to +12 angleInHours, or -180 to +180 degrees
        val x = Degree.cos(HA) * Degree.cos(Decl)
        val y = Degree.sin(HA) * Degree.cos(Decl)
        val z = Degree.sin(Decl)
        val latitude = moment.location.latitude
        val xhor = x * Degree.sin(latitude) - z * Degree.cos(latitude)
        val zhor = x * Degree.cos(latitude) + z * Degree.sin(latitude)
        val azimuth = Degree.arcTan2(y, xhor) + 180 // measure from north eastward
        val altitude = Degree.arcTan2(zhor, Math.sqrt(xhor * xhor + y * y))

        // This completes our calculation of the local azimuth and altitude.
        // Note that azimuth is 0 at North, 90 deg at East, 180 deg at South and 270 deg at West.
        // Altitude is of course 0 at the (mathematical) horizon, 90 deg at zenith, and negative below the horizon.
        position = Topocentric(azimuth, altitude)
    }

    override fun getBasics(moment: Moment): PropertyList =
        PropertyList().apply {
            add(PropertyKeyType.DIRECTION, com.stho.nyota.R.drawable.compass, "Direction", position!!.toString())
            add(PropertyKeyType.AZIMUTH, com.stho.nyota.R.drawable.horizontal, "Azimuth", Hour.fromDegree(position!!.azimuth))
            add(PropertyKeyType.ALTITUDE, com.stho.nyota.R.drawable.horizontal, "Altitude", Degree.fromDegree(position!!.altitude))
        }

    override fun getDetails(moment: Moment): PropertyList =
        PropertyList().apply {
            add(com.stho.nyota.R.drawable.equatorial, "Right Ascension", Hour.fromDegree(RA))
            add(com.stho.nyota.R.drawable.equatorial, "Declination", Degree.fromDegree(Decl))
        }

    override val visibility: Int
        get() = when {
            Topocentric.isVisible(position) -> com.stho.nyota.R.drawable.visible
            Topocentric.isBelowHorizon(position) -> com.stho.nyota.R.drawable.invisible
            else -> com.stho.nyota.R.drawable.dizzy
        }

    override val isVisible: Boolean
        get() = Topocentric.isVisible(position)

    override fun isNear(otherPosition: Topocentric, toleranceInDegree: Double): Boolean =
        position?.isNear(otherPosition, toleranceInDegree) ?: false

    override fun isNear(otherPosition: Topocentric, azimuthTolerance: Double, altitudeTolerance: Double): Boolean =
        position?.isNear(otherPosition, azimuthTolerance, altitudeTolerance) ?: false

    override fun distanceTo(otherPosition: Topocentric): Double =
        position?.distanceTo(otherPosition) ?: INVALID_DISTANCE

    companion object {
        const val INVALID_MAGNITUDE: Double = 100.0
    }
}

