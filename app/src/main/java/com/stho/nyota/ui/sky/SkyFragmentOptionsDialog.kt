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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentSkyDialogOptionsBinding.inflate(inflater, container, false)

        binding.buttonOK.setOnClickListener { onOK() }
        binding.checkBoxDisplaySymbols.setOnCheckedChangeListener { _, isChecked -> options.displaySymbols = isChecked }
        binding.checkBoxDisplayConstellations.setOnCheckedChangeListener { _, isChecked -> options.displayConstellations = isChecked }
        binding.checkBoxDisplayConstellationNames.setOnCheckedChangeListener { _, isChecked -> options.displayConstellationNames = isChecked }
        binding.checkBoxDisplayPlanetNames.setOnCheckedChangeListener { _, isChecked -> options.displayPlanetNames = isChecked }
        binding.checkBoxDisplayStarNames.setOnCheckedChangeListener { _, isChecked -> options.displayStarNames = isChecked }
        binding.checkBoxDisplayEcliptic.setOnCheckedChangeListener { _, isChecked -> options.displayEcliptic = isChecked }
        binding.checkBoxDisplayHints.setOnCheckedChangeListener { _, isChecked -> options.displayHints = isChecked }
        binding.checkBoxDisplayTargets.setOnCheckedChangeListener { _, isChecked -> options.displayTargets = isChecked }
        binding.checkBoxDisplaySatellites.setOnCheckedChangeListener { _, isChecked -> options.displaySatellites = isChecked }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateOptions()
    }

    private fun updateOptions() {
        binding.checkBoxDisplaySymbols.isChecked = options.displaySymbols
        binding.checkBoxDisplayConstellations.isChecked = options.displayConstellations
        binding.checkBoxDisplayConstellationNames.isChecked = options.displayConstellationNames
        binding.checkBoxDisplayPlanetNames.isChecked = options.displayPlanetNames
        binding.checkBoxDisplayStarNames.isChecked = options.displayStarNames
        binding.checkBoxDisplayEcliptic.isChecked = options.displayEcliptic
        binding.checkBoxDisplayHints.isChecked = options.displayHints
        binding.checkBoxDisplayTargets.isChecked = options.displayTargets
        binding.checkBoxDisplaySatellites.isChecked = options.displaySatellites
    }

    private fun onOK() {
        dismiss()
    }
}

