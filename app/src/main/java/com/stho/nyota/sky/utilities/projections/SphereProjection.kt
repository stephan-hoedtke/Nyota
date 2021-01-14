package com.stho.nyota.sky.utilities.projections

import com.stho.nyota.sky.utilities.Point
import com.stho.nyota.sky.utilities.Radian
import com.stho.nyota.views.SkyPoint
import kotlin.math.*

class SphereProjection : AbstractSphereProjection(), ISphereProjection {

    override val projection: Projection = Projection.SPHERE

    override fun calculateAngle(delta: Double): Double =
        Radian.toDegrees(delta / zoom)

    override fun projectImagePoint(x1: Double, y1: Double, z1: Double): SkyPoint {
        val factor = 1 / z1
        val x2 = factor * x1
        val y2 = factor * y1
        val lambda = atan(x2)
        val phi = atan(y2 * cos(lambda)) // TODO make it better! (a)  atan(y2 * cos(lambda)) or (b) atan(y2)
        return SkyPoint(lambda, phi)
    }

    override fun inverseProjection(x2: Double, y2: Double): SkyPoint {
        val lambda = x2
        val phi = y2
        val xx = tan(lambda)
        val yy = tan(phi) / cos(lambda)
        val factor = 1 / sqrt(1 + xx * xx + yy * yy)
        val z1 = factor
        val x1 = factor * xx
        val y1 = factor * yy
        return SkyPoint(x1, y1, z1)
    }
}



