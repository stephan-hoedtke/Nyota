package com.stho.nyota.ui.moon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.stho.nyota.AbstractElementFragment
import com.stho.nyota.AbstractViewModel
import com.stho.nyota.R
import com.stho.nyota.createViewModel
import com.stho.nyota.databinding.FragmentMoonBinding
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.universe.Moon
import com.stho.nyota.sky.utilities.Formatter
import com.stho.nyota.sky.utilities.Moment


class MoonFragment : AbstractElementFragment() {

    private lateinit var viewModel: MoonViewModel
    private var bindingReference: FragmentMoonBinding? = null
    private val binding: FragmentMoonBinding get() = bindingReference!!

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel(MoonViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentMoonBinding.inflate(inflater, container, false)

        super.setupBasics(binding.basics)
        super.setupDetails(binding.details)

        binding.buttonSkyView.setOnClickListener { onSkyView() }
        binding.buttonFinderView.setOnClickListener { onFinderView() }
        binding.timeIntervalFooter.buttonNext.setOnClickListener { viewModel.onNext() }
        binding.timeIntervalFooter.buttonPrevious.setOnClickListener { viewModel.onPrevious() }
        binding.timeIntervalFooter.interval.setOnClickListener { onPickInterval() }
        // TODO: react on touch events on binding.age...

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.moonLD.observe(viewLifecycleOwner, { moon -> updateMoon(moon) })
        viewModel.intervalLD.observe(viewLifecycleOwner, { interval -> binding.timeIntervalFooter.interval.text = interval.name })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
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
        binding.timeVisibilityOverlay.currentVisibility.setImageResource(moon.visibility)
        binding.image.setPhase(moon)
        binding.age.setAge(moon)
        binding.title.text = moon.name
        binding.prevNewMoon.text = Formatter.toString(moon.prevNewMoon!!, moment.timeZone, Formatter.TimeFormat.DATETIME)
        binding.fullMoon.text = Formatter.toString(moon.fullMoon!!, moment.timeZone, Formatter.TimeFormat.DATETIME)
        binding.nextNewMoon.text = Formatter.toString(moon.nextNewMoon!!, moment.timeZone, Formatter.TimeFormat.DATETIME)
        updateActionBar(moon.name, toLocalDateString(moment))
    }

    private fun onPickInterval() {
        val bundle = bundleOf("INTERVAL" to viewModel.interval.toString())
        findNavController().navigate(R.id.action_global_nav_interval_picker, bundle)
    }
}