package com.stho.nyota.sky.utilities

import com.stho.nyota.sky.universe.Algorithms
import com.stho.nyota.sky.universe.altitudeInKm
import org.osmdroid.util.GeoPoint


/**
 * GEO-Location with latitude in degrees, longitude in degrees, altitude in km
 *
 * Define a new GEO-location.
 * @param latitude in degrees
 * @param longitude in degrees
 * @param altitude in km
 */
class Location(override val latitude: Double, override val longitude: Double, override val altitude: Double = 0.0) : ILocation {

    override fun toString(): String =
        toString(latitude, longitude)

    internal fun toGeoPoint(): GeoPoint =
        GeoPoint(latitude, longitude, altitude)

    fun isNearTo(otherLocation: ILocation): Boolean =
        getHorizontalDistanceInKmTo(otherLocation) < TEN_METERS_IN_KM

    fun getHorizontalDistanceInKmTo(otherLocation: ILocation): Double =
        Algorithms.getHorizontalDistanceInKmTo(latitude, longitude, otherLocation.latitude, otherLocation.longitude)

    companion object {

        private const val TEN_METERS_IN_KM = 0.01

        internal fun fromAndroidLocation(location: android.location.Location): Location =
            Location(location.latitude, location.longitude, location.altitudeInKm())

        internal fun fromGeoPoint(geoPoint: GeoPoint): Location =
            Location(geoPoint.latitude, geoPoint.longitude, geoPoint.altitude)

        fun toString(location: android.location.Location): String =
            toString(location.latitude, location.longitude)

        fun toString(latitude: Double, longitude: Double): String =
            Angle.toString(longitude, Angle.AngleType.LONGITUDE) + Formatter.SPACE + Angle.toString(latitude, Angle.AngleType.LATITUDE)

        fun getDefault(): Location =
            Location(0.0,0.0)
    }
}