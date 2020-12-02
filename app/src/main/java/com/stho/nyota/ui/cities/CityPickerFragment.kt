package com.stho.nyota.ui.cities

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.stho.nyota.AbstractFragment
import com.stho.nyota.R
import com.stho.nyota.RecyclerViewItemDivider
import com.stho.nyota.createViewModel
import com.stho.nyota.sky.utilities.Cities
import com.stho.nyota.sky.utilities.City
import kotlinx.android.synthetic.main.fragment_city_picker.view.*


class CityPickerFragment : AbstractFragment() {
    lateinit var viewModel: CityPickerViewModel
    lateinit var adapter: CityPickerRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel(CityPickerViewModel::class.java)
        setHasOptionsMenu(true)
    }

    override val abstractViewModel: IAbstractViewModel
        get() = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_city_picker, container, false)

        adapter = CityPickerRecyclerViewAdapter(this)
        adapter.onSelectionChanged = { city -> onSelectionChanged(city) }
        adapter.onEdit = { city -> onEdit(city) }
        adapter.onDelete = { position, city -> onDelete(position, city) }
        adapter.select(viewModel.moment.city)

        root.list.attachItemTouchHelper(SwipeToDelete(adapter))
        root.list.layoutManager = LinearLayoutManager(context)
        root.list.adapter = adapter
        root.list.addItemDecoration(RecyclerViewItemDivider(requireContext()))
        root.buttonOK.setOnClickListener { onButtonOK() }
        root.buttonDefault.setOnClickListener { onButtonDefault() }

        viewModel.repository.citiesLD.observe(viewLifecycleOwner, Observer { cities -> updateCities(cities) })
        updateActionBar(getString(R.string.title_choose_city), "")
        return root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()
    }

    private fun updateCities(cities: Cities) =
        adapter.updateCities(cities)

    private fun onSelectionChanged(city: City) {
        // no need to do anything...
    }

    private fun onEdit(city: City) =
        findNavController().navigate(CityPickerFragmentDirections.actionNavCityPickerToNavCity(city.name))

    private fun onDelete(position: Int, city: City) {
        viewModel.repository.deleteCity(requireContext(), city)
        adapter.updateDelete(position)
        showUndoSnackBar(position, city)
    }

    private fun onButtonOK() {
        viewModel.repository.setCity(adapter.getSelectedCity())
        findNavController().popBackStack()
    }

    private fun onButtonDefault() {
        // TODO: implement or remove
    }

    private fun showUndoSnackBar(position: Int, city: City) {
        val container: View = requireActivity().findViewById<View>(R.id.drawer_layout)
        val snackbar = Snackbar.make(container, "City was deleted", Snackbar.LENGTH_LONG)
        snackbar.setAction("Undo") {
            undoDelete(position, city)
        }
        snackbar.setActionTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimaryText
            )
        )
        snackbar.setBackgroundTint(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorSignalBackground
            )
        )
        snackbar.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorSecondaryText))
        snackbar.show()
    }

    private fun undoDelete(position: Int, city: City) {
        viewModel.repository.undoDeleteCity(requireContext(), position, city)
        adapter.updateUndoDelete(position)
    }
}

