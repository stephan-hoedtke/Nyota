package com.stho.nyota.sky.utilities.projections

import com.stho.nyota.sky.utilities.Radian
import com.stho.nyota.views.SkyPoint
import kotlin.math.sqrt

class ArchimedesProjection : AbstractSphereProjection(), ISphereProjection {

    override val projection: Projection = Projection.Archimedes

    override fun calculateAngle(delta: Double): Double =
        Radian.toDegrees(delta / zoom)

    override fun projectImagePoint(x1: Double, y1: Double, z1: Double): SkyPoint {
        return SkyPoint(x1, y1)
    }

    override fun inverseProjection(x2: Double, y2: Double): SkyPoint? {
        val Q = 1 - x2 * x2 - y2 * y2
        if (Q < 0) {
            return null
        }
        return SkyPoint(x2, y2, sqrt(Q))
    }
}

