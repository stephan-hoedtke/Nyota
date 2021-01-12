package com.stho.nyota.sky.utilities.projections

import android.graphics.PointF
import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.Point

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
            val x = (zoom * it.x).toFloat()
            val y = (zoom * it.y).toFloat()
            PointF(x, -y)
        }

    private fun calculateImagePoint(pointAzimuth: Double, pointAltitude: Double): Point? {
        val deltaAzimuth = pointAzimuth - centerAzimuth
        val z = Degree.sin(pointAltitude)
        val L = Degree.cos(pointAltitude)
        val x = L * Degree.sin(deltaAzimuth)
        val y = L * Degree.cos(deltaAzimuth)
        val y1 = z * cos - y * sin
        val z1 = y * cos + z * sin

        if (isExcluded(z1)) {
            return null
        }

        return projectImagePoint(x, y1, z1)
    }


    protected abstract fun projectImagePoint(x: Double, y: Double, z: Double): Point

    /**
     * to avoid projection of points "behind the observer"
     */
    companion object {

        private const val TOLERANCE = 0.17364818 // cos(80°)

        private fun isExcluded(z: Double): Boolean =
            z < TOLERANCE
    }
}
