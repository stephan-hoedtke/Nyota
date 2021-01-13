package com.stho.nyota.sky.utilities.projections

import com.stho.nyota.sky.utilities.Point
import com.stho.nyota.sky.utilities.Radian
import kotlin.math.sqrt

class ArchimedesProjection : AbstractSphereProjection(), ISphereProjection {

    override val projection: Projection = Projection.ARCHIMEDES

    override fun calculateAngle(delta: Double): Double =
        Radian.toDegrees(delta / zoom)

    override fun projectImagePoint(x1: Double, y1: Double, z1: Double): Point {
        return Point(x1, y1)
    }

    override fun inverseProjection(x2: Double, y2: Double): Point {
        return Point(x2, y2)
    }
}

