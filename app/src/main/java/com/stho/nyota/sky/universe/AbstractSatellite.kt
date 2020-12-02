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

    override fun getBasics(moment: Moment): PropertyList {
        val basics = PropertyList()
        basics.add(com.stho.nyota.R.drawable.horizontal, Angle.toString(position!!.azimuth, Angle.AngleType.AZIMUTH) + Formatter.SPACE + Angle.toString(position!!.altitude, Angle.AngleType.ALTITUDE))
        return basics
    }

    override fun getDetails(moment: Moment): PropertyList {
        val details = PropertyList()
        details.add(com.stho.nyota.R.drawable.horizontal, "Azimuth", Hour.fromDegree(position!!.azimuth))
        details.add(com.stho.nyota.R.drawable.horizontal, "Altitude", Degree.fromDegree(position!!.altitude))
        details.add(com.stho.nyota.R.drawable.equatorial, "Latitude", Degree.fromDegree(location.latitude))
        details.add(com.stho.nyota.R.drawable.equatorial, "Longitude", Degree.fromDegree(location.longitude))
        details.add(com.stho.nyota.R.drawable.equatorial, "Height", Formatter.df0.format(location.altitude) + " km")
        details.add(com.stho.nyota.R.drawable.distance, "Distance", Formatter.df0.format(position!!.distance) + " km")
        details.add(com.stho.nyota.R.drawable.star, "Speed", Formatter.df0.format(speed) + " km/h")
        details.add(com.stho.nyota.R.drawable.time, "TLE", Formatter.formatDate.format(tle.date))
        return details
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