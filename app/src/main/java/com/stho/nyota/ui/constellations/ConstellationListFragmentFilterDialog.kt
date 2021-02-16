package com.stho.nyota.ui.constellations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.stho.nyota.databinding.FragmentConstellationDialogOptionsBinding
import com.stho.nyota.databinding.FragmentConstellationListDialogFilterBinding
import com.stho.nyota.databinding.FragmentSkyDialogOptionsBinding
import com.stho.nyota.sky.universe.Constellations
import com.stho.nyota.ui.sky.ISkyViewOptions
import java.util.logging.Filter


class ConstellationListFragmentFilterDialog(private val viewModel: ConstellationListViewModel): DialogFragment() {

    private var bindingReference: FragmentConstellationListDialogFilterBinding? = null
    private val binding: FragmentConstellationListDialogFilterBinding get() = bindingReference!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentConstellationListDialogFilterBinding.inflate(inflater, container, false)

        binding.buttonOK.setOnClickListener { onOK() }
        binding.radioButtonPtolemaus.setOnCheckedChangeListener { v, isChecked -> if (isChecked) bindRadioButtons(Constellations.Filter.Ptolemaeus) }
        binding.radioButtonZodiac.setOnCheckedChangeListener { v, isChecked -> if (isChecked) bindRadioButtons(Constellations.Filter.Zodiac) }
        binding.radioButtonIAU.setOnCheckedChangeListener { v, isChecked -> if (isChecked) bindRadioButtons(Constellations.Filter.IAU) }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.optionsLD.observe(viewLifecycleOwner, { options -> onObserveOptions(options) })
    }

    private fun onObserveOptions(options: ConstellationListViewModel.Options) {
        bindRadioButtons(options.filter)
    }

    private fun bindRadioButtons(filter: Constellations.Filter) {
        binding.radioButtonPtolemaus.isChecked = (filter == Constellations.Filter.Ptolemaeus)
        binding.radioButtonZodiac.isChecked = (filter == Constellations.Filter.Zodiac)
        binding.radioButtonIAU.isChecked = (filter == Constellations.Filter.IAU)
    }

    private val filterFromRadioButtons: Constellations.Filter
        get() =
            when {
                binding.radioButtonIAU.isChecked -> Constellations.Filter.IAU
                binding.radioButtonZodiac.isChecked -> Constellations.Filter.Zodiac
                binding.radioButtonPtolemaus.isChecked -> Constellations.Filter.Ptolemaeus
                else -> Constellations.Filter.IAU
            }

    private fun onOK() {
        viewModel.updateOptions(filter = filterFromRadioButtons)
        dismiss()
    }
}

