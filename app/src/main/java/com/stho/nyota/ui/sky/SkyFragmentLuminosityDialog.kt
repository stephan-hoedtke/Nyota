package com.stho.nyota.ui.sky

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.stho.nyota.SeekBarAdapter
import com.stho.nyota.databinding.FragmentSkyDialogLuminosityBinding
import com.stho.nyota.sky.utilities.Formatter


class SkyFragmentLuminosityDialog(private val options: SkyViewOptions): DialogFragment() {

    private var bindingReference: FragmentSkyDialogLuminosityBinding? = null
    private val binding: FragmentSkyDialogLuminosityBinding get() = bindingReference!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentSkyDialogLuminosityBinding.inflate(inflater, container, false)

        binding.sky.setOptions(options)
        binding.buttonOK.setOnClickListener { onOK() }
        binding.seekBarMagnitudeFilter.setOnSeekBarChangeListener(SeekBarAdapter() { progress ->
            options.magnitude = SkyViewOptions.percentToBrightness(progress)
            binding.textViewMagnitudeFilter.text = Formatter.df2.format(options.magnitude)
        })
        binding.seekBarRadius.setOnSeekBarChangeListener(SeekBarAdapter() { progress ->
            options.radius = SkyViewOptions.percentToRadius(progress)
            binding.textViewRadius.text = Formatter.df2.format(options.radius)
        })
        binding.seekBarGamma.setOnSeekBarChangeListener(SeekBarAdapter() { progress ->
            options.gamma = SkyViewOptions.percentToGamma(progress)
            binding.textViewGamma.text = Formatter.df2.format(options.gamma)
        })
        binding.seekBarAlpha.setOnSeekBarChangeListener(SeekBarAdapter() { progress ->
            options.alpha = SkyViewOptions.percentToAlpha(progress)
            binding.textViewAlpha.text = Formatter.df2.format(options.alpha)
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        options.touchLD.observe(viewLifecycleOwner, { _ -> binding.sky.touch() })
        updateOptions()
    }

    private fun updateOptions() {
        binding.textViewMagnitudeFilter.text = Formatter.df2.format(options.magnitude)
        binding.seekBarMagnitudeFilter.progress = SkyViewOptions.brightnessToPercent(options.magnitude)
        binding.textViewRadius.text = Formatter.df2.format(options.radius)
        binding.seekBarRadius.progress = SkyViewOptions.radiusToPercent(options.radius)
        binding.textViewGamma.text = Formatter.df2.format(options.gamma)
        binding.seekBarGamma.progress = SkyViewOptions.gammaToPercent(options.gamma)
        binding.textViewAlpha.text = Formatter.df2.format(options.alpha)
        binding.seekBarAlpha.progress = SkyViewOptions.alphaToPercent(options.alpha)
    }

    private fun onOK() {
        dismiss()
    }
}

