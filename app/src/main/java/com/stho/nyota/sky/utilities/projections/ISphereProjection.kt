package com.stho.nyota.sky.utilities.projections

import com.stho.nyota.sky.utilities.Topocentric
import com.stho.nyota.views.SkyPointF


interface ISphereProjection {
    val projection: Projection
    fun setZoom(zoomAngle: Double, width: Int)
    fun setCenter(centerAzimuth: Double, centerAltitude: Double)
    fun calculateZoomImagePoint(azimuth: Double, altitude: Double): SkyPointF?
    fun calculateAngle(delta: Double): Double
    fun inverseZoomImagePoint(p: SkyPointF): Topocentric?
    val sensitivityAngle: Double
    val zoomAngle: Double

    companion object {
        fun create(projection: Projection) =
            when (projection) {
                Projection.Sphere -> SphereProjection()
                Projection.Gnomonic -> GnomonicProjection()
                Projection.Mercator -> MercatorProjection()
                Projection.Archimedes -> ArchimedesProjection()
                Projection.Stereographic -> StereographicProjection()
            }
    }
}

/**
 * see: https://math.rice.edu/~polking/cartography/cart.pdf
 */
enum class Projection {
    Gnomonic,
    Sphere,
    Stereographic,
    Archimedes,
    Mercator;

    fun serialize(): String =
        toString()

    companion object {
        fun deserialize(value: String): Projection {
            return try {
                valueOf(value)
            } catch (ex: Exception) {
                Gnomonic
            }
        }
    }
}

