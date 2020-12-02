package com.stho.nyota.ui.constellations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.stho.nyota.AbstractElementFragment
import com.stho.nyota.AbstractViewModel
import com.stho.nyota.R
import com.stho.nyota.sky.universe.Constellation
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.utilities.Moment
import kotlinx.android.synthetic.main.fragment_constellation.view.*
import kotlinx.android.synthetic.main.time_visibility_overlay.*

class ConstellationFragment : AbstractElementFragment() {

    private lateinit var viewModel: ConstellationViewModel

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val constellationName: String? = getConstellationNameFromArguments()
        viewModel = createConstellationViewModel(constellationName)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_constellation, container, false)

        super.setupBasics(root.basics)
        super.setupDetails(root.details)

        viewModel.universeLD.observe(viewLifecycleOwner, Observer { universe -> onUpdateConstellation(universe.moment) })

        root.buttonSkyView.setOnClickListener { onSkyView() }
        root.buttonFinderView.setOnClickListener { onFinderView() }
        return root
    }

    override val element: IElement
        get() = viewModel.constellation

    private fun onUpdateConstellation(moment: Moment) {
        viewModel.constellation.also {
            basicsAdapter.updateProperties(it.getBasics(moment))
            detailsAdapter.updateProperties(it.getDetails(moment))
            bind(moment, it)
        }
    }

    private fun bind(moment: Moment, constellation: Constellation) {
        view?.apply {
            currentTime.text = toLocalTimeString(moment)
            currentVisibility.setImageResource(constellation.visibility)
            image.setImageResource(constellation.largeImageId)
            title.text = constellation.name
        }
        updateActionBar(constellation.name, toLocalDateString(moment))
    }

    private fun getConstellationNameFromArguments(): String? =
        arguments?.getString("CONSTELLATION")
}
