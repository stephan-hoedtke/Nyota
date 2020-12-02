package com.stho.nyota

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stho.nyota.sky.universe.IElement
import kotlinx.android.synthetic.main.fragment_moon.view.*

abstract class AbstractElementFragment : AbstractFragment() {

    protected lateinit var basicsAdapter: PropertiesRecyclerViewAdapter
    protected lateinit var detailsAdapter: PropertiesRecyclerViewAdapter

    abstract val element: IElement

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupShowDetails(view)
    }

    private fun setupShowDetails(view: View) {
        view.buttonShowDetails?.setOnClickListener { onShowDetails() }
        abstractViewModel.showDetailsLD.observe(viewLifecycleOwner, Observer { value -> updateShowDetails(value) })
    }

    private fun updateShowDetails(value: Boolean) {
        view?.also {
            it.buttonShowDetails?.setImageResource(if (value) R.drawable.hide else R.drawable.show)
            it.details?.visibility = if (value) View.VISIBLE else View.GONE
        }
    }

    private fun onShowDetails() =
        abstractViewModel.onToggleShowDetails()

    protected fun setupBasics(recyclerView: RecyclerView) {
        basicsAdapter = PropertiesRecyclerViewAdapter()
        basicsAdapter.onItemClick = { p -> openProperty(p) }

        with (recyclerView) {
            this.layoutManager = LinearLayoutManager(context)
            this.adapter = basicsAdapter
            this.addItemDecoration(RecyclerViewItemDivider(context))
        }
    }

    protected fun setupDetails(recyclerView: RecyclerView) {

        detailsAdapter = PropertiesRecyclerViewAdapter()
        detailsAdapter.onItemClick = { p -> openProperty(p) }

        with (recyclerView) {
            this.layoutManager = LinearLayoutManager(context)
            this.adapter = detailsAdapter
            this.addItemDecoration(RecyclerViewItemDivider(context))
        }
    }

    protected fun onSkyView() =
        findNavController().navigate(R.id.action_global_nav_sky, bundleForElement)

    protected fun onFinderView() =
        findNavController().navigate(R.id.action_global_nav_finder, bundleForElement)

    private val bundleForElement: Bundle
        get() = bundleOf("ELEMENT" to element.name)
}

