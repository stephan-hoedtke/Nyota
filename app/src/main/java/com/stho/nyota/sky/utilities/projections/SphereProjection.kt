package com.stho.nyota.sky.utilities.projections

import com.stho.nyota.sky.utilities.Point
import com.stho.nyota.sky.utilities.Radian
import kotlin.math.*

class SphereProjection : AbstractSphereProjection(), ISphereProjection {

    override val projection: Projection = Projection.SPHERE

    override fun calculateAngle(delta: Double): Double =
        Radian.toDegrees(delta / zoom)

    override fun projectImagePoint(x: Double, y: Double, z: Double): Point {
        val factor = 1 / z
        val x2 = x * factor
        val y2 = y * factor
        val lambda = atan(x2)
        val phi = atan(y2 * cos(lambda)) // TODO make it better! (a)  atan(y2 * cos(lambda)) or (b) atan(y2)
        return Point(lambda, phi)
    }

    override fun inverseProjection(x2: Double, y2: Double): Point {
        val xx = tan(x2)
        val yy = tan(y2) / cos(x2)
        val x = xx / sqrt(1 + xx * xx)
        val y = yy / sqrt(1 + yy * yy)
        return Point(x, y)
    }
}



