package com.stho.nyota

import android.os.SystemClock
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito


class TimeSourceUnitTest {

    class FakeClock(var elapsedRealtimeNanos: Long = 1000000000000L) {
        fun addSeconds(seconds: Long) {
            elapsedRealtimeNanos += NANOS_PER_SECOND * seconds
        }
        companion object {
            private const val NANOS_PER_SECOND = 1000000000L
        }
    }

     /*
        requires: Mockito > 3.4.0 and testImplementation 'org.mockito:mockito-inline:3.4.0' instead of 'org.mockito:mockito-core:xxx'
     */
    @Test // @Ignore("too slow")
    fun timeSource_elapsedRealtimeSeconds_mockStatic_isCorrect() {
        val fakeClock = FakeClock()
        val timeSource = SystemClockTimeSource()
        val systemClock = Mockito.mockStatic(SystemClock::class.java)
        systemClock.`when`<Any>(SystemClock::elapsedRealtimeNanos).then { _ -> fakeClock.elapsedRealtimeNanos }

        val t0 = timeSource.elapsedRealtimeSeconds
        fakeClock.addSeconds(1)
        val t1 = timeSource.elapsedRealtimeSeconds
        val dt = t1 - t0

        systemClock.close()
        Assert.assertEquals("Elapsed time source time", dt, 1.0, DELTA)
    }

    companion object {
        private const val DELTA = 0.0000001
    }
}