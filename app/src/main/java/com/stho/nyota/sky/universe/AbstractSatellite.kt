package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.*
import com.stho.nyota.sky.utilities.Formatter
import com.stho.nyota.sky.utilities.Vector

/**
 * Created by shoedtke on 24.01.2017.
 */
abstract class AbstractSatellite : IElement {

    abstract val tle: TLE

    override var position: Topocentric? = null
        protected set

    protected val positionVector: Vector = Vector()

    protected val velocity: Vector = Vector()

    var location: Location = Location(0.0, 0.0, 0.0)

    abstract fun updateFor(julianDay: Double)

    /**
     * Speed in km/h
     * @return
     */
    val speed: Double
        get() = 60 * velocity.length

    override fun getBasics(moment: Moment): PropertyList =
        PropertyList().apply {
            add(com.stho.nyota.R.drawable.compass, "Direction", position!!.toString())
            add(com.stho.nyota.R.drawable.horizontal, "Azimuth", Hour.fromDegree(position!!.azimuth))
            add(com.stho.nyota.R.drawable.horizontal, "Altitude", Degree.fromDegree(position!!.altitude))
            add(com.stho.nyota.R.drawable.equatorial, "Height", Formatter.toDistanceKmString(location.altitude))
            add(com.stho.nyota.R.drawable.distance, "Distance", Formatter.toDistanceKmString(position!!.distance))
            add(com.stho.nyota.R.drawable.empty, "Speed", Formatter.toSpeedString(speed))
        }

    override fun getDetails(moment: Moment): PropertyList =
        PropertyList().apply {
            add(com.stho.nyota.R.drawable.equatorial, "Latitude", Degree.fromDegree(location.latitude))
            add(com.stho.nyota.R.drawable.equatorial, "Longitude", Degree.fromDegree(location.longitude))
            add(com.stho.nyota.R.drawable.compass, "NORAD", tle.noradSatelliteNumber.toString())
            add(com.stho.nyota.R.drawable.empty, "TLE", Formatter.toString(tle.date, Formatter.timeZoneGMT, Formatter.TimeFormat.DATETIME_TIMEZONE))
            add(com.stho.nyota.R.drawable.empty, "Mean Distance", Formatter.toDistanceKmString(tle.meanDistanceFromEarth))
            add(com.stho.nyota.R.drawable.empty, "Revolutions", Formatter.df2.format(tle.revolutionsPerDay))
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
        position?.distanceTo(otherPosition) ?: Topocentric.INVALID_DISTANCE

}