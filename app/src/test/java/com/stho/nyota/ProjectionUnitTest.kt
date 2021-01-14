package com.stho.nyota

import com.stho.nyota.sky.utilities.Topocentric
import com.stho.nyota.sky.utilities.projections.*
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test


// val view: View = Mockito.mock(View::class.java)

class ProjectionUnitTest {

    @Test
    fun test_central_projection() {
        val projection = GnomonicProjection()
        test_projection(projection, 0.0, 0.0, 45.0, Topocentric(10.0, 20.0))
        test_projection(projection, 10.0, 30.0, 55.0, Topocentric(20.0, 40.0))
        test_projection(projection, 20.0, 30.0, 65.0, Topocentric(-20.0, -40.0))
    }

    @Test
    fun test_central_projection_range() {
        val projection = GnomonicProjection()
        test_projection_range(projection, 0.0, 0.0, 45.0)
        test_projection_range(projection, 10.0, 20.0, 45.0)
    }

    @Test
    fun test_stereographic_projection() {
        val projection = StereographicProjection()
        test_projection(projection, 0.0, 0.0, 45.0, Topocentric(10.0, 20.0))
        test_projection(projection, 10.0, 30.0, 55.0, Topocentric(20.0, 40.0))
        test_projection(projection, 20.0, 30.0, 65.0, Topocentric(-20.0, -40.0))
    }

    @Test
    fun test_stereographic_projection_range() {
        val projection = StereographicProjection()
        test_projection_range(projection, 0.0, 0.0, 45.0)
        test_projection_range(projection, 10.0, 20.0, 45.0)
    }

    @Test
    fun test_archimedes_projection() {
        val projection = ArchimedesProjection()
        test_projection(projection, 0.0, 0.0, 45.0, Topocentric(10.0, 20.0))
        test_projection(projection, 10.0, 30.0, 55.0, Topocentric(20.0, 40.0))
        test_projection(projection, 20.0, 30.0, 65.0, Topocentric(-20.0, -40.0))
    }

    @Test
    fun test_archimedes_projection_range() {
        val projection = ArchimedesProjection()
        test_projection_range(projection, 0.0, 0.0, 45.0)
        test_projection_range(projection, 10.0, 20.0, 45.0)
    }

    @Test
    fun test_mercator_projection() {
        val projection = MercatorProjection()
        test_projection(projection, 0.0, 0.0, 45.0, Topocentric(10.0, 20.0))
        test_projection(projection, 10.0, 30.0, 55.0, Topocentric(20.0, 40.0))
        test_projection(projection, 20.0, 30.0, 65.0, Topocentric(-20.0, -40.0))
    }

    @Test
    fun test_mercator_projection_range() {
        val projection = MercatorProjection()
        test_projection_range(projection, 0.0, 0.0, 45.0)
        test_projection_range(projection, 10.0, 20.0, 45.0)
    }

    @Test
    fun test_sphere_projection() {
        val projection = SphereProjection()
        test_projection(projection, 0.0, 0.0, 45.0, Topocentric(10.0, 20.0))
        test_projection(projection, 10.0, 30.0, 55.0, Topocentric(20.0, 40.0))
        test_projection(projection, 20.0, 30.0, 65.0, Topocentric(-20.0, -40.0))
    }

    @Test
    fun test_sphere_projection_range() {
        val projection = SphereProjection()
        test_projection_range(projection, 0.0, 0.0, 45.0)
        test_projection_range(projection, 10.0, 20.0, 45.0)
    }


    private fun test_projection_range(projection: ISphereProjection, centerAzimuth: Double, centerAltitude: Double, zoom: Double) {
        for (relative in relativePositions) {
            val position = getAbsolutePosition(centerAzimuth, centerAltitude, relative)
            test_projection(projection, centerAzimuth, centerAltitude, zoom, position)
        }
    }

    private fun test_projection(projection: ISphereProjection, centerAzimuth: Double, centerAltitude: Double, zoom: Double, position: Topocentric) {
        projection.setCenter(centerAzimuth, centerAltitude)
        projection.setZoom(zoom, width = 1000)
        test_projection(projection, position)
    }

    private fun getAbsolutePosition(centerAzimuth: Double, centerAltitude: Double, relativePosition: Topocentric): Topocentric =
        Topocentric(centerAzimuth + relativePosition.azimuth, centerAltitude + relativePosition.altitude)

    private val relativePositions: ArrayList<Topocentric> by lazy {
        ArrayList<Topocentric>().apply {
            for (a in -30..30 step 15) {
                for (b in -20..20 step 10) {
                    add(Topocentric(a.toDouble(), b.toDouble()))
                }
            }
        }
    }

    private fun test_projection(projection: ISphereProjection, position: Topocentric) {
        val p = projection.calculateZoomImagePoint(position.azimuth, position.altitude)
        if (p == null) {
            Assert.fail("Project failed for $position")
        } else {
            val actual = projection.inverseZoomImagePoint(p)
            if (actual == null) {
                Assert.fail("Inverse project failed for $position and image point $p")
            } else {
                Assert.assertEquals("Invalid azimuth ${actual.azimuth} for $position", position.azimuth, actual.azimuth, 0.01)
                Assert.assertEquals("Invalid altitude ${actual.altitude} for $position", position.altitude, actual.altitude, 0.01)
            }
        }
    }
}