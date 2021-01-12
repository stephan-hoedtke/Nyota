package com.stho.nyota.sky.utilities.projections

import com.stho.nyota.sky.utilities.Point
import com.stho.nyota.sky.utilities.Radian

class ArchimedesProjection : AbstractSphereProjection(), ISphereProjection {

    override val projection: Projection = Projection.ARCHIMEDES

    override fun calculateAngle(delta: Double): Double =
        Radian.toDegrees(delta / zoom)

    override fun projectImagePoint(x: Double, y: Double, z: Double): Point {
        return Point(x, y)
    }
}

