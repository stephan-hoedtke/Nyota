package com.stho.nyota.ui.finder


import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stho.nyota.AbstractFragment
import com.stho.nyota.AbstractViewModel
import com.stho.nyota.R
import com.stho.nyota.databinding.FragmentFinderBinding
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.utilities.Angle
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.sky.utilities.Orientation
import com.stho.nyota.views.RotaryView

// TODO: keep current parameters (ringAngle, freeze, ... ) when Orientation changes or other apps go to foreground...

class FinderFragment : AbstractFragment() {

    private lateinit var viewModel: FinderViewModel
    private var bindingReference: FragmentFinderBinding? = null
    private val binding: FragmentFinderBinding get() = bindingReference!!

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val elementName: String? = getElementNameFromArguments()
        viewModel = createFinderViewModel(elementName)
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
        binding.buttonRefresh.setOnClickListener{ viewModel.toggleRefreshAutomatically() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.orientationLD.observe(viewLifecycleOwner, { orientation -> onUpdateDeviceOrientation(orientation) })
        viewModel.ringAngleLD.observe(viewLifecycleOwner, { angle -> onUpdateRingAngle(angle) })
        viewModel.universeLD.observe(viewLifecycleOwner, { universe -> onUpdateElement(universe.moment) })
        viewModel.refreshAutomaticallyLD.observe(viewLifecycleOwner, { refresh -> onUpdateRefreshAutomatically(refresh) })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.reset()
    }

    private fun onUpdateElement(moment: Moment) =
        bind(moment, viewModel.element)

    private fun bind(moment: Moment, element: IElement) {
        binding.timeVisibilityOverlay.currentTime.text = toLocalTimeString(moment)
        binding.timeVisibilityOverlay.currentVisibility.setImageResource(element.visibility)
        binding.targetAzimuth.text = Angle.toString(element.position?.azimuth ?: 0.0, Angle.AngleType.AZIMUTH)
        binding.targetAltitude.text = Angle.toString(element.position?.altitude ?: 0.0, Angle.AngleType.ALTITUDE)
        binding.horizonView.targetAltitude = element.position?.altitude ?: 0.0
        binding.targetAzimuthPointer.rotation = viewModel.getRotationToTarget()
        updateActionBar(element.toString(), toLocalDateString(moment))
    }

    private fun onUpdateRingAngle(angle: Double) {
        binding.compassRing.rotation = angle.toFloat()
        if (!viewModel.refreshAutomatically) {
            binding.targetAzimuthPointer.rotation = viewModel.getRotationToTargetManuallyFor(angle)
        }
    }

    private fun onUpdateDeviceOrientation(orientation: Orientation) {
        binding.compassNorthPointer.rotation = orientation.getRotationToNorth()
        binding.horizonView.currentDeviceOrientation = orientation
        binding.currentDeviceAzimuth.text = Angle.toString(orientation.azimuth, Angle.AngleType.AZIMUTH)
        binding.currentDevicePitch.text = Angle.toString(orientation.pitch, Angle.AngleType.PITCH)
        binding.currentDeviceRoll.text = Angle.toString(orientation.roll, Angle.AngleType.ROLL)
        if (viewModel.refreshAutomatically) {
            binding.targetAzimuthPointer.rotation = viewModel.getRotationToTargetAutomaticallyFor(orientation)
        }
    }

    private fun onUpdateRefreshAutomatically(refresh: Boolean) {
        binding.buttonRefresh.text = getString(if (refresh) R.string.label_freeze else R.string.label_unfreeze)
    }

    private fun getElementNameFromArguments(): String? =
        arguments?.getString("ELEMENT")

    companion object {
        private const val HANDLER_DELAY = 100
    }
}

