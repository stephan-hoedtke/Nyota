package com.stho.nyota.sky.utilities.projections

import com.stho.nyota.sky.utilities.Point
import com.stho.nyota.sky.utilities.Radian

class StereographicProjection  : AbstractSphereProjection(), ISphereProjection {

    override val projection: Projection = Projection.STEREOGRAPHIC

    override fun calculateAngle(delta: Double): Double =
        Radian.toDegrees(delta / zoom)

    override fun projectImagePoint(x: Double, y: Double, z: Double): Point {
        val factor = 2 / (1 + z)
        val x2 = x * factor
        val y2 = y * factor
        return Point(x2, y2)
    }
}


