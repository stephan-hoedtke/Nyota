package com.stho.nyota.views

import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.Orientation
import kotlin.math.PI
import kotlin.math.abs

class Rotation(private val matrix: FloatArray) {
    val m11: Double by lazy { matrix[0].toDouble() }
    val m12: Double by lazy { matrix[1].toDouble() }
    val m13: Double by lazy { matrix[2].toDouble() }
    val m21: Double by lazy { matrix[3].toDouble() }
    val m22: Double by lazy { matrix[4].toDouble() }
    val m23: Double by lazy { matrix[5].toDouble() }
    val m31: Double by lazy { matrix[6].toDouble() }
    val m32: Double by lazy { matrix[7].toDouble() }
    val m33: Double by lazy { matrix[8].toDouble() }

    /**
     * Returns the orientation (azimuth, pitch, roll, center azimuth, center altitude) of the device
     *      for a rotation from sensor frame into earth frame
     *
     *      (A) for the normal case:
     *                  cos(x) <> 0:
     *
     *         -->      m12 = cosX * sinZ,
     *                  m22 = cosX * cosZ,
     *                  m31 = cosX * sinY,
     *                  m32 = -sinX,
     *                  m33 = cosX * cosY
     *
     *          -->     X = asin(-m32)
     *                  Y = atan(m31 / m33)
     *                  Z = atan(m12 / m22)
     *
     *      (B) for the gimbal lock when:
     *                  cos(x) = 0
     *                  sin(x) = +/-1 (X=+/-90°)
     *
     *              m11 = sinX * sinY * sinZ + cosY * cosZ,
     *              m13 = sinX * cosY * sinZ - sinY * cosZ,
     *              m21 = sinX * sinY * cosZ - cosY * sinZ,
     *              m23 = sinX * cosY * cosZ + sinY * sinZ,
     *
     *
     *          --> only (Y - Z) is defined, as cosY * cosZ + sinY * sinZ = cos(Y - Z) etc.
     *          --> assume z = 0
     *                  cos(z) = 1
     *                  sin(z) = 0
     *
     *          (B.1) when sin(X) = -1 (X=-90°):
     *                  m21 = - sinY * cosZ - cosY * sinZ = - sin(Y + Z)
     *                  m23 = - cosY * cosZ + sinY * sinZ = - cos(Y + Z)
     *
     *          -->     Y = atan2(-m21, -m23) and Y = Y' + Z' for any other Y' and Z'
     *
     *          (B.2) when sin(X) = -1 (X=-90°)
     *                  m21 = sinY * cosZ - cosY * sinZ = sin(Y - Z) = sinY
     *                  m23 = cosY * cosZ + sinY * sinZ = cos(Y - Z) = cosY
     *
     *          -->     Y = atan2(m21, m23) and Y = Y' - Z' for any other Y' and Z'
     *
     *      (C) The center "pointer" vector is defined by
     *              C = M * (0, 0, -1)
     *
     *          --> C = (-m13, -m23, -m33)
     *
     *              center azimuth  = atan2(-m13, -m23)
     *              center azimuth = asin(-m33) // opposite of pitch
     *
     * see also: SensorManager.getOrientation()
     *
     */
    internal fun getOrientation(): Orientation =
        if (isGimbalLockForSinus(m32)) {
            if (m32 < 0) { // pitch 90°
                val roll = Degree.arcTan2(m21, m23)
                Orientation(
                    azimuth = 0.0,
                    pitch = 90.0,
                    roll = roll,
                    centerAzimuth = 180 - roll,
                    centerAltitude = 0.0,
                )
            } else { // pitch -90°
                val roll = Degree.arcTan2(-m21, -m23)
                Orientation(
                    azimuth = 0.0,
                    pitch = -90.0,
                    roll = roll,
                    centerAzimuth = roll,
                    centerAltitude = 0.0,
                )
            }
        } else {
            if (isGimbalLockForCenter(m13, m23)) { // pitch 0°
                val azimuth = Degree.arcTan2(m12, m22)
                val roll = Degree.arcTan2(m31, m33)
                Orientation(
                    azimuth = azimuth,
                    pitch = Degree.arcSin(-m32),
                    roll = roll,
                    centerAzimuth = azimuth,
                    centerAltitude = roll - 90
                )
            } else {
                Orientation(
                    azimuth = Degree.arcTan2(m12, m22),
                    pitch = Degree.arcSin(-m32),
                    roll = Degree.arcTan2(m31, m33),
                    centerAzimuth = Degree.arcTan2(-m13, -m23),
                    centerAltitude = Degree.arcSin(-m33)
                )
            }
        }

    companion object {

        /**
         * Returns if sin(x) is about +/- 1.0
         */
        private fun isGimbalLockForSinus(sinX: Double): Boolean =
            sinX < GIMBAL_LOCK_SINUS_MINIMUM || sinX > GIMBAL_LOCK_SINUS_MAXIMUM

        /**
         * Returns if x^2 +y^2 is too small to calculate the atan2
         */
        private fun isGimbalLockForCenter(sinX: Double, cosX: Double): Boolean =
            abs(sinX) < GIMBAL_LOCK_SINUS_TOLERANCE && abs(cosX) < GIMBAL_LOCK_SINUS_TOLERANCE

        /**
         * When the pitch is about 90° (Gimbal lock) the rounding errors of x, y, z produce unstable azimuth and roll
         *      pitch = +/- 90°
         *      --> z = +/- 1.0
         *          x = +/- 0.0
         *          y = +/- 0.0
         *      --> atan2(...,...) can be anything.
         *
         * Tolerance estimation:
         *      x,y < 0.001 --> z > sqrt(1 - x * x - y * y) = sqrt(0.999998) = 0.999999 --> 89.92°
         *          pitch = +/- (90° +/- 0.08°) or
         *          pitch = +/- (PI/2 +/- 0.001414) or
         *          sin(x) = +/- (1.0 +/- 0.000001)
         *
         */
        private const val PI_OVER_TWO = PI / 2
        private const val GIMBAL_LOCK_SINUS_TOLERANCE: Double = 0.000001
        private const val GIMBAL_LOCK_SINUS_MINIMUM: Double = -0.999999
        private const val GIMBAL_LOCK_SINUS_MAXIMUM: Double = 0.999999

        internal fun requireAdjustmentForLookingAtThePhoneFromBelow(orientation: Orientation) =
            orientation.roll <= -90 || 90 <= orientation.roll

        internal fun adjustForLookingAtThePhoneFromBelow(orientation: Orientation): Orientation =
            Orientation(
                azimuth = Degree.normalize(180 + orientation.azimuth),
                pitch = Degree.normalizeTo180(180 - orientation.pitch),
                roll = Degree.normalizeTo180(180 - orientation.roll),
                centerAzimuth = orientation.centerAzimuth,
                centerAltitude = orientation.centerAltitude,
            )

    }
}