package com.stho.nyota.sky.utilities

import kotlin.math.*

object Rotation {

    /**
     * Returns the orientation (azimuth, pitch, roll, center azimuth, center altitude) of the device
     *      for a rotation from sensor frame into earth frame
     *
     *      (C) The center "pointer" vector is defined by
     *              C = M * (0, 0, -1)
     *
     *          --> C = (-m13, -m23, -m33)
     *
     *              center azimuth  = atan2(-m13, -m23)
     *              center azimuth = asin(-m33) // opposite of pitch
     */
    internal fun getOrientationFor(r: IRotation): Orientation =
        if (isGimbalLockForSinus(r.m32)) {
            if (r.m32 < 0) { // pitch 90°
                val roll = Degree.arcTan2(r.m21, r.m23)
                Orientation(
                    azimuth = 0.0,
                    pitch = 90.0,
                    roll = roll,
                    centerAzimuth = 180 - roll,
                    centerAltitude = 0.0,
                )
            } else { // pitch -90°
                val roll = Degree.arcTan2(-r.m21, -r.m23)
                Orientation(
                    azimuth = 0.0,
                    pitch = -90.0,
                    roll = roll,
                    centerAzimuth = roll,
                    centerAltitude = 0.0,
                )
            }
        } else {
            if (isGimbalLockForCenter(r.m13, r.m23)) { // pitch 0°
                val azimuth = Degree.arcTan2(r.m12, r.m22)
                val roll = Degree.arcTan2(r.m31, r.m33)
                Orientation(
                    azimuth = azimuth,
                    pitch = Degree.arcSin(-r.m32),
                    roll = roll,
                    centerAzimuth = azimuth,
                    centerAltitude = roll - 90,
                )
            }
            else {
                Orientation(
                    azimuth = Degree.arcTan2(r.m12, r.m22),
                    pitch = Degree.arcSin(-r.m32),
                    roll = Degree.arcTan2(r.m31, r.m33),
                    centerAzimuth = Degree.arcTan2(-r.m13, -r.m23),
                    centerAltitude = Degree.arcSin(-r.m33),
                )
            }
        }

    private const val FREE_FALL_GRAVITY_SQUARED = 0.01 * 9.81 * 9.81
    private const val FREE_FALL_MAGNETOMETER_SQUARED = 0.01

    internal fun getRotationMatrixFromAccelerationMagnetometer(acceleration: Vector, magnetometer: Vector, defaultValue: RotationMatrix): RotationMatrix =
        getRotationMatrixFromAccelerationMagnetometer(acceleration, magnetometer) ?: defaultValue

    /**
     * Returns the rotation matrix M for a device defined by the gravity and the geomagnetic vector
     *      in case of free fall or close to magnetic pole it returns null
     *      as the rotation matrix cannot be calculated
     *
     * @param acceleration acceleration (a vector pointing upwards in default position)
     * @param magnetometer magnetic (a vector pointing down to the magnetic north)
     *
     * see also: SensorManager.getRotationMatrix()
     */
    internal fun getRotationMatrixFromAccelerationMagnetometer(acceleration: Vector, magnetometer: Vector): RotationMatrix? {
        val normSquareA = acceleration.normSquare()
        if (normSquareA < FREE_FALL_GRAVITY_SQUARED) {
            // free fall detected, typical values of gravity should be 9.81
            return null
        }

        val hh = Vector.cross(magnetometer, acceleration)
        val normSquareH = hh.normSquare()
        if (normSquareH < FREE_FALL_MAGNETOMETER_SQUARED) {
            // free fall or in space or close to magnetic north pole, typical values of magnetometer should be more than 100
            return null
        }

        val h = hh / sqrt(normSquareH)
        val a = acceleration / sqrt(normSquareA)
        val m = Vector.cross(a, h)
        return RotationMatrix(
            m11 = h.x, m12 = h.y, m13 = h.z,
            m21 = m.x, m22 = m.y, m23 = m.z,
            m31 = a.x, m32 = a.y, m33 = a.z,
        )
    }

