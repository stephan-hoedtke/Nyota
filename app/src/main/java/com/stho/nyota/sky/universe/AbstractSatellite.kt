package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.*
import com.stho.nyota.sky.utilities.Angle

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
            add(com.stho.nyota.R.drawable.horizontal, "Direction", position!!.toString())
            add(com.stho.nyota.R.drawable.horizontal, "Azimuth", Hour.fromDegree(position!!.azimuth))
            add(com.stho.nyota.R.drawable.horizontal, "Altitude", Degree.fromDegree(position!!.altitude))
            add(com.stho.nyota.R.drawable.equatorial, "Height", Formatter.df0.format(location.altitude) + " km")
            add(com.stho.nyota.R.drawable.distance, "Distance", Formatter.df0.format(position!!.distance) + " km")
            add(com.stho.nyota.R.drawable.empty, "Speed", Formatter.df0.format(speed) + " km/h")
        }

    override fun getDetails(moment: Moment): PropertyList =
        PropertyList().apply {
            add(com.stho.nyota.R.drawable.equatorial, "Latitude", Degree.fromDegree(location.latitude))
            add(com.stho.nyota.R.drawable.equatorial, "Longitude", Degree.fromDegree(location.longitude))
            add(com.stho.nyota.R.drawable.compass, "NORAD", tle.noradSatelliteNumber.toString())
            add(com.stho.nyota.R.drawable.empty, "TLE", Formatter.formatDate.format(tle.date))
            add(com.stho.nyota.R.drawable.empty, "Mean Distance", Formatter.df0.format(tle.meanDistanceFromEarth) + " km")
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
}