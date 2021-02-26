package com.stho.nyota.ui.constellations

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.stho.nyota.*
import com.stho.nyota.databinding.FragmentConstellationListBinding
import com.stho.nyota.sky.universe.*
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.ui.home.HomeFragment


/**
 * A fragment representing a list of Items.
 */
class ConstellationListFragment : AbstractFragment() {

    // TODO: implement transaction as described here:
    // https://material.io/blog/android-material-motion

    private lateinit var viewModel: ConstellationListViewModel
    private lateinit var adapter: ConstellationListRecyclerViewAdapter
    private var bindingReference: FragmentConstellationListBinding? = null
    private val binding: FragmentConstellationListBinding get() = bindingReference!!

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel(ConstellationListViewModel::class.java)
        setHasOptionsMenu(true)
        loadOptions()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentConstellationListBinding.inflate(inflater, container, false)

        adapter = ConstellationListRecyclerViewAdapter()
        adapter.onItemClick = { constellation -> onConstellation(constellation.key) }
        adapter.onItemLongClick = { constellation -> showNextStepDialogForElement(constellation.key)}

        binding.constellations.layoutManager = LinearLayoutManager(requireContext())
        binding.constellations.adapter = adapter
        binding.constellations.addItemDecoration(RecyclerViewItemDivider(requireContext()))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.universeLD.observe(viewLifecycleOwner, { universe -> updateUniverse(universe) })
        viewModel.constellationsLD.observe(viewLifecycleOwner, { constellations -> onObserveConstellations(constellations) })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_constellation_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_filter -> displayConstellationListFragmentFilterDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        saveOptions()
    }

    private fun loadOptions() {
        requireContext().getSharedPreferences(KEY, Context.MODE_PRIVATE).apply {
            viewModel.updateOptions(
                Constellations.Filter.deserialize(getString(FILTER, null) ?: viewModel.options.filter.serialize())
            )
        }
    }

    private fun saveOptions() {
        requireContext().getSharedPreferences(KEY, Context.MODE_PRIVATE).edit().apply {
            putString(FILTER, viewModel.options.filter.serialize())
            apply()
        }
    }

    private fun updateUniverse(universe: Universe) {
        adapter.notifyDataSetChanged() // Do not change the list. Only attributes of list elements have changed
        bind(universe.moment)
    }

    private fun onObserveConstellations(constellations: List<Constellation>) {
        adapter.updateConstellations(constellations)
    }

    private fun bind(moment: Moment) {
        bindTime(binding.timeOverlay, moment)
        updateActionBar(R.string.label_constellations, toLocalDateString(moment))
    }

    private fun displayConstellationListFragmentFilterDialog() {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val tag = "DIALOG"
        ConstellationListFragmentFilterDialog(viewModel).show(fragmentManager, tag)
    }

    companion object {
        private const val KEY = "ConstellationListFragment"
        private const val FILTER = "Filter"
    }
}

