package com.stho.nyota.ui.stars

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stho.nyota.AbstractElementFragment
import com.stho.nyota.AbstractViewModel
import com.stho.nyota.databinding.FragmentStarBinding
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.universe.Star
import com.stho.nyota.sky.utilities.Moment


class StarFragment : AbstractElementFragment() {

    private lateinit var viewModel: StarViewModel
    private var bindingReference: FragmentStarBinding? = null
    private val binding: FragmentStarBinding get() = bindingReference!!

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val key: String? = getKeyFromArguments()
        viewModel = createStarViewModel(key)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentStarBinding.inflate(inflater, container, false)

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
        viewModel.universeLD.observe(viewLifecycleOwner, { universe -> updateStar(universe.moment) })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
    }

    override val element: IElement
        get() = viewModel.star

    private fun updateStar(moment: Moment) {
        with(viewModel.star) {
            basicsAdapter.updateProperties(this.getBasics(moment))
            detailsAdapter.updateProperties(this.getDetails(moment))
            bind(moment, this)
        }
    }

    private fun bind(moment: Moment, star: Star) {
        bindTime(binding.timeVisibilityOverlay, moment, star.visibility)
        binding.image.setImageResource(star.largeImageId)
        setActionBarTitle(star.toString())
    }

    private fun getKeyFromArguments(): String? =
        arguments?.getString("STAR")

}
