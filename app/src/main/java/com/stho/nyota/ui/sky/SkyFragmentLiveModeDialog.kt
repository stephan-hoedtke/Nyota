package com.stho.nyota.ui.sky

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.stho.nyota.databinding.FragmentSkyDialogLiveModeBinding
import com.stho.nyota.settings.Settings
import com.stho.nyota.sky.utilities.LiveMode


class SkyFragmentLiveModeDialog(val settings: Settings): DialogFragment() {

    private var bindingReference: FragmentSkyDialogLiveModeBinding? = null
    private val binding: FragmentSkyDialogLiveModeBinding get() = bindingReference!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setStyle(DialogFragment.STYLE_NORMAL, R.style.MyTheme_Dialog);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentSkyDialogLiveModeBinding.inflate(inflater, container, false)

        binding.buttonOK.setOnClickListener { onOK() }
        binding.radioButtonOff.setOnCheckedChangeListener { _, isChecked -> if (isChecked) settings.liveMode = LiveMode.Off }
        binding.radioButtonHints.setOnCheckedChangeListener { _, isChecked -> if (isChecked) settings.liveMode = LiveMode.Hints }
        binding.radioButtonMoveCenter.setOnCheckedChangeListener { _, isChecked -> if (isChecked) settings.liveMode = LiveMode.MoveCenter }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateOptions()
    }

    private fun updateOptions() {
        binding.radioButtonOff.isChecked = settings.liveMode == LiveMode.Off
        binding.radioButtonHints.isChecked = settings.liveMode == LiveMode.Hints
        binding.radioButtonMoveCenter.isChecked = settings.liveMode == LiveMode.MoveCenter
    }

    private fun onOK() {
        dismiss()
    }
}

