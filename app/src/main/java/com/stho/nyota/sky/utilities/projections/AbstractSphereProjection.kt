package com.stho.nyota.sky.utilities.projections

import android.graphics.PointF
import android.os.Debug
import android.util.Log
import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.Point
import com.stho.nyota.sky.utilities.Topocentric
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

    override fun calculateZoomImagePoint(azimuth: Double, altitude: Double): PointF? =
        calculateImagePoint(azimuth, altitude) ?.let {
            val x: Float = (zoom * it.x).toFloat()
            val y: Float = (zoom * it.y).toFloat()
            PointF(x, -y)
        }

    private fun calculateImagePoint(pointAzimuth: Double, pointAltitude: Double): Point? {
        val deltaAzimuth = pointAzimuth - centerAzimuth
        val z = Degree.sin(pointAltitude)
        val L = Degree.cos(pointAltitude)
        val x = L * Degree.sin(deltaAzimuth)
        val y = L * Degree.cos(deltaAzimuth)
        val x1 = x
        val y1 = z * cos - y * sin
        val z1 = z * sin + y * cos

        if (isExcluded(z1)) {
            return null
        }

        return projectImagePoint(x1, y1, z1)
    }

    override fun inverseZoomImagePoint(p: PointF): Topocentric? {
        val x2: Double = p.x / zoom
        val y2: Double = p.y / zoom
        return inverseImagePoint(x2, y2)
    }

    private fun inverseImagePoint(x2: Double, y2: Double): Topocentric? =
        inverseProjection(x2, y2) ?.let {
            val x1 = it.x
            val y1 = it.y
            val Q = 1 - x1 * x1 - y1 * y1
            if (Q > 1) {
                return null
            }
            val z1 = sqrt(Q)
            val x = x1
            val y = z1 * cos - y1 * sin
            val z = z1 * sin + y1 * cos
            val altitude = Degree.normalizeTo180(Degree.arcSin(z))
            val azimuth = Degree.normalizeTo180(Degree.arcTan2(x, y))
            Topocentric(azimuth = azimuth + centerAzimuth, altitude = altitude)
        }


    protected abstract fun projectImagePoint(x1: Double, y1: Double, z1: Double): Point
    protected abstract fun inverseProjection(x2: Double, y2: Double): Point?

    /**
     * to avoid projection of points "behind the observer"
     */
    companion object {

        private const val TOLERANCE = 0.17364818 // cos(80°)

        private fun isExcluded(z: Double): Boolean =
            z < TOLERANCE
    }
}
