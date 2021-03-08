package com.stho.nyota.ui.planets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stho.nyota.AbstractElementFragment
import com.stho.nyota.AbstractViewModel
import com.stho.nyota.databinding.FragmentPlanetBinding
import com.stho.nyota.sky.universe.AbstractPlanet
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.utilities.Moment

class PlanetFragment : AbstractElementFragment() {

    private lateinit var viewModel: PlanetViewModel
    private var bindingReference: FragmentPlanetBinding? = null
    private val binding: FragmentPlanetBinding get() = bindingReference!!

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val planetName: String? = getPlanetNameFromArguments()
        viewModel = createPlanetViewModel(planetName)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentPlanetBinding.inflate(inflater, container, false)

        super.setupBasics(binding.basics)
        super.setupDetails(binding.details)

        binding.buttonSkyView.setOnClickListener { onSkyView() }
        binding.buttonFinderView.setOnClickListener { onFinderView() }
        binding.image.setOnClickListener { onSkyView() }
        binding.image.setOnLongClickListener { onFinderView(); true }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.universeLD.observe(viewLifecycleOwner, { universe -> updatePlanet(universe.moment) })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
    }

    override val element: IElement
        get() = viewModel.planet

    private fun updatePlanet(moment: Moment) {
        viewModel.planet.also {
            basicsAdapter.updateProperties(it.getBasics(moment))
            detailsAdapter.updateProperties(it.getDetails(moment))
            bind(moment, it)
        }
    }

    private fun bind(moment: Moment, planet: AbstractPlanet) {
        bindTime(binding.timeVisibilityOverlay, moment, planet.visibility)
        binding.image.setImageResource(planet.largeImageId)
        binding.image.setPhase(planet)
        updateActionBar(planet.name)
    }

    private fun getPlanetNameFromArguments(): String? {
        return arguments?.getString("PLANET")
    }
}