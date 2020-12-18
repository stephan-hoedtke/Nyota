package com.stho.nyota

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.stho.nyota.settings.Settings
import com.stho.nyota.sky.universe.Universe
import com.stho.nyota.sky.utilities.Formatter
import com.stho.nyota.sky.utilities.IProperty
import com.stho.nyota.sky.utilities.Moment

abstract class AbstractFragment : Fragment() {

    interface IAbstractViewModel {
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
        binding.interval?.setOnLongClickListener { onIntervalReset() }
        binding.buttonNext?.setOnClickListener { onButtonNext() }
        binding.buttonPrevious?.setOnClickListener { onButtonPrevious() }
        binding.imageTime?.setOnLongClickListener { onIntervalReset() }
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

    protected fun openProperty(property: IProperty) =
        showSnackBar("Display property ${property.name} with value ${property.value} for key ${property.key}")

    fun showSnackBar(message: String) {
        view?.also {
            Snackbar.make(it, message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getColor(R.color.colorSignalBackground))
                .setTextColor(getColor(R.color.colorSecondaryText))
                .show()
        }
    }

    private fun onIntervalSelect() =
        findNavController().navigate(R.id.action_global_nav_interval_picker)

    private fun onIntervalReset(): Boolean {
        abstractViewModel.onReset()
        return true
    }

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

    protected fun getColor(resId: Int): Int =
        ContextCompat.getColor(requireContext(), resId)

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

