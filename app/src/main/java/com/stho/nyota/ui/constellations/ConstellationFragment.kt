package com.stho.nyota.ui.constellations

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.stho.nyota.AbstractElementFragment
import com.stho.nyota.AbstractViewModel
import com.stho.nyota.ISkyViewListener
import com.stho.nyota.R
import com.stho.nyota.databinding.FragmentConstellationBinding
import com.stho.nyota.sky.universe.Constellation
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.universe.Star
import com.stho.nyota.sky.utilities.*


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
        val key: String? = getKeyFromArguments()
        viewModel = createConstellationViewModel(key)
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
        binding.sky.registerListener(object : ISkyViewListener {
            override fun onChangeCenter() {
                // Ignore
            }

            override fun onChangeZoom() {
                // Ignore
            }

            override fun onDoubleTap() {
                binding.sky.setTippedStar(null)
                binding.sky.setReferenceStar(null)
                binding.sky.resetTransformation()
            }

            override fun onSingleTap(position: Topocentric) {
                displaySnackbarForPosition(position)
            }
        })
        binding.image.alpha = 0f
        binding.grip.setOnTouchListener(object: View.OnTouchListener {
            private var active: Boolean = false
            private var startY: Float = 0f
            private var startBegin: Int = 0

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        val params = binding.horizontalGuideline1.layoutParams as ConstraintLayout.LayoutParams
                        startBegin = params.guideBegin
                        startY = event.rawY
                        active = true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (active) {
                            val value = (event.rawY - startY) + startBegin
                            val params = binding.horizontalGuideline1.layoutParams as ConstraintLayout.LayoutParams;
                            params.guideBegin = value.toInt()
                            binding.horizontalGuideline1.layoutParams = params
                        }
                    }
                    MotionEvent.ACTION_OUTSIDE -> active = false
                    MotionEvent.ACTION_CANCEL -> active = false
                }
                return true
            }
        })

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
        when (property.keyType) {
            PropertyKeyType.STAR -> viewModel.constellation.findStarInConstellationByKey(property.key)?.let {
                binding.sky.setReferenceStar(it)
                binding.sky.invalidate()
            }
            else -> super.onPropertyClick(property)
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
        bindTime(binding.timeVisibilityOverlay, moment, constellation.visibility)
        binding.image.setImageResource(constellation.largeImageId)
        binding.sky.notifyDataSetChanged()
        updateActionBar(constellation.name, toLocalDateString(moment))
    }

    private fun displaySnackbarForPosition(position: Topocentric) {
        viewModel.constellation.findNearestStarByPosition(position, binding.sky.options.magnitude, binding.sky.sensitivityAngle)?.let {
            displaySnackbarForStarAtPosition(position, it)
            // TODO: select the respective list item
        }
    }

    private fun displaySnackbarForStarAtPosition(position: Topocentric, star: Star) {
        binding.sky.setTippedStar(star)

        val message: String = messageTextForStar(position, star)

        Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE)
            .setBackgroundTint(getColor(R.color.colorSignalBackground))
            .setTextColor(getColor(R.color.colorSecondaryText))
            .setDuration(3000)
            .setAction(star.toString()) { onStar(star) }
            .show()

    }

    private fun messageTextForStar(position: Topocentric, star: Star): String =
        if (star.hasSymbol)
            "Star ${star.symbol.greekSymbol} ${star.magnAsString} at $position"
        else
            "Star ${star.magnAsString} at $position "


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

    private fun onStar(star: Star) =
        findNavController().navigate(R.id.action_global_nav_star, bundleOf("STAR" to star.key))

    private fun onZoomIn() {
        binding.sky.applyScale(1.1)
    }

    private fun onZoomOut() {
        binding.sky.applyScale(1 / 1.1)
    }

    private fun getKeyFromArguments(): String? =
        arguments?.getString("CONSTELLATION")
}
