package com.stho.nyota.ui.cities

import android.os.Bundle
import android.util.Log
import android.view.*
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

        adapter = CityPickerRecyclerViewAdapter(this, binding.list)
        adapter.onSelectionChanged = { city -> onSelectionChanged(city) }
        adapter.onEdit = { city -> onEdit(city) }
        adapter.onDelete = { position, city -> onDelete(position, city) }

        binding.list.attachItemTouchHelper(SwipeToDelete(adapter))
        binding.list.layoutManager = LinearLayoutManager(context)
        binding.list.adapter = adapter
        binding.list.addItemDecoration(RecyclerViewItemDivider(requireContext()))
        binding.buttonDone.setOnClickListener { onDone() }
        binding.buttonNew.setOnClickListener { onNew() }

        registerForContextMenu(binding.buttonNew)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.repository.updateCityDistances()
        viewModel.citiesLD.observe(viewLifecycleOwner, { cities -> observeCities(cities) })
        viewModel.selectedCityLD.observe(viewLifecycleOwner, { city -> observeSelectedCity(city) })
        updateActionBar(getString(R.string.title_choose_city))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.context_menu_city_picker, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        when (v.id) {
            R.id.buttonNew -> requireActivity().menuInflater.inflate(R.menu.context_menu_city_picker, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_new -> onNew()
            R.id.action_default -> onDefault()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_new -> onNew()
            R.id.action_default -> onDefault()
        }
        return super.onContextItemSelected(item)
    }

    private fun observeCities(cities: Cities) =
        adapter.updateCities(cities)

    private fun observeSelectedCity(city: City) {
        Log.d("CITY", "Observe city ${city.nameEx}")
        adapter.selectedCity = city
    }

    private fun onSelectionChanged(city: City) {
        Log.d("CITY", "Selection changed city ${city.nameEx}")
        viewModel.repository.setCity(city)
    }

    private fun onEdit(city: City) =
        findNavController().navigate(CityPickerFragmentDirections.actionNavCityPickerToNavCity(city.name))

    private fun onDelete(position: Int, city: City) {
        viewModel.deleteCity(city)
        adapter.updateDelete(position)
        showUndoSnackBar(position, city)
    }

    private fun onNew() {
        findNavController().navigate(CityPickerFragmentDirections.actionNavCityPickerToNavCity(City.NEW_CITY))
    }

    private fun onDefault() {
        viewModel.createDefaultCities()
    }

    private fun onDone() {
        findNavController().popBackStack()
    }

    private fun showUndoSnackBar(position: Int, city: City) {
        val message = getString(R.string.message_city_was_deleted)
        view?.also {
            Snackbar.make(it, message, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.label_undo)) { undoDelete(position, city) }
                .setActionTextColor(getColor(R.color.colorPrimaryText))
                .setBackgroundTint(getColor(R.color.colorSignalBackground))
                .setTextColor(getColor(R.color.colorSecondaryText))
                .show()
        }
    }

    private fun undoDelete(position: Int, city: City) {
        viewModel.undoDeleteCity(position, city)
        adapter.updateUndoDelete(position)
    }
}

