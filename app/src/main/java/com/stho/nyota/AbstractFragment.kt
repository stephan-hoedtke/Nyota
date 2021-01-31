package com.stho.nyota

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.stho.nyota.databinding.TimeOverlayBinding
import com.stho.nyota.databinding.TimeVisibilityOverlayBinding
import com.stho.nyota.repository.Repository
import com.stho.nyota.settings.Settings
import com.stho.nyota.sky.universe.Universe
import com.stho.nyota.sky.utilities.Formatter
import com.stho.nyota.sky.utilities.IProperty
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.sky.utilities.PropertyKeyType
import com.stho.nyota.ui.constellations.ChooseNextStepDialog
import java.util.*

abstract class AbstractFragment : Fragment() {

    interface IAbstractViewModel {
        val repository: Repository
        val settings: Settings
        val moment: Moment
        val universeLD: LiveData<Universe>

        val interval: Interval
        val intervalLD: LiveData<Interval>
        fun onNext()
        fun onPrevious()
        fun onReset()

        val showDetailsLD: LiveData<Boolean>
        var showDetails: Boolean
        fun onToggleShowDetails()
    }

    private var bindingReference: AbstractFragmentBinding? = null
    private val binding: AbstractFragmentBinding get() = bindingReference!!

    private class AbstractFragmentBinding(view: View) {
        val interval: TextView? = view.findViewById<TextView>(R.id.interval)
        val buttonNext: ImageView? = view.findViewById<ImageView>(R.id.buttonNext)
        val buttonPrevious: ImageView? = view.findViewById<ImageView>(R.id.buttonPrevious)
        val imageTime: ImageView? = view.findViewById<ImageView>(R.id.imageTime)
    }

    private val supportActionBar: ActionBar?
        get() = (activity as AppCompatActivity?)?.supportActionBar

    abstract val abstractViewModel: IAbstractViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingReference = AbstractFragmentBinding(view)
        setupFooterListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
    }

    private fun setupFooterListener() {
        binding.interval?.setOnClickListener { onIntervalSelect() }
        binding.interval?.setOnLongClickListener { onIntervalReset(); true }
        binding.buttonNext?.setOnClickListener { onButtonNext() }
        binding.buttonPrevious?.setOnClickListener { onButtonPrevious() }
        binding.imageTime?.setOnLongClickListener { onIntervalReset(); true }
        abstractViewModel.intervalLD.observe(viewLifecycleOwner) { interval -> updateUpdateInterval(interval) }
        abstractViewModel.settings.updateLocationAutomaticallyLD.observe(viewLifecycleOwner) { value -> updateLocationAutomatically(value) }
        abstractViewModel.settings.updateTimeAutomaticallyLD.observe(viewLifecycleOwner) { value -> updateTimeAutomatically(value) }
    }

    protected fun updateActionBar(resId: Int, subtitle: String) =
        updateActionBar(getString(resId), subtitle)

    protected fun updateActionBar(title: String, subtitle: String) {
        supportActionBar?.also {
            it.title = title
            it.subtitle = subtitle
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }
    }

    @Suppress("NON_EXHAUSTIVE_WHEN")
    protected open fun onPropertyClick(property: IProperty) {
        when (property.keyType) {
            PropertyKeyType.CONSTELLATION -> onConstellation(property.key)
        }
    }

    @Suppress("NON_EXHAUSTIVE_WHEN")
    protected open fun onPropertyLongClick(property: IProperty) {
        when (property.keyType) {
            PropertyKeyType.STAR -> showNextStepDialogForElement(property.key)
            PropertyKeyType.CONSTELLATION -> showNextStepDialogForElement(property.key)
        }
    }

    fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(getColor(R.color.colorSignalBackground))
            .setTextColor(getColor(R.color.colorSecondaryText))
            .show()
    }

    private fun onIntervalSelect() =
        findNavController().navigate(R.id.action_global_nav_interval_picker)

    private fun onIntervalReset() =
        abstractViewModel.onReset()

    private fun onButtonNext() =
        abstractViewModel.onNext()

    private fun onButtonPrevious() =
        abstractViewModel.onPrevious()

    private fun updateUpdateInterval(interval: Interval) {
        binding.interval?.text = interval.name
    }

    private fun updateLocationAutomatically(value: Boolean) {
        // TODO nothing
    }

    private fun updateTimeAutomatically(value: Boolean) {
        binding.imageTime?.isEnabled = !value
    }

    protected fun bindTime(overlay: TimeOverlayBinding, moment: Moment) {
        overlay.currentTime.text = toLocalTimeString(moment)
        overlay.imageTimeHours.rotation = getHoursAngle(moment)
        overlay.imageTimeMinutes.rotation = getMinutesAngle(moment)
    }

    protected fun bindTime(overlay: TimeVisibilityOverlayBinding, moment: Moment, visibility: Int) {
        overlay.currentTime.text = toLocalTimeString(moment)
        overlay.imageTimeHours.rotation = getHoursAngle(moment)
        overlay.imageTimeMinutes.rotation = getMinutesAngle(moment)
        overlay.currentVisibility.setImageResource(visibility)
    }

    private fun getHoursAngle(moment: Moment): Float =
        30f * moment.localTime.get(Calendar.HOUR_OF_DAY)

    private fun getMinutesAngle(moment: Moment): Float =
        6f * moment.localTime.get(Calendar.MINUTE)

    protected fun getColor(resId: Int): Int =
        ContextCompat.getColor(requireContext(), resId)

    protected fun showNextStepDialogForElement(key: String) {
        abstractViewModel.repository.getElementByKey(key)?.also {
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val tag = "choose_next_step_dialog"
            ChooseNextStepDialog(it).show(fragmentManager, tag)
        }
    }

    protected fun onSkyView(key: String) =
        findNavController().navigate(R.id.action_global_nav_sky, bundleOf("ELEMENT" to key))

    protected fun onFinderView(key: String) =
        findNavController().navigate(R.id.action_global_nav_finder, bundleOf("ELEMENT" to key))

    protected fun onStar(key: String) =
        findNavController().navigate(R.id.action_global_nav_star, bundleOf("STAR" to key))

    protected fun onConstellation(key: String) =
        findNavController().navigate(R.id.action_global_nav_constellation, bundleOf("CONSTELLATION" to key))

    companion object {
        fun toLocalTimeString(moment: Moment): String {
            // val a = Formatter.toString(moment.localTime, Formatter.TimeFormat.TIME)
            // val b = Formatter.toString(moment.utc, moment.timeZone, Formatter.TimeFormat.TIME)
            // both give equal results...
            return Formatter.toString(moment.localTime, Formatter.TimeFormat.TIME_SEC)
        }
        fun toLocalDateString(moment: Moment): String {
            return moment.city.nameEx + ' ' + Formatter.toString(moment.localTime, Formatter.TimeFormat.DATE_TIMEZONE)
        }
    }
}

