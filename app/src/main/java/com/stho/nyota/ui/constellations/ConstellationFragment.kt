package com.stho.nyota.ui.constellations

import android.os.Bundle
import android.view.*
import com.stho.nyota.AbstractElementFragment
import com.stho.nyota.AbstractViewModel
import com.stho.nyota.R
import com.stho.nyota.databinding.FragmentConstellationBinding
import com.stho.nyota.sky.universe.Constellation
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.utilities.IProperty
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.sky.utilities.PropertyKey

// TODO: show constellation in "real sky view", not just the Icon
// see: https://en.wikipedia.org/wiki/Greek_alphabet (modern print)


class ConstellationFragment : AbstractElementFragment() {

    private lateinit var viewModel: ConstellationViewModel
    private var bindingReference: FragmentConstellationBinding? = null
    private val binding: FragmentConstellationBinding get() = bindingReference!!

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val constellationName: String? = getConstellationNameFromArguments()
        viewModel = createConstellationViewModel(constellationName)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentConstellationBinding.inflate(inflater, container, false)

        super.setupBasics(binding.basics)
        super.setupDetails(binding.details)

        binding.sky.setConstellation(viewModel.constellation)
        binding.buttonSkyView.setOnClickListener { onSkyView() }
        binding.buttonFinderView.setOnClickListener { onFinderView() }
        binding.timeVisibilityOverlay.currentVisibility.setOnClickListener { onToggleImage() }
        binding.buttonZoomIn.setOnClickListener { onZoomIn() }
        binding.buttonZoomOut.setOnClickListener { onZoomOut() }
        binding.image.alpha = 0f
        binding.image.setOnClickListener { onSkyView() }
        binding.image.setOnLongClickListener { onFinderView(); true }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.universeLD.observe(viewLifecycleOwner, { universe -> onUpdateConstellation(universe.moment) })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_constellation, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_display) {
            // TODO: Show dialog to set otions... https://guides.codepath.com/android/using-dialogfragment
            showSnackbar("Show Display Option Dialog here...")
        }
        return super.onOptionsItemSelected(item)
    }

    override val element: IElement
        get() = viewModel.constellation


    @Suppress("NON_EXHAUSTIVE_WHEN")
    override fun onPropertyClick(property: IProperty) {
        when (property.key) {
            PropertyKey.STAR ->
                viewModel.constellation.stars.firstOrNull { star -> star.name == property.name } ?.let {
                    binding.sky.setStar(it)
                    binding.sky.invalidate()
                }
        }
    }

    @Suppress("NON_EXHAUSTIVE_WHEN")
    override fun onPropertyLongClick(property: IProperty) {
        when (property.key) {
            PropertyKey.STAR ->
                viewModel.constellation.stars.firstOrNull { star -> star.name == property.name }?.let {
                    onStar(it.uniqueName)
                }
        }
    }

    private fun onUpdateConstellation(moment: Moment) {
        viewModel.constellation.also {
            basicsAdapter.updateProperties(it.getBasics(moment))
            detailsAdapter.updateProperties(it.getDetails(moment))
            bind(moment, it)
        }
    }

    private fun bind(moment: Moment, constellation: Constellation) {
        binding.timeVisibilityOverlay.currentTime.text = toLocalTimeString(moment)
        binding.timeVisibilityOverlay.currentVisibility.setImageResource(constellation.visibility)
        binding.image.setImageResource(constellation.largeImageId)
        binding.title.text = constellation.name
        binding.sky.notifyDataSetChanged()
        updateActionBar(constellation.name, toLocalDateString(moment))
    }

    private fun onToggleImage() {

        // TODO: Use animation...
        if (binding.image.alpha < 1f) {
            binding.image.alpha = 1f
            binding.sky.visibility = View.INVISIBLE
        } else {
            binding.image.alpha = 0f
            binding.sky.visibility = View.VISIBLE
        }
    }

    private fun onZoomIn() {
        binding.sky.options.applyScale(1.1)
    }

    private fun onZoomOut() {
        binding.sky.options.applyScale(1 / 1.1)
    }

    private fun getConstellationNameFromArguments(): String? =
        arguments?.getString("CONSTELLATION")
}
