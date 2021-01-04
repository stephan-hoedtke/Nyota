package com.stho.nyota.ui.sky

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.stho.nyota.createViewModel
import com.stho.nyota.databinding.FragmentSkyDialogOptionsBinding


class SkyFragmentOptionsDialog: DialogFragment() {

    // SkyFragment and SkyFragmentOptionsDialog share the view model instance, which is created with the activity as owner.
    private lateinit var viewModel: SkyViewModel
    private var bindingReference: FragmentSkyDialogOptionsBinding? = null
    private val binding: FragmentSkyDialogOptionsBinding get() = bindingReference!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // viewModel = createSkyViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentSkyDialogOptionsBinding.inflate(inflater, container, false)

        binding.buttonOK.setOnClickListener { onOK() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // viewModel.optionsLD.observe(viewLifecycleOwner, { options -> updateOptions(options) })
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

//    private fun updateOptions(options: HomeViewModel.Options) {
//        binding.checkBoxShowStars.isChecked = options.showStars
//        binding.checkBoxShowPlanets.isChecked = options.showPlanets
//        binding.checkBoxShowSatellites.isChecked = options.showSatellites
//        binding.checkBoxShowTargets.isChecked = options.showTargets
//        binding.checkBoxShowInvisibleElements.isChecked = options.showInvisibleElements
//    }
//
    private fun onOK() {
//        viewModel.updateOptions(
//            showStars = binding.checkBoxShowStars.isChecked,
//            showPlanets = binding.checkBoxShowPlanets.isChecked,
//            showSatellites = binding.checkBoxShowSatellites.isChecked,
//            showTargets = binding.checkBoxShowTargets.isChecked,
//            showInvisibleElements = binding.checkBoxShowInvisibleElements.isChecked
//        )
        dismiss()
    }
}

