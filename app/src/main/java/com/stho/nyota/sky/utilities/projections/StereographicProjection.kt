package com.stho.nyota.sky.utilities.projections

import com.stho.nyota.sky.utilities.Point
import com.stho.nyota.sky.utilities.Radian
import com.stho.nyota.views.SkyPoint
import kotlin.math.*

class StereographicProjection  : AbstractSphereProjection(), ISphereProjection {

    override val projection: Projection = Projection.STEREOGRAPHIC

    override fun calculateAngle(delta: Double): Double =
        Radian.toDegrees(delta / zoom)

    override fun projectImagePoint(x1: Double, y1: Double, z1: Double): SkyPoint {
        val factor = 2 / (1 + z1)
        val x2 = x1 * factor
        val y2 = y1 * factor
        return SkyPoint(x2, y2)
    }

    override fun inverseProjection(x2: Double, y2: Double): SkyPoint {
        val s = x2 * x2 + y2 * y2
        val z1 = (4 - s) / (4 + s)
        val factor = (1 + z1) / 2
        val x1 = factor * x2
        val y1 = factor * y2
        return SkyPoint(x1, y1, z1)
    }
}


