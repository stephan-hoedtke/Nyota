package com.stho.nyota

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Display
import android.view.Surface
import android.view.WindowManager
import com.stho.nyota.sky.utilities.Orientation
import com.stho.nyota.views.Rotation

interface IOrientationFilter {
    fun onOrientationChanged(rotationMatrix: FloatArray)
    val currentOrientation: Orientation
}


class OrientationSensorListener(private val context: Context, private val filter: IOrientationFilter) : SensorEventListener {

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val windowManager: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val rotationVectorReading = FloatArray(5)
    private var display: Display? = null

    internal fun onResume() {
        display = windowManager.defaultDisplay
        initializeRotationVectorSensor()
    }

    internal fun onPause() {
        display = null
        removeSensorListeners()
    }

    /**
     * The rotation vector sensor is fusing gyroscope, accelerometer and magnetometer. No further sensor fusion required.
     */
    private fun initializeRotationVectorSensor() {
        val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        if (rotationVectorSensor != null) {
            sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_GAME, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    private fun removeSensorListeners() =
        sensorManager.unregisterListener(this)

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // We don't care
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor?.type) {
            Sensor.TYPE_ROTATION_VECTOR -> {
                System.arraycopy(event.values, 0, rotationVectorReading, 0, rotationVectorReading.size)
                updateOrientationFromRotationVectorReadings()
            }
        }
    }

    private fun updateOrientationFromRotationVectorReadings() {
        val rotationMatrix = FloatArray(9)
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVectorReading)
        val adjustedRotationMatrix = getAdjustedRotationMatrix(rotationMatrix)
        filter.onOrientationChanged(adjustedRotationMatrix)
    }

    /*
      See the following training materials from google.
      https://codelabs.developers.google.com/codelabs/advanced-android-training-sensor-orientation/index.html?index=..%2F..advanced-android-training#0
     */
    private fun getAdjustedRotationMatrix(rotationMatrix: FloatArray): FloatArray {
        val adjustedRotationMatrix = FloatArray(9)
        when (display?.rotation) {
            Surface.ROTATION_90 -> {
                SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, adjustedRotationMatrix)
                return adjustedRotationMatrix
            }
            Surface.ROTATION_180 -> {
                SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y, adjustedRotationMatrix)
                return adjustedRotationMatrix
            }
            Surface.ROTATION_270 -> {
                SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X, adjustedRotationMatrix)
                return adjustedRotationMatrix
            }
            else -> {
                return rotationMatrix
            }
        }
    }
}