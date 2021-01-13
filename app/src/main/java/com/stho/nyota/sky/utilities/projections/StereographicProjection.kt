package com.stho.nyota.sky.utilities.projections

import com.stho.nyota.sky.utilities.Point
import com.stho.nyota.sky.utilities.Radian
import kotlin.math.*

class StereographicProjection  : AbstractSphereProjection(), ISphereProjection {

    override val projection: Projection = Projection.STEREOGRAPHIC

    override fun calculateAngle(delta: Double): Double =
        Radian.toDegrees(delta / zoom)

    override fun projectImagePoint(x1: Double, y1: Double, z1: Double): Point {
        val factor = 2 / (1 + z1)
        val x2 = x1 * factor
        val y2 = y1 * factor
        return Point(x2, y2)
    }

    override fun inverseProjection(x2: Double, y2: Double): Point {
        val chi = atan(x2 / 2)
        val phi = PI / 2 - 2 * chi
        val z1 = tan(phi)
        val factor = (1 - z1) / 2
        val x1 = x2 * factor
        val y1 = y2 * factor
        val ax = cos(phi)
        return Point(x1, y1)
    }
}


