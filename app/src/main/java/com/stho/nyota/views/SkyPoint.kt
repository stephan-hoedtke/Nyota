package com.stho.nyota.views

import android.view.MotionEvent
import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.Topocentric

/**
 * representing an celestial element on the sky in cartesian coordinates P=x,y,z
 */
data class SkyPoint(val x: Double, val y: Double, val z: Double = 0.0) {

    companion object {
        fun fromPosition(azimuth: Double, altitude: Double): SkyPoint {
            val z = Degree.sin(altitude)
            val L = Degree.cos(altitude)
            val x = L * Degree.sin(azimuth)
            val y = L * Degree.cos(azimuth)
            return SkyPoint(x, y, z)
        }
    }
}

/**
 * representing an element on the screen be measured from the middle of the screen P=x,y in screen resolution
 */
data class SkyPointF(val x: Float, val y: Float) {

    fun plus(dx: Double, dy: Double): SkyPointF =
        SkyPointF(x + dx.toFloat(), y + dy.toFloat())

    fun minus(dx: Double, dy: Double): SkyPointF =
        SkyPointF(x - dx.toFloat(), y - dy.toFloat())

    companion object {
        fun forMotionEvent(e: MotionEvent, width: Int, height: Int): SkyPointF =
            SkyPointF(e.x - width / 2, e.y - height / 2)

        fun forImagePoint(p: SkyPoint, zoom: Double): SkyPointF {
            val x: Float = (zoom * p.x).toFloat()
            val y: Float = (zoom * p.y).toFloat()
            return SkyPointF(x, -y)
        }
    }
}


