package com.stho.nyota.sky.utilities.projections

import com.stho.nyota.sky.utilities.Point
import com.stho.nyota.sky.utilities.Radian
import kotlin.math.*

class MercatorProjection : AbstractSphereProjection(), ISphereProjection {

    override val projection: Projection = Projection.GNOMONIC

    override fun calculateAngle(delta: Double): Double =
        Radian.toDegrees(delta / zoom)

    override fun projectImagePoint(x1: Double, y1: Double, z1: Double): Point {
        val factor = 1 / z1
        val x2 = x1 * factor
        val y2 = y1 * factor
        val lambda = atan(x2)
        val phi = atan(y2 * cos(lambda))
        val x3 = lambda
        val y3 = ln(tan(PI / 4 + phi / 2))
        return Point(x3, y3)
    }

    override fun inverseProjection(x3: Double, y3: Double): Point? {
        val lambda = x3
        val phi = 2 * atan(exp(y3) - PI / 2)
        val x2 = tan(lambda)
        val y2 = tan(phi) / cos(lambda)
        val x = x2 / sqrt(1 + x2 * x2)
        val y = y2 / sqrt(1 + y2 * y2)
        return Point(x, y)
    }
}

