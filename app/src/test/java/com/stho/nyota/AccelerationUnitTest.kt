package com.stho.nyota

import org.junit.Assert
import org.junit.Test

class AccelerationUnitTest {

    class FakeTimeSource(var time: Double = 0.0) : TimeSource {
        override val elapsedRealtimeSeconds: Double
            get() = time
    }

    @Test
    fun acceleration_forOnePosition_isCorrect() {

        val startValue = 0.0
        val targetValue = 1.0
        val factor = 1.1 // Reaching the final position in 1.1 seconds
        val timeSource = FakeTimeSource()

        timeSource.time = 100.0
        val acceleration = Acceleration(factor, timeSource)
        acceleration.rotateTo(targetValue)
        val x0 = acceleration.position
        Assert.assertEquals("Position after 0 second", startValue, x0, EPS)

        timeSource.time = 100.1
        val x1 = acceleration.position
        Assert.assertTrue("Position after 0.1 seconds", x1 > startValue && x1 < targetValue)

        timeSource.time = 100.2
        val x2 = acceleration.position
        Assert.assertTrue("Position after 0.2 seconds", x2 > startValue && x2 < targetValue)

        // ...

        timeSource.time = 101.0
        val x9 = acceleration.position
        Assert.assertTrue("Position after 1.0 seconds", x9 > startValue && x9 < targetValue)

        timeSource.time = 101.1
        val x10 = acceleration.position
        Assert.assertEquals("Position after 1.1 seconds (reaching the target position)", targetValue, x10, EPS)

        timeSource.time = 101.2
        val x11 = acceleration.position
        Assert.assertEquals("Position after 1.2 seconds (keeping the target position)",targetValue,  x11, EPS)

    }

    @Test
    fun acceleration_withChangingPositions_isCorrect() {

        val startValue = 0.0
        val firstTargetValue = 1.0
        val secondTargetValue = 8.0
        val factor = 1.1 // Reaching the final position in 1.1 seconds
        val timeSource = FakeTimeSource()

        timeSource.time = 100.0
        val acceleration = Acceleration(factor, timeSource)
        acceleration.rotateTo(firstTargetValue)
        val x0 = acceleration.position
        Assert.assertEquals("Position after 0 second", startValue, x0, EPS)

        // ...

        timeSource.time = 100.5
        val x1 = acceleration.position
        Assert.assertTrue("Position after 0.5 seconds", x1 > startValue && x1 < firstTargetValue)

        acceleration.rotateTo(secondTargetValue)

        val x2 = acceleration.position
        Assert.assertEquals("Position after 0.5 seconds didn't change yet", x2, x2, EPS)

        // ...

        timeSource.time = 101.5
        val x15 = acceleration.position
        Assert.assertTrue("Position after 1.5 seconds", x15 > startValue && x15 < secondTargetValue)

        timeSource.time = 101.6
        val x16 = acceleration.position
        Assert.assertEquals("Position after 1.6 seconds (reaching the second target position)", secondTargetValue, x16, EPS)

        timeSource.time = 101.7
        val x17 = acceleration.position
        Assert.assertEquals("Position after 1.7 seconds (keeping the second target position)", secondTargetValue, x17, EPS)
    }


    @Test
    fun acceleration_withNormalRotation_isCorrect() {

        val startValue = 0.0
        val firstTargetValue = 10.0
        val secondTargetValue = 30.0
        val factor = 1.0 // Reaching the final position in 1 second
        val timeSource = FakeTimeSource()

        timeSource.time = 100.0
        val acceleration = Acceleration(factor, timeSource)
        acceleration.rotateTo(firstTargetValue)
        val x0 = acceleration.position
        Assert.assertEquals("Position after 0 second", x0, startValue, EPS)

        // ...

        timeSource.time = 101.0
        val x10 = acceleration.position
        Assert.assertEquals("Position after 1.6 seconds (reaching the second target position)", firstTargetValue, x10, EPS)

        acceleration.rotateTo(secondTargetValue)

        timeSource.time = 101.1
        val x11 = acceleration.position
        Assert.assertTrue("Position after 1.6 seconds (reaching the second target position)",x11 > firstTargetValue && x11 < secondTargetValue)

        // ...

        timeSource.time = 101.9
        val x19 = acceleration.position
        Assert.assertTrue("Position after 1.7 seconds (keeping the second target position)", x19 > firstTargetValue && x19 < secondTargetValue)

        timeSource.time = 102.0
        val x20 = acceleration.position
        Assert.assertEquals("Position after 1.7 seconds (keeping the second target position)", secondTargetValue, x20, EPS)

        timeSource.time = 102.1
        val x21 = acceleration.position
        Assert.assertEquals("Position after 1.7 seconds (keeping the second target position)", secondTargetValue, x21, EPS)
    }


    @Test
    fun acceleration_withRotationForward_adjustBackwards_isCorrect() {

        val firstTargetValue = 350.0
        val secondTargetValue = 380.0
        val adjustedStartValue = 360.0
        val adjustedFirstTargetValue = -10.0
        val adjustedSecondTargetValue = 20.0
        val factor = 1.0 // Reaching the final position in 1 second
        val timeSource = FakeTimeSource()

        timeSource.time = 100.0
        val acceleration = Acceleration(factor, timeSource)
        acceleration.rotateTo(firstTargetValue)
        val x0 = acceleration.position
        Assert.assertEquals("Position after 0 second", adjustedStartValue, x0, EPS)

        // ...

        timeSource.time = 101.0
        val x10 = acceleration.position
        Assert.assertEquals("Position after 1.6 seconds (reaching the second target position)", firstTargetValue, x10, EPS)

        acceleration.rotateTo(secondTargetValue)
        val x1A = acceleration.position
        Assert.assertEquals("Position after 1.6 seconds (adjusted)", adjustedFirstTargetValue, x1A, EPS)

        timeSource.time = 101.1
        val x11 = acceleration.position
        Assert.assertTrue("Position after 1.6 seconds (reaching the second target position)",x11 > adjustedFirstTargetValue && x11 < adjustedSecondTargetValue)

        // ...

        timeSource.time = 101.9
        val x19 = acceleration.position
        Assert.assertTrue("Position after 1.7 seconds (keeping the second target position)", x19 > adjustedFirstTargetValue && x19 < adjustedSecondTargetValue)

        timeSource.time = 102.0
        val x20 = acceleration.position
        Assert.assertEquals("Position after 1.7 seconds (keeping the second target position)", adjustedSecondTargetValue, x20, EPS)

        timeSource.time = 102.1
        val x21 = acceleration.position
        Assert.assertEquals("Position after 1.7 seconds (keeping the second target position)", adjustedSecondTargetValue, x21, EPS)
    }

    companion object {
        private const val EPS = 0.00000001
    }
}


