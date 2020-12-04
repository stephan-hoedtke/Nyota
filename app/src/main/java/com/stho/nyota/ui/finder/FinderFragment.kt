package com.stho.nyota.ui.finder
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stho.nyota.AbstractFragment
import com.stho.nyota.AbstractViewModel
import com.stho.nyota.databinding.FragmentFinderBinding
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.utilities.Angle
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.sky.utilities.Orientation
import com.stho.nyota.views.RotaryView


class FinderFragment : AbstractFragment() {

    private lateinit var orientationFilter: IOrientationFilter
    private lateinit var orientationSensorListener: OrientationSensorListener
    private lateinit var viewModel: FinderViewModel
    private var bindingReference: FragmentFinderBinding? = null
    private val binding: FragmentFinderBinding get() = bindingReference!!
    private val handler: Handler = Handler()

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val elementName: String? = getElementNameFromArguments()
        viewModel = createFinderViewModel(elementName)
        orientationFilter = OrientationAccelerationFilter()
        orientationSensorListener = OrientationSensorListener(requireContext(), orientationFilter)
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
                viewModel.seek()
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.orientationLD.observe(viewLifecycleOwner, { orientation -> onUpdateOrientation(orientation) })
        viewModel.ringAngleLD.observe(viewLifecycleOwner, { alpha -> binding.compassRing.rotation = alpha.toFloat() })
        viewModel.universeLD.observe(viewLifecycleOwner, { universe -> onUpdateElement(universe.moment) })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.reset()
        orientationSensorListener.onResume()
        initializeHandler()
    }

    override fun onPause() {
        super.onPause()
        removeHandler()
        orientationSensorListener.onPause()
    }

    private fun initializeHandler() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                viewModel.updateOrientation(orientationFilter.orientation)
                handler.postDelayed(this, FinderFragment.HANDLER_DELAY.toLong())
            }
        }, FinderFragment.HANDLER_DELAY.toLong())
    }

    private fun removeHandler() =
        handler.removeCallbacksAndMessages(null)

    private fun onUpdateElement(moment: Moment) =
        bind(moment, viewModel.element)

    private fun bind(moment: Moment, element: IElement) {
        binding.timeVisibilityOverlay.currentTime.text = toLocalTimeString(moment)
        binding.timeVisibilityOverlay.currentVisibility.setImageResource(element.visibility)
        binding.targetAzimuth.text = Angle.toString(element.position?.azimuth ?: 0.0, Angle.AngleType.AZIMUTH)
        binding.targetAltitude.text = Angle.toString(element.position?.altitude ?: 0.0, Angle.AngleType.ALTITUDE)
        binding.horizonView.targetAltitude = element.position?.altitude ?: 0.0
        updateActionBar(element.name, toLocalDateString(moment))
    }

    private fun onUpdateOrientation(orientation: Orientation) {
        bind(orientation, viewModel.element)
    }

    private fun bind(orientation: Orientation, element: IElement) {
        val north = orientation.azimuth
        val target = element.position!!.azimuth
        val targetRelativeRotation = Angle.normalizeTo180(target + north)
        binding.compassNorthPointer.rotation = -north.toFloat()
        binding.targetAzimuthPointer.rotation = targetRelativeRotation.toFloat()
        binding.horizonView.currentDeviceOrientation = orientation
        binding.currentDeviceAzimuth.text = Angle.toString(orientation.azimuth, Angle.AngleType.AZIMUTH)
        binding.currentDevicePitch.text = Angle.toString(orientation.pitch, Angle.AngleType.PITCH)
    }

    private fun getElementNameFromArguments(): String? {
        return arguments?.getString("ELEMENT")
    }

    companion object {
        private const val HANDLER_DELAY = 100
    }

    // TODO: compass sensor into main activity ??
}

