package com.stho.nyota

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
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
import com.stho.nyota.sky.universe.*
import com.stho.nyota.sky.utilities.Formatter
import com.stho.nyota.sky.utilities.IProperty
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.sky.utilities.PropertyKeyType
import com.stho.nyota.ui.constellations.ChooseNextStepDialog
import com.stho.nyota.ui.home.HomeFragmentDirections
import java.util.*

abstract class AbstractFragment : Fragment(), INavigable {

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

        val showIntervalLD: LiveData<Boolean>
        val showDetailsLD: LiveData<Boolean>

        val showInterval: Boolean
        val showDetails: Boolean

        fun onToggleShowInterval()
        fun onToggleShowDetails()
    }

    private var bindingReference: AbstractFragmentBinding? = null
    private val binding: AbstractFragmentBinding get() = bindingReference!!

    /**
     * Implement the Binding manually:
     * a) Interval footer: buttonPrevious, interval, buttonNext
     * b) currentTime, imageTime
     */
    private class AbstractFragmentBinding(view: View) {
        val interval: TextView? = view.findViewById<TextView>(R.id.interval)
        val buttonNext: ImageView? = view.findViewById<ImageView>(R.id.buttonNext)
        val buttonPrevious: ImageView? = view.findViewById<ImageView>(R.id.buttonPrevious)
        val imageTime: ImageView? = view.findViewById<ImageView>(R.id.imageTime)
        val currentTime: TextView? = view.findViewById<TextView>(R.id.currentTime)
        val timeIntervalFooter: ViewGroup? = view.findViewById<ViewGroup>(R.id.time_interval_footer)
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

    /**
     * click on Clock
     * click on Time(Text)    --> toggle Interval footer
     */
    private fun setupFooterListener() {
        binding.interval?.setOnClickListener { onIntervalSelect() }
        binding.interval?.setOnLongClickListener { onIntervalReset(); true }
        binding.buttonNext?.setOnClickListener { onButtonNext() }
        binding.buttonPrevious?.setOnClickListener { onButtonPrevious() }
        binding.currentTime?.setOnClickListener { onToggleShowInterval() }
        binding.imageTime?.setOnClickListener { onToggleShowInterval() }
        abstractViewModel.intervalLD.observe(viewLifecycleOwner) { interval -> onObserveInterval(interval) }
        abstractViewModel.showIntervalLD.observe(viewLifecycleOwner, { value -> onObserveShowInterval(value) })
        abstractViewModel.settings.updateTimeAutomaticallyLD.observe(viewLifecycleOwner) { value -> onObserveUpdateTimeAutomatically(value) }
    }

    protected fun updateActionBar(resId: Int) =
        updateActionBar(getString(resId))

    protected fun updateActionBar(title: String) {
        supportActionBar?.also {
            it.title = title
            it.subtitle = null
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }
    }

    @Suppress("NON_EXHAUSTIVE_WHEN")
    protected open fun onPropertyClick(property: IProperty) {
        when (property.keyType) {
            PropertyKeyType.CONSTELLATION -> onConstellation(property.key)
            PropertyKeyType.STAR -> onStar(property.key)
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

    private fun onToggleShowInterval() =
        abstractViewModel.onToggleShowInterval()

    private fun onTime() {
        abstractViewModel.settings.updateTimeAutomatically = false
    }

    private fun onObserveShowInterval(value: Boolean) {
        binding.timeIntervalFooter?.visibility = if (value) View.GONE else View.VISIBLE
        binding.timeIntervalFooter?.layoutParams?.height = if (value) 0 else android.app.ActionBar.LayoutParams.WRAP_CONTENT
    }

    private fun onObserveInterval(interval: Interval) {
        binding.interval?.text = interval.name
    }

    private fun onObserveUpdateTimeAutomatically(value: Boolean) {
        binding.imageTime?.isEnabled = !value
    }

    protected fun bindTime(overlay: TimeOverlayBinding, moment: Moment) {
        overlay.currentTime.text = toTimeString(moment)
        overlay.currentDate.text = toDateString(moment)
        overlay.currentCity.text = toCityString(moment)
        overlay.imageTimeHours.rotation = getHoursAngle(moment)
        overlay.imageTimeMinutes.rotation = getMinutesAngle(moment)
    }

    protected fun bindTime(overlay: TimeVisibilityOverlayBinding, moment: Moment, visibility: Int) {
        overlay.currentTime.text = toTimeString(moment)
        overlay.currentDate.text = toDateString(moment)
        overlay.currentCity.text = toCityString(moment)
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

    protected fun showNextStepDialogForElement(key: String) =
        abstractViewModel.repository.getElementByKey(key)?.also {
            showNextStepDialogForElement(it)
        }

    override fun showNextStepDialogForElement(element: IElement) {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val tag = "DIALOG"
        ChooseNextStepDialog(element, this).show(fragmentManager, tag)
    }

    override fun onStar(star: Star) =
        onStar(star.key)

    override fun onPlanet(planet: AbstractPlanet) =
        onPlanet(planet.key)

    override fun onConstellation(constellation: Constellation) =
        onConstellation(constellation.key)

    override fun onSatellite(satellite: Satellite) =
        onSatellite(satellite.key)

    override fun onGalaxy(galaxy: Galaxy) =
        onFinderView(galaxy.key)

    private fun onSkyView() =
        findNavController().navigate(HomeFragmentDirections.actionNavHomeToNavSky(null))

    override fun onSkyView(element: IElement) =
        onSkyView(element.key)

    override fun onFinderView(element: IElement) =
        onFinderView(element.key)


    private fun onSkyView(key: String) =
        findNavController().navigate(R.id.action_global_nav_sky, bundleOf("ELEMENT" to key))

    protected fun onFinderView(key: String) =
        findNavController().navigate(R.id.action_global_nav_finder, bundleOf("ELEMENT" to key))

    override fun onSun() =
        findNavController().navigate(R.id.action_global_nav_sun)

    override fun onMoon() =
        findNavController().navigate(R.id.action_global_nav_moon)

    protected fun onStar(key: String) =
        findNavController().navigate(R.id.action_global_nav_star, bundleOf("STAR" to key))

    protected fun onPlanet(key: String) =
        findNavController().navigate(R.id.action_global_nav_planet, bundleOf("PLANET" to key))

    protected fun onSatellite(key: String) =
        findNavController().navigate(R.id.action_global_nav_satellite, bundleOf("SATELLITE" to key))

    protected fun onConstellation(key: String) =
        findNavController().navigate(R.id.action_global_nav_constellation, bundleOf("CONSTELLATION" to key))

    companion object {
        private fun toTimeString(moment: Moment): String =
            Formatter.toString(moment.localTime, Formatter.TimeFormat.TIME_SEC_TIMEZONE)

        private fun toDateString(moment: Moment): String =
            Formatter.toString(moment.localTime, Formatter.TimeFormat.DATE)

        private fun toCityString(moment: Moment): String =
            moment.city.nameEx
    }
}

