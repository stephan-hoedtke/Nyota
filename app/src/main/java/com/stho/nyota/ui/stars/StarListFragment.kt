package com.stho.nyota.ui.stars

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.stho.nyota.*
import com.stho.nyota.sky.universe.*
import com.stho.nyota.sky.utilities.Moment
import kotlinx.android.synthetic.main.fragment_star_list.view.*
import kotlinx.android.synthetic.main.time_overlay.view.*

class StarListFragment : AbstractFragment() {

    private lateinit var viewModel: StarListViewModel
    private lateinit var adapter: ElementsRecyclerViewAdapter

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel(StarListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_star_list, container, false)

        adapter = ElementsRecyclerViewAdapter()
        adapter.onItemClick = { element -> openTarget(element) }

        root.stars.layoutManager = LinearLayoutManager(requireContext())
        root.stars.adapter = adapter
        root.stars.addItemDecoration(RecyclerViewItemDivider(requireContext()))

        viewModel.universeLD.observe(viewLifecycleOwner, Observer { universe -> updateUniverse(universe) })
        return root
    }

    private fun openTarget(element: IElement) {
        when (element) {
            is Star -> openStar(element)
        }
    }

    private fun openStar(star: Star) =
        findNavController().navigate(StarListFragmentDirections.actionNavStarsToNavStar(star.name))

    private fun updateUniverse(universe: Universe) {
        adapter.updateElementsUseNewList(universe.vip)
        bind(universe.moment)
    }

    private fun bind(moment: Moment) {
        view?.also {
            it.currentTime.text = toLocalTimeString(moment)
        }
        updateActionBar(getString(R.string.label_stars), toLocalDateString(moment))
    }
}
