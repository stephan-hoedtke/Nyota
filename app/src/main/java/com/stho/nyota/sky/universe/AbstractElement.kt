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
        position = Algorithms.calculateAzimuthAltitude(RA, Decl, moment)
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

