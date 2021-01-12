package com.stho.nyota.sky.utilities.projections

import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.Point

/**
 * Planar gnomonic projection = Central Project from the center of the sphere
 */
class GnomonicProjection : AbstractSphereProjection(), ISphereProjection {

    override val projection: Projection = Projection.GNOMONIC

    override fun calculateAngle(delta: Double): Double =
        Degree.arcTan2(delta, zoom)

    override fun projectImagePoint(x: Double, y: Double, z: Double): Point {
        val factor = 1 / z
        val x2 = x * factor
        val y2 = y * factor
        return Point(x2, y2)
    }
}