    /**
     * Returns the rotation matrix as integration of angle velocity from gyroscope of a time period
     *
     * @param omega angle velocity around x, y, z, in radians/second
     * @param dt time period in seconds
     */
    internal fun getRotationFromGyro(omega: Vector, dt: Double): Quaternion {
        // Calculate the angular speed of the sample
        val omegaMagnitude: Double = omega.norm()

        // Normalize the rotation vector if it's big enough to get the axis
        // (that is, EPSILON should represent your maximum allowable margin of error)
        val w = if (omegaMagnitude > OMEGA_THRESHOLD)
            omega * (1 / omegaMagnitude)
        else
            omega

        // Quaternion integration:
        // ds/dt = omega x s
        // with s = q # s0 # q* follows
        //      dq/dt = 0.5 * omega # q
        //      q(t) = exp(0.5 * omega * (t - t0)) # q0
        //      q(t) = cos(|v|) + v / |v| * sin(|v|) # q0 with v = 0.5 * omega * (t - t0)
        //      this is equivalent to a rotation by theta around the rotation vector omega/|omega| with theta = |omega| * (t - t0)
        val theta: Double = omegaMagnitude * dt
        return Quaternion.forRotation(w, theta)
    }

    /**
     * Jacobian Matrix for rotation by quaternion q:
     *
     *          f(q, v) = q* # v # q
     *                  = q.rotationMatrix().transpose() * v
     *
     *          (rotate v from earth frame to body frame)
     *
     */
    @Suppress("SpellCheckingInspection")
    class Jacobian(
        val dfxds: Double, val dfxdx: Double, val dfxdy: Double, val dfxdz: Double,
        val dfyds: Double, val dfydx: Double, val dfydy: Double, val dfydz: Double,
        val dfzds: Double, val dfzdx: Double, val dfzdy: Double, val dfzdz: Double,
    ) {
        companion object {
            @Suppress("SpellCheckingInspection")
            fun create(q: Quaternion, r: Vector): Jacobian {
                val rxqs = 2 * r.x * q.s
                val rxqx = 2 * r.x * q.x
                val rxqy = 2 * r.x * q.y
                val rxqz = 2 * r.x * q.z
                val ryqs = 2 * r.y * q.s
                val ryqx = 2 * r.y * q.x
                val ryqy = 2 * r.y * q.y
                val ryqz = 2 * r.y * q.z
                val rzqs = 2 * r.z * q.s
                val rzqx = 2 * r.z * q.x
                val rzqy = 2 * r.z * q.y
                val rzqz = 2 * r.z * q.z
                return Jacobian(
                    dfxds = ryqz - rzqy,
                    dfxdx = ryqy + rzqz,
                    dfxdy = -2 * rxqy + ryqx - rzqs,
                    dfxdz = -2 * rxqz + ryqs + rzqx,
                    dfyds = -rxqz + rzqx,
                    dfydx = rxqy - 2 * ryqx + rzqs,
                    dfydy = rxqx + rzqz,
                    dfydz = -rxqs - 2 * ryqz + rzqy,
                    dfzds = rxqy - ryqx,
                    dfzdx = rxqz - ryqs - 2 * rzqx,
                    dfzdy = rxqs + ryqz - 2 * rzqy,
                    dfzdz = rxqx + ryqy,
                )
            }
        }
    }

    /**
     * Returns if x is about +/- PI/2
     */
    private fun isGimbalLockForRadians(x: Double): Boolean =
        x < GIMBAL_LOCK_RADIANS_MINIMUM || x > GIMBAL_LOCK_RADIANS_MAXIMUM

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
    private const val GIMBAL_LOCK_RADIANS_MINIMUM: Double = 0.001414 - PI_OVER_TWO
    private const val GIMBAL_LOCK_RADIANS_MAXIMUM: Double = PI_OVER_TWO - 0.001414
    private const val GIMBAL_LOCK_SINUS_TOLERANCE: Double = 0.000001
    private const val GIMBAL_LOCK_SINUS_MINIMUM: Double = -0.999999
    private const val GIMBAL_LOCK_SINUS_MAXIMUM: Double = 0.999999
    private const val OMEGA_THRESHOLD: Double = 0.0000001
}

