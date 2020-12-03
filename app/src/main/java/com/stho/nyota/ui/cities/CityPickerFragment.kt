package com.stho.nyota.ui.cities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.stho.nyota.AbstractFragment
import com.stho.nyota.R
import com.stho.nyota.RecyclerViewItemDivider
import com.stho.nyota.createViewModel
import com.stho.nyota.databinding.FragmentCityPickerBinding
import com.stho.nyota.sky.utilities.Cities
import com.stho.nyota.sky.utilities.City


class CityPickerFragment : AbstractFragment() {
    private lateinit var viewModel: CityPickerViewModel
    private lateinit var adapter: CityPickerRecyclerViewAdapter
    private var bindingReference: FragmentCityPickerBinding? = null
    private val binding: FragmentCityPickerBinding get() = bindingReference!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel(CityPickerViewModel::class.java)
        setHasOptionsMenu(true)
    }

    override val abstractViewModel: IAbstractViewModel
        get() = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentCityPickerBinding.inflate(inflater, container, false)

        adapter = CityPickerRecyclerViewAdapter(this)
        adapter.onSelectionChanged = { city -> onSelectionChanged(city) }
        adapter.onEdit = { city -> onEdit(city) }
        adapter.onDelete = { position, city -> onDelete(position, city) }
        adapter.select(viewModel.moment.city)

        binding.list.attachItemTouchHelper(SwipeToDelete(adapter))
        binding.list.layoutManager = LinearLayoutManager(context)
        binding.list.adapter = adapter
        binding.list.addItemDecoration(RecyclerViewItemDivider(requireContext()))
        binding.buttonDone.setOnClickListener { onDone() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.citiesLD.observe(viewLifecycleOwner, { cities -> updateCities(cities) })
        viewModel.selectedCityLC.observe(viewLifecycleOwner, { city -> updateSelectedCity(city) })
        updateActionBar(getString(R.string.title_choose_city), "")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()
    }

    private fun updateCities(cities: Cities) =
        adapter.updateCities(cities)

    private fun updateSelectedCity(city: City) {
        adapter.selectedCity = city
    }

    private fun onSelectionChanged(city: City) {
        viewModel.repository.setCity(city)
    }

    private fun onEdit(city: City) =
        findNavController().navigate(CityPickerFragmentDirections.actionNavCityPickerToNavCity(city.name))

    private fun onDelete(position: Int, city: City) {
        viewModel.repository.deleteCity(requireContext(), city)
        adapter.updateDelete(position)
        showUndoSnackBar(position, city)
    }

    private fun onDone() {
        findNavController().popBackStack()
    }

    private fun showUndoSnackBar(position: Int, city: City) {
        val container: View = requireActivity().findViewById<View>(R.id.drawer_layout)
        val snackbar = Snackbar.make(container, "City was deleted", Snackbar.LENGTH_LONG)
        snackbar.setAction("Undo") {
            undoDelete(position, city)
        }
        snackbar.setActionTextColor(getColor(R.color.colorPrimaryText))
        snackbar.setBackgroundTint(getColor(R.color.colorSignalBackground))
        snackbar.setTextColor(getColor(R.color.colorSecondaryText))
        snackbar.show()
    }

    private fun undoDelete(position: Int, city: City) {
        viewModel.repository.undoDeleteCity(requireContext(), position, city)
        adapter.updateUndoDelete(position)
    }
}

