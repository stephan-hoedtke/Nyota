package com.stho.nyota.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.stho.nyota.R
import com.stho.nyota.createViewModel
import com.stho.nyota.sky.utilities.City
import kotlinx.android.synthetic.main.fragment_city.view.*
import kotlinx.android.synthetic.main.fragment_home_dialog_options.*
import kotlinx.android.synthetic.main.fragment_home_dialog_options.view.*

class HomeFragmentOptionsDialog: DialogFragment() {

    // HomeFragment and HomeFragmentOptionsDialog share the view model instance, which is created with the activity as owner.
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel(HomeViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_home_dialog_options, container, false)

        root.buttonOK.setOnClickListener { onOK() }

        viewModel.optionsLD.observe(viewLifecycleOwner, Observer { options -> updateOptions(options) })
        return root
    }

    private fun updateOptions(options: HomeViewModel.Options) {
        view?.also {
            it.checkBoxShowStars.isChecked = options.showStars
            it.checkBoxShowPlanets.isChecked = options.showPlanets
            it.checkBoxShowSatellites.isChecked = options.showSatellites
            it.checkBoxShowTargets.isChecked = options.showTargets
            it.checkBoxShowInvisibleElements.isChecked = options.showInvisibleElements
        }
    }

    private fun onOK() {
        view?.also {
            viewModel.updateOptions(
                showStars = it.checkBoxShowStars.isChecked,
                showPlanets = it.checkBoxShowPlanets.isChecked,
                showSatellites = it.checkBoxShowSatellites.isChecked,
                showTargets = it.checkBoxShowTargets.isChecked,
                showInvisibleElements = it.checkBoxShowInvisibleElements.isChecked
            )
        }
        dismiss()
    }
}

