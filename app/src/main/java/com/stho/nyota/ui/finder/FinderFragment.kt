package com.stho.nyota.ui.finder
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.view.*
import com.stho.nyota.AbstractFragment
import com.stho.nyota.AbstractViewModel
import com.stho.nyota.databinding.FragmentFinderBinding
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.views.RotaryView


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
    private var bindingReference: FragmentFinderBinding? = null
    private val binding: FragmentFinderBinding get() = bindingReference!!

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val elementName: String? = getElementNameFromArguments()
        viewModel = createFinderViewModel(elementName)
        sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentFinderBinding.inflate(inflater, container, false)

        binding.compassRing.setOnRotateListener(object : RotaryView.OnRotateListener {
            override fun onRotate(delta: Double) {
                viewModel.rotate(delta)
            }
        })
        binding.compassRing.setOnDoubleTapListener(object : RotaryView.OnDoubleTapListener {
            override fun onDoubleTap() {
                viewModel.reset()
            }
        })

        // TODO: Update on changes of orientation etc.

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.ringAngleLD.observe(viewLifecycleOwner, {
                alpha -> binding.compassRing.rotation = alpha
        })
        viewModel.northPointerPositionLD.observe(viewLifecycleOwner, {
                angle -> binding.compassNorthPointer.rotation = -angle
        })
        viewModel.universeLD.observe(viewLifecycleOwner, {
                universe -> onUpdateElement(universe.moment)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
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
        binding.timeVisibilityOverlay.currentTime.text = toLocalTimeString(moment)
        binding.timeVisibilityOverlay.currentVisibility.setImageResource(element.visibility)
        updateActionBar(element.name, toLocalDateString(moment))
    }

    private fun getElementNameFromArguments(): String? {
        return arguments?.getString("ELEMENT")
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // ignore
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
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
    private fun getAdjustedRotationMatrix(): FloatArray {
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

    // TODO: compass sensor into main activity ??
}

