package com.stho.nyota.ui.sky

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.stho.nyota.AbstractFragment
import com.stho.nyota.AbstractViewModel
import com.stho.nyota.R
import com.stho.nyota.sky.utilities.Moment
import kotlinx.android.synthetic.main.fragment_sky.view.*
import kotlinx.android.synthetic.main.time_overlay.view.*

class SkyFragment : AbstractFragment() {

    private lateinit var viewModel: SkyViewModel

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val elementName = getElementNameFromArguments()
        viewModel = createSkyViewModel(elementName);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(com.stho.nyota.R.layout.fragment_sky, container, false)

        root.sky.setUniverse(viewModel.universe)
        root.sky.setReferenceElement(viewModel.element)
        root.sky.loadSettings(viewModel.skyViewSettings)

        viewModel.universeLD.observe(viewLifecycleOwner, Observer { universe -> updateMoment(universe.moment) })

        return root
    }

    private fun updateMoment(moment: Moment) {
        bind(moment)
    }

    private fun bind(moment: Moment) {
        view?.also {
            it.currentTime.text = toLocalTimeString(moment)
            it.sky.notifyDataSetChanged()
        }
        updateActionBar(R.string.label_nyota, toLocalDateString(moment))
    }

    private fun getElementNameFromArguments(): String? {
        return arguments?.getString("ELEMENT")
    }
}