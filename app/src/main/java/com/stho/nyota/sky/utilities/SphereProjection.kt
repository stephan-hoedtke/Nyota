package com.stho.nyota.sky.utilities

import android.graphics.PointF

class SphereProjection {
    private var zoom: Double = 1.0
    private var centerAzimuth = 0.0
    private var cos = 0.0
    private var sin = 0.0

    fun setZoom(zoomAngle: Double, width: Int) {
        zoom = 0.5 * width / Degree.tan(0.5 * zoomAngle)
    }

    fun setCenter(centerAzimuth: Double, centerAltitude: Double) {
        this.centerAzimuth = centerAzimuth
        cos = Degree.cos(centerAltitude)
        sin = Degree.sin(centerAltitude)
    }

    fun calculateZoomImagePoint(azimuth: Double, altitude: Double): PointF? =
        calculateImagePoint(azimuth, altitude) ?.let {
            val x = (zoom * it.x).toFloat()
            val y = (zoom * it.y).toFloat()
            PointF(x, -y)
        }

    fun calculateAngle(delta: Double): Double =
        Degree.arcTan2(delta, zoom)

    private fun calculateImagePoint(pointAzimuth: Double, pointAltitude: Double): Point? {
        val deltaAzimuth = pointAzimuth - centerAzimuth
        val z = Degree.sin(pointAltitude)
        val L = Degree.cos(pointAltitude)
        val x = L * Degree.sin(deltaAzimuth)
        val y = L * Degree.cos(deltaAzimuth)
        val y1 = z * cos - y * sin
        val z1 = y * cos + z * sin
        if (isExcluded(z1)) return null
        val factor = 1 / (y * cos + z * sin)
        val x2 = x * factor
        val y2 = y1 * factor
        return Point(x2, y2)
    }

    private fun isExcluded(z1: Double): Boolean =
        z1 < TOLERANCE

    companion object {
        private val TOLERANCE = Degree.cos(75.0)
    }
}