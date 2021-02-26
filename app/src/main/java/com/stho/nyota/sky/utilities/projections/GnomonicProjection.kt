package com.stho.nyota.sky.utilities.projections

import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.views.SkyPoint
import kotlin.math.sqrt

/**
 * Planar gnomonic projection = Central Project from the center of the sphere
 */
class GnomonicProjection : AbstractSphereProjection(), ISphereProjection {

    override val projection: Projection = Projection.Gnomonic

    override fun calculateAngle(delta: Double): Double =
        Degree.arcTan2(delta, zoom)

    override fun projectImagePoint(x1: Double, y1: Double, z1: Double): SkyPoint {
        val factor = 1 / z1
        val x2 = factor * x1
        val y2 = factor * y1
        return SkyPoint(x2, y2)
    }

    override fun inverseProjection(x2: Double, y2: Double): SkyPoint {
        val factor = 1 / sqrt(1 + x2 * x2 + y2 * y2)
        val z1 = factor
        val x1 = factor * x2
        val y1 = factor * y2
        return SkyPoint(x1, y1, z1)
    }
}
