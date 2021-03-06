package com.stho.nyota.ui.constellations

import android.os.Bundle
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import com.stho.nyota.AbstractElementFragment
import com.stho.nyota.AbstractViewModel
import com.stho.nyota.ISkyViewListener
import com.stho.nyota.R
import com.stho.nyota.databinding.FragmentConstellationBinding
import com.stho.nyota.settings.ViewStyle
import com.stho.nyota.sky.universe.Constellation
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.universe.Star
import com.stho.nyota.sky.utilities.*


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

        binding.sky.setOptions(viewModel.options)
        binding.sky.setConstellation(viewModel.constellation)
        binding.buttonSkyView.setOnClickListener { onSkyView() }
        binding.buttonFinderView.setOnClickListener { onFinderView() }
        binding.timeVisibilityOverlay.currentVisibility.setOnClickListener { onToggleImage() }
        binding.buttonZoomIn.setOnClickListener { onZoomIn() }
        binding.buttonZoomOut.setOnClickListener { onZoomOut() }
        binding.buttonToggleStyle.setOnClickListener { onToggleStyle() }
        binding.sky.registerListener(object : ISkyViewListener {
            override fun onChangeCenter() {
                viewModel.setCenter(binding.sky.center)
            }

            override fun onChangeZoom() {
                viewModel.setZoomAngle(binding.sky.zoomAngle)
            }

            override fun onDoubleTap() {
                binding.sky.setTippedStar(null)
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
                            val params = binding.horizontalGuideline1.layoutParams as ConstraintLayout.LayoutParams
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
        viewModel.zoomAngleLD.observe(viewLifecycleOwner, { zoomAngle -> onObserveZoomAngle(zoomAngle) })
        viewModel.centerLD.observe(viewLifecycleOwner, { center -> onObserveCenter(center) })
        viewModel.options.versionLD.observe(viewLifecycleOwner, { _ -> binding.sky.touch() })
        viewModel.styleLD.observe(viewLifecycleOwner, { style -> onObserveStyle(style) })
        viewModel.tipLD.observe(viewLifecycleOwner, { tip -> onObserveTip(tip) })
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
        when (item.itemId) {
            R.id.action_display -> displayConstellationFragmentOptionsDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    override val element: IElement
        get() = viewModel.constellation


    override fun onPropertyClick(property: IProperty) {
        when (property.keyType) {
            PropertyKeyType.STAR -> viewModel.constellation.findStarInConstellationByKey(property.key)?.let {
                viewModel.setTippedStar(it)
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

    private fun onObserveZoomAngle(zoomAngle: Double) {
        binding.sky.zoomAngle = zoomAngle
    }

    private fun onObserveCenter(center: Topocentric) {
        binding.sky.setCenter(center)
    }

    private fun onObserveTip(tip: ConstellationViewModel.Tip) {
        binding.sky.setTippedStar(tip.star)
        basicsAdapter.selectItem(tip.star);
        detailsAdapter.selectItem(tip.star)
    }

    private fun onObserveStyle(style: ViewStyle) {
        binding.buttonToggleStyle.setImageResource(
            when (style) {
                ViewStyle.Normal -> R.drawable.view_style_yellow
                ViewStyle.HintsOnly -> R.drawable.view_style_red
                ViewStyle.Plain -> R.drawable.view_style_blue
            }
        )
        viewModel.options.style = style
    }

    private fun bind(moment: Moment, constellation: Constellation) {
        bindTime(binding.timeVisibilityOverlay, moment, constellation.visibility)
        binding.image.setImageResource(constellation.largeImageId)
        binding.sky.notifyDataSetChanged()
        setActionBarTitle(constellation.name)
    }

    private fun displaySnackbarForPosition(position: Topocentric) {
        viewModel.constellation.findNearestStarByPosition(position, binding.sky.options.magnitude, binding.sky.sensitivityAngle)?.let {
            displaySnackbarForStar(it)
        }
    }

    private fun displaySnackbarForStar(star: Star) {
        viewModel.setTippedStar(star)

        val message: String = messageTextForStar(star)

        Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE)
            .setBackgroundTint(getColor(R.color.colorSignalBackground))
            .setTextColor(getColor(R.color.colorSecondaryText))
            .setDuration(13000)
            .setAction(star.toString()) { onStar(star) }
            .addCallback(object: Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    viewModel.undoTip(star)
                }
            })
            .show()

    }

    private fun messageTextForStar(star: Star): String =
        if (star.hasSymbol)
            "Star ${star.symbol.greekSymbol} ${star.magnAsString}"
        else
            "Star ${star.magnAsString}"


    private fun onToggleImage() {
        if (binding.image.alpha < 1f) {
            binding.image.alpha = 1f
            binding.sky.visibility = View.INVISIBLE
        } else {
            binding.image.alpha = 0f
            binding.sky.visibility = View.VISIBLE
        }
    }

    override fun onStar(star: Star) {
        viewModel.setTippedStar(star)
        super.onStar(star)
    }

    private fun onZoomIn() {
        viewModel.applyScale(1.1)
    }

    private fun onZoomOut() {
        viewModel.applyScale(1 / 1.1)
    }

    private fun onToggleStyle() {
        viewModel.onToggleStyle()
    }

    private fun displayConstellationFragmentOptionsDialog() {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val tag = "DIALOG"
        ConstellationFragmentOptionsDialog(viewModel.options).show(fragmentManager, tag)
    }

    private fun getKeyFromArguments(): String? =
        arguments?.getString("CONSTELLATION")
}
