package com.stho.nyota.ui.finder

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.lifecycle.Observer
import com.stho.nyota.AbstractFragment
import com.stho.nyota.AbstractViewModel
import com.stho.nyota.R
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.views.RotaryView
import kotlinx.android.synthetic.main.fragment_finder.view.*
import kotlinx.android.synthetic.main.time_visibility_overlay.view.*


class FinderFragment : AbstractFragment(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var windowManager: WindowManager
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val rotationMatrixAdjusted = FloatArray(9)
    private val orientationAngles = FloatArray(3)
    private val handler: Handler = Handler()
    private var display: Display? = null

    private lateinit var viewModel: FinderViewModel

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val elementName: String? = getElementNameFromArguments()
        viewModel = createFinderViewModel(elementName)
        sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_finder, container, false)

        root.compass_ring.setOnRotateListener(object : RotaryView.OnRotateListener {
            override fun onRotate(delta: Double) {
                viewModel.rotate(delta)
            }
        })
        root.compass_ring.setOnDoubleTapListener(object : RotaryView.OnDoubleTapListener {
            override fun onDoubleTap() {
                viewModel.reset()
            }
        })
        viewModel.ringAngleLD.observe(viewLifecycleOwner, Observer {
                alpha -> root.compass_ring.rotation = alpha
        })
        viewModel.northPointerPositionLD.observe(viewLifecycleOwner, Observer {
                angle -> root.compass_north_pointer.rotation = -angle
        })
        viewModel.universeLD.observe(viewLifecycleOwner, Observer {
                universe -> onUpdateElement(universe.moment)
        })


        // TODO: Update on changes of orientation etc.

        return root
    }

    override fun onResume() {
        super.onResume()
        display = windowManager.defaultDisplay
        viewModel.reset()
        initializeAccelerationSensor()
        initializeMagneticFieldSensor()
        initializeHandler()
    }

    override fun onPause() {
        super.onPause()
        removeSensorListeners()
        removeHandler()
    }

    private val HANDLER_DELAY = 100

    private fun initializeAccelerationSensor() {
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    private fun initializeMagneticFieldSensor() {
        val magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        if (magneticField != null) {
            sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_GAME, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    private fun initializeHandler() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                viewModel.updateNorthPointer()
                handler.postDelayed(this, HANDLER_DELAY.toLong())
            }
        }, HANDLER_DELAY.toLong())
    }

    private fun removeHandler() =
        handler.removeCallbacksAndMessages(null)

    private fun removeSensorListeners() =
        sensorManager.unregisterListener(this)

    private fun onUpdateElement(moment: Moment) =
        bind(moment, viewModel.element)

    private fun bind(moment: Moment, element: IElement) {
        view?.also {
            it.currentTime?.text = toLocalTimeString(moment)
            it.currentVisibility?.setImageResource(element.visibility)
        }
        updateActionBar(element.name, toLocalDateString(moment))
    }

    private fun getElementNameFromArguments(): String? {
        return arguments?.getString("ELEMENT")
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // ignore
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.getType()) {
            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(
                    event.values,
                    0,
                    accelerometerReading,
                    0,
                    accelerometerReading.size
                )
                updateOrientationAngles()
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(
                    event.values,
                    0,
                    magnetometerReading,
                    0,
                    magnetometerReading.size
                )
                updateOrientationAngles()
            }
        }
    }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    private fun updateOrientationAngles() {
        if (SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)) {
            SensorManager.getOrientation(getAdjustedRotationMatrix(), orientationAngles)
            viewModel.update(orientationAngles)
        }
    }

    /*
      See the following training materials from google.
      https://codelabs.developers.google.com/codelabs/advanced-android-training-sensor-orientation/index.html?index=..%2F..advanced-android-training#0
     */
    private fun getAdjustedRotationMatrix(): FloatArray? {
        when (display?.rotation) {
            Surface.ROTATION_90 -> {
                SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, rotationMatrixAdjusted)
                return rotationMatrixAdjusted
            }
            Surface.ROTATION_180 -> {
                SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y, rotationMatrixAdjusted)
                return rotationMatrixAdjusted
            }
            Surface.ROTATION_270 -> {
                SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X, rotationMatrixAdjusted)
                return rotationMatrixAdjusted
            }
            else -> {
                return rotationMatrix
            }
        }
    }
}