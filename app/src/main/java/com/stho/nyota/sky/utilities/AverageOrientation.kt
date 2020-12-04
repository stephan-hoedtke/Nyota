package com.stho.nyota.sky.utilities

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager

class AverageOrientation {
    private val averageAzimuth: IAverage = Average()
    private val averagePitch: IAverage = Average()
    private val averageDirection: IAverage = Average()
    private val averageRoll: IAverage = Average()

    internal class SensorValues {
        val gravity = LPFVector()
        val geomagnetic = LPFVector()
        val rotation = LPFVector()
    }

    private val sensorValues = SensorValues()

    val azimuth: Double
        get() = averageAzimuth.currentAngle

    val pitch: Double
        get() = averagePitch.currentAngle

    val direction: Double
        get() = averageDirection.currentAngle

    val roll: Double
        get() = averageRoll.currentAngle

    val orientation: Orientation
        get() = Orientation(azimuth, pitch, direction, roll)

    @Synchronized
    fun updateSensorValues(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> sensorValues.gravity.update(event.values)
            Sensor.TYPE_MAGNETIC_FIELD -> sensorValues.geomagnetic.update(event.values)
            Sensor.TYPE_ROTATION_VECTOR -> sensorValues.rotation.update(event.values)
        }
        if (sensorValues.rotation.hasValues()) {
            updateByRotation()
        } else if (sensorValues.gravity.hasValues() && sensorValues.geomagnetic.hasValues()) {
            updateByGravityGeomagnetic()
        }
    }

    private fun updateByRotation() {
        val R = FloatArray(9)
        val O = FloatArray(3)
        SensorManager.getRotationMatrixFromVector(R, sensorValues.rotation.values)
        SensorManager.getOrientation(R, O)
        update(O)
        // setAzimuth((float)Math.toDegrees(O[0]), Angle.normalizeTo180((float)Math.toDegrees(O[2])));
    }

    private fun updateByGravityGeomagnetic() {
        val R = FloatArray(9)
        val O = FloatArray(3)
        SensorManager.getRotationMatrix(R, null, sensorValues.gravity.values, sensorValues.geomagnetic.values)
        SensorManager.getOrientation(R, O)
        update(O)
    }

    private fun update(O: FloatArray) {
        val a = Math.toDegrees(O[0].toDouble()) // Azimuth
        val p = Math.toDegrees(O[1].toDouble()) // Pitch [-90 -- +90] positive: to the ground (it's not [-180 -- +180])
        val r = Math.toDegrees(O[2].toDouble()) // Roll [-180 -- +180] positive: to the left (it's not [-90 -- +90])
        if (-90 <= r && r <= +90) {  // the phones surface is looked at from above, orientation (orthogonal to the surface) is pointing into the earth
            averageAzimuth.setAngle(a)
            averagePitch.setAngle(-p - 90)
            averageDirection.setAngle(-p)
            averageRoll.setAngle(r)
        } else { // the phones surface is looked at from below, orientation (orthogonal to the surface) is pointing into the sky
            averageAzimuth.setAngle(Angle.normalize(a + 180))
            averagePitch.setAngle(-p)
            averageDirection.setAngle(180 + p)
            averageRoll.setAngle(if (r > 90) 180 - r else -r - 180)
        }
    }

    override fun toString(): String {
        return (Angle.toString(azimuth, Angle.AngleType.DEGREE_NORTH_EAST_SOUTH_WEST)
                + Formatter.SPACE
                + Angle.toString(pitch, Angle.AngleType.ALTITUDE))
    }

    fun toString(flat: Boolean): String {
        return (Angle.toString(azimuth, Angle.AngleType.DEGREE_NORTH_EAST_SOUTH_WEST)
                + Formatter.SPACE
                + Angle.toString(if (flat) direction else pitch, Angle.AngleType.ALTITUDE))
    }
}

