package com.stho.nyota.sky.utilities.projections

import android.graphics.PointF
import android.os.Debug
import android.util.Log
import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.Point
import com.stho.nyota.sky.utilities.Radian
import com.stho.nyota.sky.utilities.Topocentric
import com.stho.nyota.views.SkyPoint
import com.stho.nyota.views.SkyPointF
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.sqrt

abstract class AbstractSphereProjection: ISphereProjection {

    protected var zoom: Double = 1.0
    protected var centerAzimuth = 0.0
    protected var centerAltitude = 0.0
    protected var cos = 0.0
    protected var sin = 0.0

    override fun setZoom(zoomAngle: Double, width: Int) {
        zoom = 0.5 * width / Degree.tan(0.5 * zoomAngle)
    }

    override fun setCenter(centerAzimuth: Double, centerAltitude: Double) {
        this.centerAzimuth = centerAzimuth
        this.centerAltitude = centerAltitude
        cos = Degree.cos(centerAltitude)
        sin = Degree.sin(centerAltitude)
    }

    override val sensitivityAngle: Double
        get() = Radian.toDegrees(1 / zoom)

    override fun calculateZoomImagePoint(azimuth: Double, altitude: Double): SkyPointF? =
        calculateImagePoint(azimuth, altitude)?.let {
            val x3: Float = (zoom * it.x).toFloat()
            val y3: Float = (zoom * it.y).toFloat()
            SkyPointF(x3, -y3)
            // TODO: consider SkyPointF.fromImagePoint(...)
        }

    private fun calculateImagePoint(pointAzimuth: Double, pointAltitude: Double): SkyPoint? {
        val deltaAzimuth = pointAzimuth - centerAzimuth
        val z = Degree.sin(pointAltitude)
        val L = Degree.cos(pointAltitude)
        val x = L * Degree.sin(deltaAzimuth)
        val y = L * Degree.cos(deltaAzimuth)
        val x1 = x
        val y1 = z * cos - y * sin
        val z1 = z * sin + y * cos
        // TODO consider: SkyPoint.fromPosition(pointAzimuth, pointAltitude), p.rotate(cos, sin) -> p = SkyPoint -> project(
        if (z1 < TOLERANCE) {
            return null
        }
        return projectImagePoint(x1, y1, z1)
    }

    override fun inverseZoomImagePoint(p: SkyPointF): Topocentric? {
        val x2: Double = p.x / zoom
        val y2: Double = p.y / zoom
        // // TODO: consider SkyPointF.toImagePoint(...)
        return inverseImagePoint(x2, -y2)
    }

    private fun inverseImagePoint(x2: Double, y2: Double): Topocentric? =
        inverseProjection(x2, y2) ?.let {
            val x1 = it.x
            val y1 = it.y
            val z1 = it.z
            val x = x1
            val y = z1 * cos - y1 * sin
            val z = z1 * sin + y1 * cos
            val altitude = Degree.normalizeTo180(Degree.arcSin(z))
            val azimuth = Degree.normalizeTo180(Degree.arcTan2(x, y))
            Topocentric(azimuth = azimuth + centerAzimuth, altitude = altitude)
        }


    protected abstract fun projectImagePoint(x1: Double, y1: Double, z1: Double): SkyPoint?
    protected abstract fun inverseProjection(x2: Double, y2: Double): SkyPoint?

    /**
     * to avoid projection of points "behind the observer"
     */
    companion object {
        private const val TOLERANCE = 0.17364818 // cos(80°)
    }
}
