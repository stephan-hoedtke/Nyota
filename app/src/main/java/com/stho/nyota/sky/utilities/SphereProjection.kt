package com.stho.nyota.sky.utilities

class SphereProjection {
    private var centerAzimuth = 0.0
    private var cos = 0.0
    private var sin = 0.0
    fun setCenter(centerAzimuth: Double, centerAltitude: Double) {
        this.centerAzimuth = centerAzimuth
        cos = Degree.cos(centerAltitude)
        sin = Degree.sin(centerAltitude)
    }

    fun getImagePoint(pointAzimuth: Double, pointAltitude: Double): Point? {
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

    private fun isExcluded(z1: Double): Boolean {
        return z1 < TOLERANCE
    }

    companion object {
        private val TOLERANCE = Degree.cos(75.0)
    }
}