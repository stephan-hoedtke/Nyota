package com.stho.nyota.ui.sun

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stho.nyota.AbstractElementFragment
import com.stho.nyota.AbstractViewModel
import com.stho.nyota.createViewModel
import com.stho.nyota.databinding.FragmentSunBinding
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.universe.Sun
import com.stho.nyota.sky.utilities.Moment


class SunFragment : AbstractElementFragment() {

    private lateinit var viewModel: SunViewModel
    private var bindingReference: FragmentSunBinding? = null
    private val binding: FragmentSunBinding get() = bindingReference!!

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel(SunViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentSunBinding.inflate(inflater, container,false)

        super.setupBasics(binding.basics)
        super.setupDetails(binding.details)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.universeLD.observe(viewLifecycleOwner, { universe -> updateSun(universe.moment) })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
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
        bindTime(binding.timeVisibilityOverlay, moment, sun.visibility)
        binding.title.text = sun.name
        setActionBarTitle(sun.name)
    }
}