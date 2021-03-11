package com.stho.nyota.ui.moon

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
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
import kotlin.math.abs


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

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentMoonBinding.inflate(inflater, container, false)

        super.setupBasics(binding.basics)
        super.setupDetails(binding.details)

        binding.buttonSkyView.setOnClickListener { onSkyView() }
        binding.buttonFinderView.setOnClickListener { onFinderView() }
        binding.timeIntervalFooter.buttonNext.setOnClickListener { viewModel.onNext() }
        binding.timeIntervalFooter.buttonPrevious.setOnClickListener { viewModel.onPrevious() }
        binding.timeIntervalFooter.interval.setOnClickListener { onPickInterval() }
        binding.image.setOnClickListener { onSkyView() }
        binding.image.setOnLongClickListener { onFinderView(); true }

        // TODO: solve the annotation issue with accessibility (I do not understand yet, what to do)
        binding.moonAgeLayout.age.setOnTouchListener(@SuppressLint("ClickableViewAccessibility")
        object : OnTouchListener {
            private var startX = 0f
            private var previousX = 0f

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> startX = event.x
                    MotionEvent.ACTION_MOVE -> viewModel.addHours(hoursToMove(event))
                    MotionEvent.ACTION_UP -> if (isJustAClickOnlyNotAMove(event)) {
                            when {
                                event.x < 0.4 * v.width -> viewModel.previousFullMoon()
                                event.x > 0.6 * v.width -> viewModel.nextFullMoon()
                                else -> viewModel.forNow()
                            }
                        }
                }
                previousX = event.x
                return true
            }

            private fun isJustAClickOnlyNotAMove(event: MotionEvent) =
                abs(event.x - startX) < 3.0

            private fun hoursToMove(event: MotionEvent) =
                (event.x - previousX).toDouble()
        })

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
        bindTime(binding.timeVisibilityOverlay, moment, moon.visibility)
        binding.image.setPhase(moon)
        binding.moonAgeLayout.age.setAge(moon)
        binding.moonAgeLayout.prevNewMoon.text = Formatter.toString(moon.prevNewMoon!!, moment.timeZone, Formatter.TimeFormat.DATETIME)
        binding.moonAgeLayout.fullMoon.text = Formatter.toString(moon.fullMoon!!, moment.timeZone, Formatter.TimeFormat.DATETIME)
        binding.moonAgeLayout.nextNewMoon.text = Formatter.toString(moon.nextNewMoon!!, moment.timeZone, Formatter.TimeFormat.DATETIME)

        setActionBarTitle(moon.name)
    }

    private fun onPickInterval() {
        val bundle = bundleOf("INTERVAL" to viewModel.interval.toString())
        findNavController().navigate(R.id.action_global_nav_interval_picker, bundle)
    }
}