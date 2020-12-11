package com.stho.nyota

import com.stho.nyota.sky.universe.Satellite
import com.stho.nyota.sky.universe.Universe
import com.stho.nyota.sky.utilities.City
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.sky.utilities.createDefaultBerlin
import org.junit.Assert
import org.junit.Test

/**
 * Created by shoedtke on 31.01.2017.
 */
class PerformanceUnitTest {

    private val universe: Universe
        get() {
            val city: City = City.createDefaultBerlin()
            val moment: Moment = Moment.forNow(city)
            return Universe().apply { updateFor(moment, false) }
        }

    @Test
    fun updateUniverse_Performance_isSufficient() {
        val startTime = System.currentTimeMillis()
        universe.apply {
            for (i in 0 until MAX) {
                updateForNow()
            }
        }
        val elapsed = System.currentTimeMillis() - startTime
        val expectedMaxElapsed = 1000
        Assert.assertTrue("Update universe took too long: $elapsed instead of $expectedMaxElapsed ms", elapsed < 1000)
    }

    @Test
    fun updateUniverseSatellite_Performance_isSufficient() {
        val startTime = System.currentTimeMillis()
        universe.apply {
            for (i in 0 until MAX) {
                val satellite: Satellite? = satellites.findSatelliteByName("ISS")
                satellite!!.updateFor(moment.forNow())
            }
        }
        val elapsed = System.currentTimeMillis() - startTime
        val expectedMaxElapsed = 1000
        Assert.assertTrue("Update satellites took too long: $elapsed instead of $expectedMaxElapsed ms", elapsed < 1000)
    }

    companion object {
        const val MAX = 1000
    }
}