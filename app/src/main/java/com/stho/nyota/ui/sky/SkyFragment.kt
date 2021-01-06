package com.stho.nyota.ui.sky

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.SeekBar
import androidx.fragment.app.FragmentManager
import com.stho.nyota.AbstractFragment
import com.stho.nyota.AbstractViewModel
import com.stho.nyota.R
import com.stho.nyota.databinding.FragmentSkyBinding
import com.stho.nyota.sky.utilities.Moment


// TODO: implement options in Sky view: show names, letters, lines, change colors, ...
// TODO: implement styles
// TODO: change updateMoment(...) into onObserveMoment(...) for all observers

class SkyFragment : AbstractFragment() {

    private lateinit var viewModel: SkyViewModel
    private var bindingReference: FragmentSkyBinding? = null
    private val binding: FragmentSkyBinding get() = bindingReference!!

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val elementName = getElementNameFromArguments()
        viewModel = createSkyViewModel(elementName)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentSkyBinding.inflate(inflater, container, false)

        binding.sky.setUniverse(viewModel.universe)
        binding.sky.setReferenceElement(viewModel.element)
        binding.sky.options.loadSettings(viewModel.settings)
        binding.buttonZoomIn.setOnClickListener { onZoomIn() }
        binding.buttonZoomOut.setOnClickListener { onZoomOut() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.universeLD.observe(viewLifecycleOwner, { universe -> updateMoment(universe.moment) })
        viewModel.settingsLD.observe(viewLifecycleOwner, { settings -> updateSettings(settings) })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sky, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_view_options -> {
                displaySkyFragmentOptionsDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateMoment(moment: Moment) {
        bind(moment)
    }

    private fun bind(moment: Moment) {
        binding.timeOverlay.currentTime.text = toLocalTimeString(moment)
        binding.sky.notifyDataSetChanged()
        updateActionBar(R.string.label_nyota, toLocalDateString(moment))
    }

    private fun updateSettings(settings: ISkyViewSettings) {
        binding.sky.options.loadSettings(settings)
    }

    private fun onZoomIn() =
        binding.sky.options.applyScale(1.1)

    private fun onZoomOut() =
        binding.sky.options.applyScale(1 / 1.1)

    private fun getElementNameFromArguments(): String? =
        arguments?.getString("ELEMENT")

    private fun displaySkyFragmentOptionsDialog() {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val tag = "fragment_sky_options_dialog"
        SkyFragmentOptionsDialog(binding.sky.options).show(fragmentManager, tag)
    }
}


