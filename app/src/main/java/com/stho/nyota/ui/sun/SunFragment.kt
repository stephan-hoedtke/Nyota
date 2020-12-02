package com.stho.nyota.ui.sun

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.stho.nyota.createViewModel
import com.stho.nyota.sky.universe.Sun
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.AbstractElementFragment
import com.stho.nyota.AbstractViewModel
import com.stho.nyota.sky.universe.IElement
import kotlinx.android.synthetic.main.fragment_sun.view.*
import kotlinx.android.synthetic.main.time_visibility_overlay.view.*


class SunFragment : AbstractElementFragment() {

    lateinit var viewModel: SunViewModel

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel(SunViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(com.stho.nyota.R.layout.fragment_sun, container, false)

        super.setupBasics(root.basics)
        super.setupDetails(root.details)

        viewModel.universeLD.observe(viewLifecycleOwner, Observer { universe -> updateSun(universe.moment) })

        return root
    }

    override val element: IElement
        get() = viewModel.sun

    private fun updateSun(moment: Moment) {
       viewModel.sun.also {
            basicsAdapter.updateProperties(it.getBasics(moment))
            detailsAdapter.updateProperties(it.getDetails(moment))
            bind(moment, it)
        }
    }

    private fun bind(moment: Moment, sun: Sun) {
        view?.also {
            it.currentTime.text = toLocalTimeString(moment)
            it.currentVisibility.setImageResource(sun.visibility)
            it.title.text = sun.name
        }
        updateActionBar(sun.name, toLocalDateString(moment))
    }

}