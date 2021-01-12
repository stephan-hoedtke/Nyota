package com.stho.nyota.sky.utilities.projections

import com.stho.nyota.sky.utilities.Point
import com.stho.nyota.sky.utilities.Radian
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sin

class SphereProjection : AbstractSphereProjection(), ISphereProjection {

    override val projection: Projection = Projection.SPHERE

    override fun calculateAngle(delta: Double): Double =
        Radian.toDegrees(delta / zoom)

    override fun projectImagePoint(x: Double, y: Double, z: Double): Point {
        val factor = 1 / z
        val x2 = x * factor
        val y2 = y * factor
        val x3 = atan(x2) // azimuth
        val y3 = atan(y2 * cos(x3)) // altitude
        return Point(x, y)
    }
}

