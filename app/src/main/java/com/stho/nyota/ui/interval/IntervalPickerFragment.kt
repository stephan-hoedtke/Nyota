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
import kotlinx.android.synthetic.main.fragment_interval_picker.view.*

// see here:
// https://www.geeksforgeeks.org/dynamic-radiobutton-in-kotlin/
// with recyclerView and selector
// https://stackoverflow.com/questions/39138315/how-to-highlight-selected-item-in-recyclerview
//

class IntervalPickerFragment : AbstractFragment() {

    lateinit var viewModel: IntervalPickerViewModel
    lateinit var adapter: IntervalPickerRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel(IntervalPickerViewModel::class.java)
        setHasOptionsMenu(true)
    }

    override val abstractViewModel: IAbstractViewModel
        get() = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_interval_picker, container, false)

        adapter = IntervalPickerRecyclerViewAdapter()
        adapter.onSelectionChanged = { interval -> onSelectionChanged(interval) }
        adapter.select(viewModel.interval)

        root.list.layoutManager = LinearLayoutManager(requireContext())
        root.list.adapter = adapter
        root.list.addItemDecoration(RecyclerViewItemDivider(requireContext()))
        root.buttonOK.setOnClickListener { onButtonOK() }

        updateActionBar(getString(R.string.title_choose_interval), "")
        return root
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

