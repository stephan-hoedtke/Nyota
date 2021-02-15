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
        binding.checkBoxDisplaySymbols.setOnCheckedChangeListener { v, isChecked -> options.displaySymbols = isChecked }
        binding.checkBoxDisplayConstellations.setOnCheckedChangeListener { v, isChecked -> options.displayConstellations = isChecked }
        binding.checkBoxDisplayConstellationNames.setOnCheckedChangeListener { v, isChecked -> options.displayConstellationNames = isChecked }
        binding.checkBoxDisplayPlanetNames.setOnCheckedChangeListener { v, isChecked -> options.displayPlanetNames = isChecked }
        binding.checkBoxDisplayStarNames.setOnCheckedChangeListener { v, isChecked -> options.displayStarNames = isChecked }
        binding.checkBoxDisplayEcliptic.setOnCheckedChangeListener { v, isChecked -> options.displayEcliptic = isChecked }
        binding.checkBoxDisplayTargets.setOnCheckedChangeListener { v, isChecked -> options.displayTargets = isChecked }
        binding.checkBoxDisplaySatellites.setOnCheckedChangeListener { v, isChecked -> options.displaySatellites = isChecked }

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
        binding.checkBoxDisplayTargets.isChecked = options.displayTargets
        binding.checkBoxDisplaySatellites.isChecked = options.displaySatellites
    }

    private fun onOK() {
        dismiss()
    }
}

