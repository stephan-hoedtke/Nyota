package com.stho.nyota.ui.sky

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.SeekBar
import com.stho.nyota.AbstractFragment
import com.stho.nyota.AbstractViewModel
import com.stho.nyota.R
import com.stho.nyota.databinding.FragmentSkyBinding
import com.stho.nyota.sky.utilities.Moment


// TODO: implement options in Sky view: show names, letters, lines, change colors, ...
// TODO: implement styles
// TODO: change updateMoment(...) into onObserveMoment(...) for all observers

class SkyFragment : AbstractFragment() {

    private lateinit var viewModel: SkyViewModel
    private var bindingReference: FragmentSkyBinding? = null
    private val binding: FragmentSkyBinding get() = bindingReference!!

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val elementName = getElementNameFromArguments()
        viewModel = createSkyViewModel(elementName)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentSkyBinding.inflate(inflater, container, false)

        binding.sky.setUniverse(viewModel.universe)
        binding.sky.setReferenceElement(viewModel.element)
        binding.sky.options.loadSettings(viewModel.skyViewSettings)
        binding.buttonZoomIn.setOnClickListener { onZoomIn() }
        binding.buttonZoomOut.setOnClickListener { onZoomOut() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.universeLD.observe(viewLifecycleOwner, { universe -> updateMoment(universe.moment) })
        viewModel.brightnessLD.observe(viewLifecycleOwner, { brightness -> binding.sky.options.brightness = brightness })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sky, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_display -> {
                // TODO: Show dialog to set otions... https://guides.codepath.com/android/using-dialogfragment
                showSnackbar("Show Display Options Dialog here...")
            }
            R.id.action_options -> {
                displayOptionsDialog()
            }
            R.id.action_brightness -> {
                displayBrightnessDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateMoment(moment: Moment) {
        bind(moment)
    }

    private fun bind(moment: Moment) {
        binding.timeOverlay.currentTime.text = toLocalTimeString(moment)
        binding.sky.notifyDataSetChanged()
        updateActionBar(R.string.label_nyota, toLocalDateString(moment))
    }

    private fun onZoomIn() =
        binding.sky.options.applyScale(1.1)

    private fun onZoomOut() =
        binding.sky.options.applyScale(1 / 1.1)

    private fun getElementNameFromArguments(): String? =
        arguments?.getString("ELEMENT")

    private fun displayBrightnessDialog(): Boolean {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        val seek = SeekBar(requireContext())
        seek.max = 255
        seek.keyProgressIncrement = 1
        seek.progress = SkyViewOptions.brightnessToPercent(viewModel.brightness)

        // TODO: set alert dialog theme: https://stackoverflow.com/questions/18346920/change-the-background-color-of-a-pop-up-dialog#

        dialog.setIcon(R.drawable.alpha_gray);
        dialog.setTitle("Brightness")
        dialog.setView(seek)

        seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                viewModel.brightness = SkyViewOptions.percentToBrightness(progress)
            }

            override fun onStartTrackingTouch(arg0: SeekBar) {
                // Nothing
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Nothing
            }
        })

        // Button OK
        dialog.setPositiveButton("OK") { dialog, which -> dialog.dismiss() }
        dialog.setNegativeButton("Close", null);
        dialog.create()
        dialog.show()
        return true
    }

    private fun displayOptionsDialog() {
        val dialog = AlertDialog.Builder(requireContext())
        val names: Array<String> = arrayOf(
            "Names",
            "Symbols",
            "Magnitude",
            "Constellations")

        val values: Array<Boolean> = arrayOf(
            binding.sky.options.displayNames,
            binding.sky.options.displaySymbols,
            binding.sky.options.displayMagnitude,
            binding.sky.options.displayConstellations)

        val booleanArray: BooleanArray = BooleanArray(4) { i -> values[i] }

        dialog.setMultiChoiceItems(names, booleanArray) { dialog, which, isChecked -> when(which) {
            0 -> binding.sky.options.displayNames = isChecked
            1 -> binding.sky.options.displaySymbols = isChecked
            2 -> binding.sky.options.displayMagnitude = isChecked
            3 -> binding.sky.options.displayConstellations = isChecked
        } }
        dialog.setPositiveButton("OK") { dialog, which -> dialog.dismiss() }
        dialog.setNegativeButton("Close", null);
        dialog.setIcon(R.drawable.alpha_gray);
        dialog.setTitle("Options")
        dialog.create()
        dialog.show()
    }

    private fun dontKNow() {
    }
}


