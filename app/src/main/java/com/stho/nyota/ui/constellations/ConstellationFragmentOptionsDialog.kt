package com.stho.nyota.ui.constellations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.stho.nyota.databinding.FragmentConstellationDialogOptionsBinding
import com.stho.nyota.databinding.FragmentSkyDialogOptionsBinding
import com.stho.nyota.ui.sky.IConstellationViewOptions
import com.stho.nyota.ui.sky.ISkyViewOptions


class ConstellationFragmentOptionsDialog(val options: IConstellationViewOptions): DialogFragment() {

    private var bindingReference: FragmentConstellationDialogOptionsBinding? = null
    private val binding: FragmentConstellationDialogOptionsBinding get() = bindingReference!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentConstellationDialogOptionsBinding.inflate(inflater, container, false)

        binding.buttonOK.setOnClickListener { onOK() }
        binding.checkBoxDisplaySymbols.setOnCheckedChangeListener { _, isChecked -> options.displaySymbols = isChecked }
        binding.checkBoxDisplayStarNames.setOnCheckedChangeListener { _, isChecked -> options.displayStarNames = isChecked }
        binding.checkBoxDisplayConstellations.setOnCheckedChangeListener { _, isChecked -> options.displayConstellations = isChecked }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateOptions()
    }

    private fun updateOptions() {
        binding.checkBoxDisplaySymbols.isChecked = options.displaySymbols
        binding.checkBoxDisplayStarNames.isChecked = options.displayStarNames
        binding.checkBoxDisplayConstellations.isChecked = options.displayConstellations
    }

    private fun onOK() {
        dismiss()
    }
}

