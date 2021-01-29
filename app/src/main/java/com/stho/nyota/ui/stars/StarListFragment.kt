package com.stho.nyota.ui.stars

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.stho.nyota.*
import com.stho.nyota.databinding.FragmentStarListBinding
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.universe.Star
import com.stho.nyota.sky.universe.Universe
import com.stho.nyota.sky.utilities.Moment


class StarListFragment : AbstractFragment() {

    private lateinit var viewModel: StarListViewModel
    private lateinit var adapter: ElementsRecyclerViewAdapter
    private var bindingReference: FragmentStarListBinding? = null
    private val binding: FragmentStarListBinding get() = bindingReference!!

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel(StarListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentStarListBinding.inflate(inflater, container, false)

        adapter = ElementsRecyclerViewAdapter()
        adapter.onItemClick = { element -> openTarget(element) }

        binding.stars.layoutManager = LinearLayoutManager(requireContext())
        binding.stars.adapter = adapter
        binding.stars.addItemDecoration(RecyclerViewItemDivider(requireContext()))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.universeLD.observe(viewLifecycleOwner, { universe -> updateUniverse(universe) })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
    }

    private fun openTarget(element: IElement) {
        when (element) {
            is Star -> openStar(element)
        }
    }

    private fun openStar(star: Star) =
        findNavController().navigate(StarListFragmentDirections.actionNavStarsToNavStar(star.key))

    private fun updateUniverse(universe: Universe) {
        adapter.updateElementsUseNewList(universe.vip)
        bind(universe.moment)
    }

    private fun bind(moment: Moment) {
        bindTime(binding.timeOverlay, moment)
        updateActionBar(getString(R.string.label_stars), toLocalDateString(moment))
    }
}
