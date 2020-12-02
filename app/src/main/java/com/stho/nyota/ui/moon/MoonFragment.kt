package com.stho.nyota.ui.moon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.stho.nyota.AbstractElementFragment
import com.stho.nyota.AbstractViewModel
import com.stho.nyota.R
import com.stho.nyota.createViewModel
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.universe.Moon
import com.stho.nyota.sky.utilities.Formatter
import com.stho.nyota.sky.utilities.Moment
import kotlinx.android.synthetic.main.fragment_moon.view.*
import kotlinx.android.synthetic.main.time_interval_footer.view.*
import kotlinx.android.synthetic.main.time_visibility_overlay.view.*


class MoonFragment : AbstractElementFragment() {

    private lateinit var viewModel: MoonViewModel

    override val abstractViewModel: AbstractViewModel
        get() = viewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel(MoonViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root= inflater.inflate(com.stho.nyota.R.layout.fragment_moon, container, false)

        super.setupBasics(root.basics)
        super.setupDetails(root.details)

        root.buttonSkyView.setOnClickListener { onSkyView() }
        root.buttonFinderView.setOnClickListener { onFinderView() }
        root.buttonNext.setOnClickListener { viewModel.onNext() }
        root.buttonPrevious.setOnClickListener { viewModel.onPrevious() }
        root.interval.setOnClickListener { onPickInterval() }
        root.age.set

        viewModel.moonLD.observe(viewLifecycleOwner, Observer { moon -> updateMoon(moon) })
        viewModel.intervalLD.observe(viewLifecycleOwner, Observer { interval -> view?.interval?.text = interval.name })

        return root
    }

    override val element: IElement
        get() = viewModel.moon

    private fun updateMoon(moon: Moon) {
        updateMoon(viewModel.moment, moon)
    }

    private fun updateMoon(moment: Moment, moon: Moon) {
        basicsAdapter.updateProperties(moon.getBasics(moment))
        detailsAdapter.updateProperties(moon.getDetails(moment))
        bind(viewModel.moment, moon)
    }

    private fun bind(moment: Moment, moon: Moon) {
        view?.also {
            it.currentTime.text = toLocalTimeString(moment)
            it.currentVisibility.setImageResource(moon.visibility)
            it.image.setPhase(moon);
            it.age.setAge(moon);
            it.title.text = moon.name
            it.prevNewMoon.text = Formatter.toString(moon.prevNewMoon!!, moment.timeZone, Formatter.TimeFormat.DATETIME)
            it.fullMoon.text = Formatter.toString(moon.fullMoon!!, moment.timeZone, Formatter.TimeFormat.DATETIME)
            it.nextNewMoon.text = Formatter.toString(moon.nextNewMoon!!, moment.timeZone, Formatter.TimeFormat.DATETIME)
        }
        updateActionBar(moon.name, toLocalDateString(moment))
    }

    private fun onPickInterval() {
        val bundle = bundleOf("INTERVAL" to viewModel.interval.toString())
        findNavController().navigate(R.id.action_global_nav_interval_picker, bundle)
    }
}