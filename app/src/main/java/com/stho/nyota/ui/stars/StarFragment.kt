package com.stho.nyota.ui.stars

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.stho.nyota.AbstractElementFragment
import com.stho.nyota.AbstractViewModel
import com.stho.nyota.R
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.universe.Star
import com.stho.nyota.sky.utilities.Moment
import kotlinx.android.synthetic.main.fragment_sun.view.*
import kotlinx.android.synthetic.main.time_visibility_overlay.view.*

class StarFragment : AbstractElementFragment() {

    lateinit var viewModel: StarViewModel

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val planetName: String? = getStarNameFromArguments()
        viewModel = createStarViewModel(planetName)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_star, container, false)

        super.setupBasics(root.basics)
        super.setupDetails(root.details)

        viewModel.universeLD.observe(viewLifecycleOwner, Observer { universe -> updateStar(universe.moment) })

        root.buttonSkyView.setOnClickListener { onSkyView() }
        root.buttonFinderView.setOnClickListener { onFinderView() }

        return root
    }

    override val element: IElement
        get() = viewModel.star

    private fun updateStar(moment: Moment) {
        with (viewModel.star) {
            basicsAdapter.updateProperties(this.getBasics(moment))
            detailsAdapter.updateProperties(this.getDetails(moment))
            bind(moment, this)
        }
    }

    private fun bind(moment: Moment, star: Star) {
        view?.also {
            it.currentTime.text = toLocalTimeString(moment)
            it.currentVisibility.setImageResource(star.visibility)
            it.image.setImageResource(star.largeImageId)
            it.title.text = star.name
        }
        updateActionBar(star.name, toLocalDateString(moment))
    }

    private fun getStarNameFromArguments(): String? {
        return arguments?.getString("STAR")
    }
}

