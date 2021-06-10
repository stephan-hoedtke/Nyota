package com.stho.nyota

import com.stho.nyota.sky.utilities.Orientation
import org.junit.Assert.assertEquals
import org.junit.Test

class OrientationUnitTest {

    @Test
    fun orientation_rotationToNorth_isCorrect() {
        orientation_rotationToNorth_isCorrect(30.0, -30f)
        orientation_rotationToNorth_isCorrect(320.0, 40f)
    }

    private fun orientation_rotationToNorth_isCorrect(deviceAzimuth: Double, expected: Float) {
        // Arrange
        val orientation: Orientation = Orientation(deviceAzimuth, 1.0, -89.0, 3.0, centerAltitude = 0.0)

        // Execute
        val actual = orientation.getRotationToNorth()

        // Assert
        assertEquals(expected, actual, DELTA_FLOAT)

    }

    @Test
    fun orientation_rotationToTarget_isCorrect() {
        orientation_rotationToTarget_isCorrect(30.0, 11.0, -19.0f)
        orientation_rotationToTarget_isCorrect(30.0, 44.0, 14.0f)
        orientation_rotationToTarget_isCorrect(320.0, 11.0, 51.0f)
    }

    private fun orientation_rotationToTarget_isCorrect(deviceAzimuth: Double, targetAzimuth: Double, expected: Float) {
        // Arrange
        val orientation: Orientation = Orientation(deviceAzimuth, 1.0, -89.0, 3.0, centerAltitude = 0.0)

        // Execute
        val actual = orientation.getRotationToTargetAt(targetAzimuth)

        // Assert
        assertEquals(expected, actual, DELTA_FLOAT)
    }

    companion object {
        private const val DELTA_FLOAT: Float = 1.0E-7F
    }
}
