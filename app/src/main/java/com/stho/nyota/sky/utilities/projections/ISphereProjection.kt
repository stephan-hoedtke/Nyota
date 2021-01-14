package com.stho.nyota.sky.utilities.projections

import com.stho.nyota.sky.utilities.Topocentric
import com.stho.nyota.views.SkyPoint
import com.stho.nyota.views.SkyPointF


interface ISphereProjection {
    val projection: Projection
    fun setZoom(zoomAngle: Double, width: Int)
    fun setCenter(centerAzimuth: Double, centerAltitude: Double)
    fun calculateZoomImagePoint(azimuth: Double, altitude: Double): SkyPointF?
    fun calculateAngle(delta: Double): Double
    fun inverseZoomImagePoint(p: SkyPointF): Topocentric?

    companion object {
        fun create(projection: Projection) =
            when (projection) {
                Projection.SPHERE -> SphereProjection()
                Projection.GNOMONIC -> GnomonicProjection()
                Projection.MERCATOR -> MercatorProjection()
                Projection.ARCHIMEDES -> ArchimedesProjection()
                Projection.STEREOGRAPHIC -> StereographicProjection()
            }
    }
}

/**
 * see: https://math.rice.edu/~polking/cartography/cart.pdf
 */
enum class Projection {
    GNOMONIC,
    SPHERE,
    STEREOGRAPHIC,
    ARCHIMEDES,
    MERCATOR;

    fun serialize(): String =
        toString()

    companion object {
        fun deserialize(value: String): Projection {
            return try {
                Projection.valueOf(value)
            } catch (ex: Exception) {
                Projection.GNOMONIC
            }
        }
    }
}
