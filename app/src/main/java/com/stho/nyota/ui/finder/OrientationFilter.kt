package com.stho.nyota.ui.finder

import com.stho.nyota.sky.utilities.Angle
import com.stho.nyota.sky.utilities.Orientation
import com.stho.nyota.sky.utilities.Vector


interface IOrientationFilter {
    fun onOrientationAnglesChanged(orientationAngles: FloatArray)
    val orientation: Orientation
}

/*
    The class takes updates of the orientation vector by listening to onOrientationAnglesChanged(angles).
    The values will be stored and smoothed with acceleration.
    A handler will regularly read the updated smoothed orientation
 */
class OrientationAccelerationFilter: IOrientationFilter {

    private val azimuthAcceleration: Acceleration = Acceleration()
    private val pitchAcceleration: Acceleration = Acceleration(0.3)
    private val rollAcceleration: Acceleration = Acceleration(0.3)
    private val lowPassFilter: LowPassFilter = LowPassFilter()

    override val orientation: Orientation
        get() {
            val azimuth = azimuthAcceleration.position
            val pitch = pitchAcceleration.position
            val roll = rollAcceleration.position
            val direction = pitch - 90
            return Orientation(azimuth = azimuth, pitch = pitch, direction = direction, roll = roll)
        }


    override fun onOrientationAnglesChanged(orientationAngles: FloatArray) {
        val gravity: Vector = lowPassFilter.setAcceleration(orientationAngles)
        val roll = normalizeRoll(gravity.z)
        val pitch = normalizePitch(gravity.y)
        val azimuth = normalizeAzimuth(gravity.x, roll)
        rollAcceleration.rotateTo(roll)
        pitchAcceleration.rotateTo(pitch)
        azimuthAcceleration.rotateTo(azimuth)
    }

    private fun normalizeRoll(roll: Double) =
        Angle.normalizeTo180(roll)

    private fun normalizePitch(pitch: Double) =
        Angle.normalizeTo180(pitch)

    private fun normalizeAzimuth(azimuth: Double, roll: Double) =
        when {
            roll < -PI_90 || roll > PI_90 -> Angle.normalize(0 - azimuth) // look at screen from below
            else -> azimuth
        }

    companion object {
        private const val PI_90 = 0.5 * Math.PI
    }
}