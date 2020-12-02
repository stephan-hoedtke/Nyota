package com.stho.nyota.sky.utilities

import android.location.Location
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

    override fun toString(): String {
        return toString(latitude, longitude)
    }

    internal fun toGeoPoint(): GeoPoint {
        return GeoPoint(latitude, longitude, altitude)
    }

    companion object {

        internal fun fromAndroidLocation(location: android.location.Location): com.stho.nyota.sky.utilities.Location {
            return Location(location.latitude, location.longitude, location.altitudeInKm())
        }

        internal fun fromGeoPoint(geoPoint: GeoPoint): com.stho.nyota.sky.utilities.Location {
            return Location(geoPoint.latitude, geoPoint.longitude, geoPoint.altitude)
        }

        fun toString(location: android.location.Location): String {
            return toString(location.latitude, location.longitude)
        }

        fun toString(latitude: Double, longitude: Double): String {
            return Angle.toString(longitude, Angle.AngleType.LONGITUDE) + Formatter.SPACE + Angle.toString(latitude, Angle.AngleType.LATITUDE)
        }

        val defaultLocation: com.stho.nyota.sky.utilities.Location
            get() = com.stho.nyota.sky.utilities.Location(0.0,0.0)
    }
}