package com.stho.nyota.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.stho.nyota.databinding.FragmentHomeDialogOptionsBinding


class HomeFragmentOptionsDialog(private var viewModel: HomeViewModel): DialogFragment() {

    private var bindingReference: FragmentHomeDialogOptionsBinding? = null
    private val binding: FragmentHomeDialogOptionsBinding get() = bindingReference!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentHomeDialogOptionsBinding.inflate(inflater, container, false)

        binding.buttonOK.setOnClickListener { onOK() }

        // What is the way to display the dialog in 80% of the phone width?
        //
        // a)
        // set fixed width in layout xml file (done here):
        // android:layout_width="@dimen/dialog_with"
        //
        // b)
        // <style name="Theme.Holo.Dialog.MinWidth">
        //<item name="android:windowMinWidthMajor">70%</item>
        //
        // c)
        // override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // dialog?.window?.attributes?.width = ViewGroup.LayoutParams.MATCH_PARENT
        // dialog?.window?.attributes?.height = ViewGroup.LayoutParams.MATCH_PARENT
        // ...

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.optionsLD.observe(viewLifecycleOwner, { options -> updateOptions(options) })
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun updateOptions(options: HomeViewModel.Options) {
        binding.checkBoxShowStars.isChecked = options.showStars
        binding.checkBoxShowPlanets.isChecked = options.showPlanets
        binding.checkBoxShowSatellites.isChecked = options.showSatellites
        binding.checkBoxShowTargets.isChecked = options.showTargets
        binding.checkBoxShowInvisibleElements.isChecked = options.showInvisibleElements
    }

    private fun onOK() {
        viewModel.updateOptions(
            showStars = binding.checkBoxShowStars.isChecked,
            showPlanets = binding.checkBoxShowPlanets.isChecked,
            showSatellites = binding.checkBoxShowSatellites.isChecked,
            showTargets = binding.checkBoxShowTargets.isChecked,
            showInvisibleElements = binding.checkBoxShowInvisibleElements.isChecked
        )
        dismiss()
    }
}

