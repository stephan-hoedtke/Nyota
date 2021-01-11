package com.stho.nyota.ui.sky

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.DialogFragment
import com.stho.nyota.databinding.FragmentSkyDialogOptionsBinding
import com.stho.nyota.sky.utilities.Formatter


class SkyFragmentOptionsDialog(val options: ISkyViewOptions): DialogFragment() {

    private var bindingReference: FragmentSkyDialogOptionsBinding? = null
    private val binding: FragmentSkyDialogOptionsBinding get() = bindingReference!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setStyle(DialogFragment.STYLE_NORMAL, R.style.MyTheme_Dialog);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentSkyDialogOptionsBinding.inflate(inflater, container, false)

        binding.buttonOK.setOnClickListener { onOK() }
        binding.checkBoxDisplaySymbols.setOnCheckedChangeListener { v, isChecked -> options.displaySymbols = isChecked }
        binding.checkBoxDisplayMagnitude.setOnCheckedChangeListener { v, isChecked -> options.displayMagnitude = isChecked }
        binding.checkBoxDisplayConstellations.setOnCheckedChangeListener { v, isChecked -> options.displayConstellations = isChecked }
        binding.checkBoxDisplayConstellationNames.setOnCheckedChangeListener { v, isChecked -> options.displayConstellationNames = isChecked }
        binding.checkBoxDisplayPlanetNames.setOnCheckedChangeListener { v, isChecked -> options.displayPlanetNames = isChecked }
        binding.checkBoxDisplayStarNames.setOnCheckedChangeListener { v, isChecked -> options.displayStarNames = isChecked }
        binding.checkBoxDisplayTargets.setOnCheckedChangeListener { v, isChecked -> options.displayTargets = isChecked }
        binding.checkBoxDisplaySatellites.setOnCheckedChangeListener { v, isChecked -> options.displaySatellites = isChecked }
        binding.seekBarBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                options.magnitude = SkyViewOptions.percentToBrightness(progress)
                binding.textViewBrightness.text = Formatter.df2.format(options.magnitude)
            }
            override fun onStartTrackingTouch(arg0: SeekBar) {
                // Nothing
            }
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Nothing
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateOptions()
    }

    private fun updateOptions() {
        binding.checkBoxDisplaySymbols.isChecked = options.displaySymbols
        binding.checkBoxDisplayMagnitude.isChecked = options.displayMagnitude
        binding.checkBoxDisplayConstellations.isChecked = options.displayConstellations
        binding.checkBoxDisplayConstellationNames.isChecked = options.displayConstellationNames
        binding.checkBoxDisplayPlanetNames.isChecked = options.displayPlanetNames
        binding.checkBoxDisplayStarNames.isChecked = options.displayStarNames
        binding.checkBoxDisplayTargets.isChecked = options.displayTargets
        binding.checkBoxDisplaySatellites.isChecked = options.displaySatellites
        binding.textViewBrightness.text = Formatter.df2.format(options.magnitude)
        binding.seekBarBrightness.progress = SkyViewOptions.brightnessToPercent(options.magnitude)
    }

    private fun onOK() {
        dismiss()
    }
}

