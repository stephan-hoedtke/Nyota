package com.stho.nyota.ui.sky

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.stho.nyota.databinding.FragmentSkyDialogChooseProjectionBinding
import com.stho.nyota.sky.utilities.projections.Projection


class SkyFragmentChooseProjectionDialog(val options: ISkyViewOptions): DialogFragment() {

    private var bindingReference: FragmentSkyDialogChooseProjectionBinding? = null
    private val binding: FragmentSkyDialogChooseProjectionBinding get() = bindingReference!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setStyle(DialogFragment.STYLE_NORMAL, R.style.MyTheme_Dialog);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentSkyDialogChooseProjectionBinding.inflate(inflater, container, false)

        binding.buttonOK.setOnClickListener { onOK() }
        binding.radioButtonSphere.setOnCheckedChangeListener { v, isChecked -> if (isChecked) options.sphereProjection = Projection.SPHERE }
        binding.radioButtonCentral.setOnCheckedChangeListener { v, isChecked -> if (isChecked) options.sphereProjection = Projection.GNOMONIC }
        binding.radioButtonMercator.setOnCheckedChangeListener { v, isChecked -> if (isChecked) options.sphereProjection = Projection.MERCATOR }
        binding.radioButtonArchimedes.setOnCheckedChangeListener { v, isChecked -> if (isChecked) options.sphereProjection = Projection.ARCHIMEDES }
        binding.radioButtonStereographic.setOnCheckedChangeListener { v, isChecked -> if (isChecked) options.sphereProjection = Projection.STEREOGRAPHIC  }
        binding.checkBoxDisplayGrid.setOnCheckedChangeListener { v, isChecked -> options.displayGrid = isChecked }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateOptions()
    }

    private fun updateOptions() {
        binding.radioButtonSphere.isChecked = options.sphereProjection == Projection.SPHERE
        binding.radioButtonCentral.isChecked = options.sphereProjection == Projection.GNOMONIC
        binding.radioButtonMercator.isChecked = options.sphereProjection == Projection.MERCATOR
        binding.radioButtonArchimedes.isChecked = options.sphereProjection == Projection.ARCHIMEDES
        binding.radioButtonStereographic.isChecked = options.sphereProjection == Projection.STEREOGRAPHIC
        binding.checkBoxDisplayGrid.isChecked = options.displayGrid
    }

    private fun onOK() {
        dismiss()
    }
}

