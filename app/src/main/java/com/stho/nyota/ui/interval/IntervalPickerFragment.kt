package com.stho.nyota.ui.interval

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.stho.nyota.*
import com.stho.nyota.databinding.FragmentIntervalPickerBinding

// see here:
// https://www.geeksforgeeks.org/dynamic-radiobutton-in-kotlin/
// with recyclerView and selector
// https://stackoverflow.com/questions/39138315/how-to-highlight-selected-item-in-recyclerview
//

class IntervalPickerFragment : AbstractFragment() {

    private lateinit var viewModel: IntervalPickerViewModel
    private lateinit var adapter: IntervalPickerRecyclerViewAdapter
    private var bindingReference: FragmentIntervalPickerBinding? = null
    private val binding: FragmentIntervalPickerBinding get() = bindingReference!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel(IntervalPickerViewModel::class.java)
        setHasOptionsMenu(true)
    }

    override val abstractViewModel: IAbstractViewModel
        get() = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentIntervalPickerBinding.inflate(inflater, container, false)

        adapter = IntervalPickerRecyclerViewAdapter()
        adapter.onSelectionChanged = { interval -> onSelectionChanged(interval) }
        adapter.select(viewModel.interval)

        binding.list.layoutManager = LinearLayoutManager(requireContext())
        binding.list.adapter = adapter
        binding.list.addItemDecoration(RecyclerViewItemDivider(requireContext()))
        binding.buttonOK.setOnClickListener { onButtonOK() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateActionBar(getString(R.string.title_choose_interval), "")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()
    }

    private fun onSelectionChanged(interval: Interval) {
        // no need to do anything...
    }

    private fun onButtonOK() {
        val selectedInterval = adapter.getSelectedInterval()
        viewModel.setInterval(selectedInterval)
        findNavController().popBackStack()
    }
}

