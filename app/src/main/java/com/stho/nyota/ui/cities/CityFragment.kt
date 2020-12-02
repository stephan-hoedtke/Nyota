package com.stho.nyota.ui.cities

import android.content.Context
import android.content.Intent
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.stho.nyota.AbstractFragment
import com.stho.nyota.R
import com.stho.nyota.sky.utilities.City
import com.stho.nyota.sky.utilities.Formatter
import com.stho.nyota.sky.utilities.Moment
import kotlinx.android.synthetic.main.fragment_city.view.*


class CityFragment : AbstractFragment() {

    private lateinit var viewModel: CityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cityName = getCityNameFromArguments()
        viewModel = createCityViewModel(cityName)
        setHasOptionsMenu(true)
    }

    override val abstractViewModel: IAbstractViewModel
        get() = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_city, container, false)
        root.buttonSave.setOnClickListener { onSave() }
        root.buttonEarthView.setOnClickListener { onEarthView() }
        root.buttonFinderView.setOnClickListener { onFinderView() }
        root.buttonReset.setOnClickListener { onReset() }
        viewModel.repository.currentAutomaticMomentLD.observe(viewLifecycleOwner, { moment -> updateAutomaticMoment(moment) })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateCity(viewModel.city)
        updateActionBar(getString(R.string.title_city), "")
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()
    }

    private fun updateCity(city: City) {
        view?.also {
            it.checkBoxAutomatic?.isChecked = city.isAutomatic
            it.editName?.setText(city.name)
            it.editLatitude.setText(Formatter.toString(city.latitude))
            it.editLongitude.setText(Formatter.toString(city.longitude))
            it.editAltitude.setText(Formatter.toString(city.altitude))
            it.image?.setImageResource(city.imageId)
            it.copyright?.text = city.copyright.text
            it.copyright?.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(city.copyright.link))
                startActivity(browserIntent)
            }
        }
    }

    private fun updateAutomaticMoment(moment: Moment) {
        view?.also {
            if (it.checkBoxAutomatic.isChecked) {
                it.editLatitude.setText(Formatter.toString(moment.city.latitude))
                it.editLongitude.setText(Formatter.toString(moment.city.longitude))
                it.editAltitude.setText(Formatter.toString(moment.city.altitude))
            }
        }
    }

    private fun onSave() {
        val city = viewModel.city
        view?.also {
            try {
                city.name = it.editName.text.toString()
                city.location = com.stho.nyota.sky.utilities.Location(
                    it.editLatitude.text.toString().toDouble(),
                    it.editLongitude.text.toString().toDouble(),
                    it.editAltitude.text.toString().toDouble()
                )
                viewModel.repository.updateCity(requireContext(), city)
                findNavController().popBackStack()
            }
            catch (ex: Exception) {
                // IGNORE
            }
        }
    }

    private fun getCityNameFromArguments(): String? =
        arguments?.getString("CITY")

    private fun onReset() {
        updateCity(viewModel.city)
    }

    private fun onFinderView() {
        // TODO
    }

    private fun onEarthView() {
        // TODO
    }
}