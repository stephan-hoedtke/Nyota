package com.stho.nyota

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.utilities.IProperty
import com.stho.nyota.sky.utilities.PropertyKeyType

abstract class AbstractElementFragment : AbstractFragment() {

    protected lateinit var basicsAdapter: PropertiesRecyclerViewAdapter
    protected lateinit var detailsAdapter: PropertiesRecyclerViewAdapter
    private var bindingReference: AbstractElementFragmentBinding? = null
    private val binding: AbstractElementFragmentBinding get() = bindingReference!!

    abstract val element: IElement

    private class AbstractElementFragmentBinding(view: View) {
        val buttonShowDetails: ImageView? = view.findViewById<ImageView>(R.id.buttonShowDetails)
        val details: RecyclerView? = view.findViewById<RecyclerView>(R.id.details)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingReference = AbstractElementFragmentBinding(view)
        setupShowDetails()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
    }

    private fun setupShowDetails() {
        binding.buttonShowDetails?.setOnClickListener { onShowDetails() }
        abstractViewModel.showDetailsLD.observe(viewLifecycleOwner, { value -> updateShowDetails(value) })
    }

    private fun updateShowDetails(value: Boolean) {
        binding.buttonShowDetails?.setImageResource(if (value) R.drawable.hide else R.drawable.show)
        binding.details?.visibility = if (value) View.VISIBLE else View.GONE
    }

    private fun onShowDetails() =
        abstractViewModel.onToggleShowDetails()

    protected fun setupBasics(recyclerView: RecyclerView) {
        basicsAdapter = PropertiesRecyclerViewAdapter()
        basicsAdapter.onItemClick = { p -> onPropertyClick(p) }
        basicsAdapter.onItemLongClick = { p -> onPropertyLongClick(p) }

        with (recyclerView) {
            this.layoutManager = LinearLayoutManager(context)
            this.adapter = basicsAdapter
            this.addItemDecoration(RecyclerViewItemDivider(context))
        }
    }

    protected fun setupDetails(recyclerView: RecyclerView) {

        detailsAdapter = PropertiesRecyclerViewAdapter()
        detailsAdapter.onItemClick = { p -> onPropertyClick(p) }
        detailsAdapter.onItemLongClick = { p -> onPropertyLongClick(p) }

        with (recyclerView) {
            this.layoutManager = LinearLayoutManager(context)
            this.adapter = detailsAdapter
            this.addItemDecoration(RecyclerViewItemDivider(context))
        }
    }

    override fun onPropertyClick(property: IProperty) =
        when (property.keyType) {
            PropertyKeyType.AZIMUTH -> onSkyView()
            PropertyKeyType.ALTITUDE -> onSkyView()
            PropertyKeyType.DIRECTION -> onFinderView()
            else -> super.onPropertyClick(property)
        }

    protected fun onSkyView() =
        onSkyView(element)

    protected fun onFinderView() =
        onFinderView(element)

}
