package com.stho.nyota.ui.info

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stho.nyota.AbstractFragment
import com.stho.nyota.R
import com.stho.nyota.createViewModel
import com.stho.nyota.databinding.FragmentInfoBinding
import com.stho.nyota.databinding.FragmentMomentLocationBinding
import com.stho.nyota.sky.universe.Universe
import com.stho.nyota.sky.utilities.*
import com.stho.nyota.ui.moment.MomentViewModel
import java.text.Normalizer

class InfoFragment : AbstractFragment() {

    private lateinit var viewModel: InfoViewModel
    private var bindingReference: FragmentInfoBinding? = null
    private val binding: FragmentInfoBinding get() = bindingReference!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel(InfoViewModel::class.java)
        setHasOptionsMenu(true)
    }

    override val abstractViewModel: IAbstractViewModel
        get() = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.repository.currentAutomaticTimeLD.observe(viewLifecycleOwner, { utc -> updateAutomaticTime(utc) })
        viewModel.repository.currentAutomaticLocationLD.observe(viewLifecycleOwner, { location -> updateAutomaticLocation(location) })
        viewModel.repository.currentOrientationLD.observe(viewLifecycleOwner, { orientation -> updateOrientation(orientation) })

        bindElements(viewModel.repository.universe)
        bindStatistics(viewModel.repository.universe)
    }

    private fun bindElements(universe: Universe) {
        binding.textViewConstellations.text = "Constellations: ${universe.constellations.size}"
        binding.textViewStars.text = "Stars: ${universe.stars.size}"
        binding.textViewSatellites.text = "Satellites: ${universe.satellites.size}"
    }

    private fun bindStatistics(universe: Universe) {
        binding.textViewUpdateTime.text = "Update time: ${Formatter.df3.format(universe.timeInSeconds)} s"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
    }

    private fun updateAutomaticTime(utc: UTC) {
        binding.textViewCurrentTime.text = Formatter.toString(utc, viewModel.moment.timeZone, Formatter.TimeFormat.DATETIME_SEC_TIMEZONE)
    }

    private fun updateAutomaticLocation(location: Location) {
        binding.textViewCurrentLocation.text = location.toString()
    }

    private fun updateOrientation(orientation: Orientation) {
        binding.textViewAzimuth.text = Formatter.df0.format(orientation.pointerAzimuth)
        binding.textViewPitch.text = Formatter.df0.format(orientation.pointerAltitude)
        binding.textViewDirection.text = Angle.toString(orientation.centerAzimuth, orientation.centerAltitude, Angle.AngleType.ORIENTATION)
        binding.textViewRoll.text = Formatter.df0.format(orientation.roll)
    }
}
