package com.stho.nyota.ui.interval

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
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

        binding.list.layoutManager = LinearLayoutManager(requireContext())
        binding.list.adapter = adapter
        binding.list.addItemDecoration(RecyclerViewItemDivider(requireContext()))
        binding.buttonDone.setOnClickListener { onDone() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.settings.intervalLD.observe(viewLifecycleOwner, { interval -> updateInterval(interval) })
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

    private fun updateInterval(interval: Interval) {
        adapter.selectedInterval = interval
    }

    private fun onSelectionChanged(interval: Interval) {
        viewModel.setInterval(interval)
    }

    private fun onDone() {
        findNavController().popBackStack()
    }
}

