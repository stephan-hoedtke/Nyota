package com.stho.nyota.sky.utilities.projections

import com.stho.nyota.sky.utilities.Radian
import com.stho.nyota.views.SkyPoint
import kotlin.math.*

class MercatorProjection : AbstractSphereProjection(), ISphereProjection {

    override val projection: Projection = Projection.Gnomonic

    override fun calculateAngle(delta: Double): Double =
        Radian.toDegrees(delta / zoom)

    override fun projectImagePoint(x1: Double, y1: Double, z1: Double): SkyPoint {
        val lambda = atan2(x1, z1)
        val phi = asin(y1)
        val x2 = lambda
        val y2 = ln(tan(PI / 4 + phi / 2))
        return SkyPoint(x2, y2)
    }

    override fun inverseProjection(x2: Double, y2: Double): SkyPoint {
        val lambda = x2
        val phi = 2 * atan(exp(y2)) - PI / 2
        val L = cos(phi)
        val y1 = sin(phi)
        val x1 = L * sin(lambda)
        val z1 = L * cos(lambda)
        return SkyPoint(x1, y1, z1)
    }
}

