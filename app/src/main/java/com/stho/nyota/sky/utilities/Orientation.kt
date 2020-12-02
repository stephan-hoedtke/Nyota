package com.stho.nyota.sky.utilities

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager

/**
 * Orientation is the direction in which your eyes look into the phone. Orthogonal to the phones surface, going out from the back.
 * Azimuth: Angle between the orientation and the geographic north at the horizon plane.
 * Inclination: Angle between the orientation and the horizon plane.
 */
class Orientation {
    private val azimuth: IAverage = Average()
    private val altitude: IAverage = Average()
    private val direction: IAverage = Average()
    private val roll: IAverage = Average()

    internal class SensorValues {
        val gravity = LPFVector()
        val geomagnetic = LPFVector()
        val rotation = LPFVector()
    }

    private val sensorValues = SensorValues()
    fun getAzimuth(): Float {
        return azimuth.currentAngle.toFloat()
    }

    fun getAltitude(): Float {
        return altitude.currentAngle.toFloat()
    }

    fun getDirection(): Float {
        return direction.currentAngle.toFloat()
    }

    fun getRoll(): Float {
        return roll.currentAngle.toFloat()
    }

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
        // setAzimuth((float)Math.toDegrees(O[0]), Angle.normalizeTo180((float)Math.toDegrees(O[2])));
    }

    private fun update(O: FloatArray) {
        val a = Math.toDegrees(O[0].toDouble()) // Azimuth
        val p = Math.toDegrees(O[1].toDouble()) // Pitch [-90 -- +90] positive: to the ground (it's not [-180 -- +180])
        val r = Math.toDegrees(O[2].toDouble()) // Roll [-180 -- +180] positive: to the left (it's not [-90 -- +90])
        if (-90 <= r && r <= +90) {  // the phones surface is looked at from above, orientation (orthogonal to the surface) is pointing into the earth
            azimuth.setAngle(a)
            altitude.setAngle(-p - 90)
            direction.setAngle(-p)
            roll.setAngle(r)
        } else { // the phones surface is looked at from below, orientation (orthogonal to the surface) is pointing into the sky
            azimuth.setAngle(Angle.normalize(a + 180))
            altitude.setAngle(-p)
            direction.setAngle(180 + p)
            if (r > 90) roll.setAngle(180 - r) else roll.setAngle(-r - 180)
        }
    }

    override fun toString(): String {
        return (Angle.toString(getAzimuth().toDouble(), Angle.AngleType.DEGREE_NORTH_EAST_SOUTH_WEST)
                + Formatter.SPACE
                + Angle.toString(getAltitude().toDouble(), Angle.AngleType.ALTITUDE))
    }

    fun toString(flat: Boolean): String {
        return (Angle.toString(getAzimuth().toDouble(), Angle.AngleType.DEGREE_NORTH_EAST_SOUTH_WEST)
                + Formatter.SPACE
                + Angle.toString(if (flat) getDirection().toDouble() else getAltitude().toDouble(), Angle.AngleType.ALTITUDE))
    }
}

