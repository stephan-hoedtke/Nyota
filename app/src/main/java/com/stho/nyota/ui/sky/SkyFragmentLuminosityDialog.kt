package com.stho.nyota.ui.sky

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.stho.nyota.SeekBarAdapter
import com.stho.nyota.databinding.FragmentSkyDialogLuminosityBinding
import com.stho.nyota.settings.Settings
import com.stho.nyota.sky.utilities.Formatter


class SkyFragmentLuminosityDialog(private val options: ISkyViewOptions): DialogFragment() {

    private var bindingReference: FragmentSkyDialogLuminosityBinding? = null
    private val binding: FragmentSkyDialogLuminosityBinding get() = bindingReference!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentSkyDialogLuminosityBinding.inflate(inflater, container, false)

        binding.sky.setOptions(options)
        binding.buttonOK.setOnClickListener { onOK() }
        binding.seekBarMagnitudeFilter.setOnSeekBarChangeListener(SeekBarAdapter() { progress ->
            options.magnitude = Settings.percentToBrightness(progress)
            binding.textViewMagnitudeFilter.text = Formatter.df2.format(options.magnitude)
        })
        binding.seekBarRadius.setOnSeekBarChangeListener(SeekBarAdapter() { progress ->
            options.radius = Settings.percentToRadius(progress)
            binding.textViewRadius.text = Formatter.df2.format(options.radius)
        })
        binding.seekBarGamma.setOnSeekBarChangeListener(SeekBarAdapter() { progress ->
            options.gamma = Settings.percentToGamma(progress)
            binding.textViewGamma.text = Formatter.df2.format(options.gamma)
        })
        binding.seekBarLambda.setOnSeekBarChangeListener(SeekBarAdapter() { progress ->
            options.lambda = Settings.percentToLambda(progress)
            binding.textViewLambda.text = Formatter.df2.format(options.lambda)
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        options.versionLD.observe(viewLifecycleOwner, { _ -> binding.sky.touch() })
        updateOptions()
    }

    private fun updateOptions() {
        binding.textViewMagnitudeFilter.text = Formatter.df2.format(options.magnitude)
        binding.seekBarMagnitudeFilter.progress = Settings.brightnessToPercent(options.magnitude)
        binding.textViewRadius.text = Formatter.df2.format(options.radius)
        binding.seekBarRadius.progress = Settings.radiusToPercent(options.radius)
        binding.textViewGamma.text = Formatter.df2.format(options.gamma)
        binding.seekBarGamma.progress = Settings.gammaToPercent(options.gamma)
        binding.textViewLambda.text = Formatter.df2.format(options.lambda)
        binding.seekBarLambda.progress = Settings.lambdaToPercent(options.lambda)
    }

    private fun onOK() {
        dismiss()
    }
}

