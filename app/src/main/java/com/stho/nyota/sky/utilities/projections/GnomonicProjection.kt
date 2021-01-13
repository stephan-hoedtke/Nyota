package com.stho.nyota.sky.utilities.projections

import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.Point
import kotlin.math.sqrt

/**
 * Planar gnomonic projection = Central Project from the center of the sphere
 */
class GnomonicProjection : AbstractSphereProjection(), ISphereProjection {

    override val projection: Projection = Projection.GNOMONIC

    override fun calculateAngle(delta: Double): Double =
        Degree.arcTan2(delta, zoom)

    override fun projectImagePoint(x1: Double, y1: Double, z1: Double): Point {
        val factor = 1 / z1
        val x2 = x1 * factor
        val y2 = y1 * factor
        return Point(x2, y2)
    }

    override fun inverseProjection(x2: Double, y2: Double): Point {
        val x = x2 / sqrt(1 + x2 * x2)
        val y = y2 / sqrt(1 + y2 * y2)
        return Point(x, y)
    }
}
