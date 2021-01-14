package com.stho.nyota

import android.graphics.PointF
import com.stho.nyota.views.SkyPoint
import com.stho.nyota.views.SkyPointF
import org.junit.Assert
import org.junit.Test

class PointUnitTest {

    /**
     * It seems quite strange:
     * val p = PointF(1f, 2f)
     * results in p.x = 0.0 and p.y = 0.0
     */
    @Test
    fun test_pointF_constructor_isMockedAndroidLibraryClass() {
        val p = PointF(1f, 2f)
        Assert.assertEquals("PointF.x:", 0f, p.x, DELTAF)
        Assert.assertEquals("PointF.y:", 0f, p.y, DELTAF)
    }

    @Test
    fun test_skyPoint_constructor() {
        val p = SkyPointF(1.0f, 2.0f)
        Assert.assertEquals("SkyPoint.x:", 1.0f, p.x, DELTAF)
        Assert.assertEquals("SkyPoint.y:", 2.0f, p.y, DELTAF)

        val q = SkyPoint(1.0, 2.0)
        Assert.assertEquals("SkyPoint.x:", 1.0, q.x, DELTA)
        Assert.assertEquals("SkyPoint.y:", 2.0, q.y, DELTA)
    }

    companion object {
        private const val DELTA: Double = 0.000001
        private const val DELTAF: Float = 0.000001f
    }
}