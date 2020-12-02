package com.stho.nyota

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.stho.nyota.settings.Settings
import com.stho.nyota.sky.universe.Universe
import com.stho.nyota.sky.utilities.Formatter
import com.stho.nyota.sky.utilities.IProperty
import com.stho.nyota.sky.utilities.Moment
import kotlinx.android.synthetic.main.time_interval_footer.view.*
import kotlinx.android.synthetic.main.time_overlay.view.*

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

    private val supportActionBar: ActionBar?
        get() = (activity as AppCompatActivity?)?.supportActionBar

    abstract val abstractViewModel: IAbstractViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFooterListener(view)
    }

    private fun setupFooterListener(view: View) {
        view.interval?.setOnClickListener { onIntervalSelect() }
        view.interval?.setOnLongClickListener { onIntervalReset() }
        view.buttonNext?.setOnClickListener { onButtonNext() }
        view.buttonPrevious?.setOnClickListener { onButtonPrevious() }
        view.imageTime?.setOnLongClickListener { onIntervalReset() }
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
        snack(view,"Display property ${property.name} with value ${property.value} for key ${property.key}")

    protected fun snack(view: View?, message: String) {
        view?.also {
            Snackbar.make(it, message, Snackbar.LENGTH_LONG).show()
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
        view?.also {
            it.interval?.text = interval.name
        }
    }

    private fun updateLocationAutomatically(value: Boolean) {
        // TODO nothing
    }

    private fun updateTimeAutomatically(value: Boolean) {
        view?.also {
            it.imageTime?.isEnabled = !value
        }
    }

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

