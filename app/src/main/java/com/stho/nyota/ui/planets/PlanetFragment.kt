package com.stho.nyota.ui.planets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.stho.nyota.AbstractElementFragment
import com.stho.nyota.AbstractViewModel
import com.stho.nyota.R
import com.stho.nyota.sky.universe.AbstractPlanet
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.utilities.Moment
import kotlinx.android.synthetic.main.fragment_planet.view.*
import kotlinx.android.synthetic.main.time_visibility_overlay.view.*


class PlanetFragment : AbstractElementFragment() {

    lateinit var viewModel: PlanetViewModel

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val planetName: String? = getPlanetNameFromArguments()
        viewModel = createPlanetViewModel(planetName)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(com.stho.nyota.R.layout.fragment_planet, container, false)

        super.setupBasics(root.basics)
        super.setupDetails(root.details)

        viewModel.universeLD.observe(viewLifecycleOwner, Observer { universe -> updatePlanet(universe.moment) })
        root.buttonSkyView.setOnClickListener { onSkyView() }
        root.buttonFinderView.setOnClickListener { onFinderView() }

        return root
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
        view?.also {
            it.currentTime.text = toLocalTimeString(moment)
            it.currentVisibility.setImageResource(planet.visibility)
            it.image.setImageResource(planet.largeImageId)
            it.image.setPhase(planet);
            it.title.text = planet.name
        }
        updateActionBar(planet.name, toLocalDateString(moment))
    }

    private fun getPlanetNameFromArguments(): String? {
        return arguments?.getString("PLANET")
    }
}