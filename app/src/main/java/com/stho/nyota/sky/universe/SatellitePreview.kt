package com.stho.nyota.sky.universe

import com.stho.nyota.sky.universe.AbstractSolarSystem.Companion.getSunFor
import com.stho.nyota.sky.universe.Algorithms.calculateNewLocationFromBearingDistance
import com.stho.nyota.sky.universe.Algorithms.getLocationForECI
import com.stho.nyota.sky.universe.Algorithms.getTopocentricFromPosition
import com.stho.nyota.sky.universe.Algorithms.getVisibilityRadius
import com.stho.nyota.sky.universe.SatelliteAlgorithms.calculatePositionVelocity
import com.stho.nyota.sky.utilities.*
import com.stho.nyota.sky.utilities.Formatter
import com.stho.nyota.sky.utilities.UTC.Companion.forNow
import com.stho.nyota.sky.utilities.Vector
import org.osmdroid.util.GeoPoint
import java.util.*
import kotlin.collections.ArrayList


class SatellitePreview {

    companion object {

        private const val MINIMAL_VISIBILITY_ELONGATION = 20.0
        private const val COUNT = 300
        private const val LIMIT = 4000
        private const val PREVIEW_INTERVAL_IN_HOURS = 1.0 / 60.0

        fun getPreviewPoints(satellite: Satellite, observer: Moment): List<SatelliteGeoPoint> {
            val points: MutableList<SatelliteGeoPoint> = ArrayList()
            var utc = forNow()
            var visible = false
            var i = 0
            while (i < LIMIT) {
                val level = 13 * i / COUNT
                val point = getPreviewPoint(satellite, observer, utc, level)
                if (i < COUNT || point.isVisible) {
                    points.add(point)
                    visible = visible || point.isVisible
                } else {
                    if (i > COUNT && visible)
                        break
                }
                i++
                utc = utc.addHours(PREVIEW_INTERVAL_IN_HOURS)
            }
            return points
        }

        private fun getPreviewPoint(satellite: Satellite, observer: Moment, utc: UTC, level: Int): SatelliteGeoPoint {
            val julianDay = utc.julianDay
            val position = Vector()
            calculatePositionVelocity(satellite.tle, julianDay, position, null)
            val location = getLocationForECI(position, julianDay)
            val topocentric = getTopocentricFromPosition(observer.location, julianDay, position)
            val sunForObserver = getSunFor(observer.location, utc)
            val sunForSatellite = getSunFor(location, utc)
            val isDark = sunForObserver.isDark
            val isReflecting = sunForSatellite.isVisibleAt(satellite.location.altitude)
            val isAboveHorizon = topocentric.altitude > MINIMAL_VISIBILITY_ELONGATION
            val isVisible = isDark && isReflecting && isAboveHorizon
            return SatelliteGeoPoint(
                location.latitude,
                location.longitude,
                topocentric.altitude,
                level,
                utc, observer.timeZone,
                topocentric.distance,
                sunForObserver.position!!.altitude,
                sunForSatellite.position!!.altitude,
                isVisible)
        }

        private fun getSunFor(location: Location, utc: UTC): Sun {
            val city: City = City.createNewCityFor(location)
            val moment = Moment.forUTC(city, utc)
            return getSunFor(moment)
        }

        fun getVisibilityPoints(satellite: Satellite): List<GeoPoint> {
            val points: MutableList<GeoPoint> = ArrayList()
            val altitude: Double = satellite.location.altitude
            val radius: Double = getVisibilityRadius(altitude, MINIMAL_VISIBILITY_ELONGATION)
            val alpha: Double = 360.0 / 90.0
            var bearing: Double = 0.0
            while (bearing < 360) {
                val location = calculateNewLocationFromBearingDistance(satellite.location, bearing, radius)
                points.add(GeoPoint(location.latitude, location.longitude))
                bearing += alpha;
            }
            return points
        }

    }
}

