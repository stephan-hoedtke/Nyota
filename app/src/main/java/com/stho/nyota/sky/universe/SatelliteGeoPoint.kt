package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.Formatter
import com.stho.nyota.sky.utilities.UTC
import org.osmdroid.util.GeoPoint

class SatelliteGeoPoint internal constructor(latitude: Double, longitude: Double, altitude: Double, private val level: Int, val uTC: UTC, val distance: Double, val sunHeightForObserver: Double, val sunHeightForSatellite: Double, val visible: Boolean) : GeoPoint(latitude, longitude, altitude) {

    val title: String
        get() = """$uTC
Altitude from your location: ${Formatter.df0.format(altitude)}°
Sun at your location: ${Formatter.df0.format(sunHeightForObserver)}°
Sun from satellite: ${Formatter.df0.format(sunHeightForSatellite)}°
Distance: ${Formatter.df0.format(distance)} km"""

    val resourceId: Int
        get() =
            if (visible)
                com.stho.nyota.R.drawable.visible
            else
                when (level) {
                    0 -> com.stho.nyota.R.drawable.p1
                    1 -> com.stho.nyota.R.drawable.p2
                    2 -> com.stho.nyota.R.drawable.p3
                    3 -> com.stho.nyota.R.drawable.p4
                    4 -> com.stho.nyota.R.drawable.p5
                    5 -> com.stho.nyota.R.drawable.p6
                    6 -> com.stho.nyota.R.drawable.p7
                    7 -> com.stho.nyota.R.drawable.p8
                    8 -> com.stho.nyota.R.drawable.p9
                    9 -> com.stho.nyota.R.drawable.p10
                    10 -> com.stho.nyota.R.drawable.p11
                    11 -> com.stho.nyota.R.drawable.p12
                    else -> com.stho.nyota.R.drawable.p13
                }

}