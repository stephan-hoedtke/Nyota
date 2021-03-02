package com.stho.nyota.ui.constellations

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.stho.nyota.INavigable
import com.stho.nyota.databinding.DialogChooseNextStepBinding
import com.stho.nyota.sky.universe.*


class ChooseNextStepDialog(private val element: IElement, private val parentFragment: INavigable): DialogFragment() {

    // HomeFragment and HomeFragmentOptionsDialog share the view model instance, which is created with the activity as owner.
    private var bindingReference: DialogChooseNextStepBinding? = null
    private val binding: DialogChooseNextStepBinding get() = bindingReference!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = DialogChooseNextStepBinding.inflate(inflater, container, false)

        binding.title.text = element.toString()
        binding.imageOpen.setOnClickListener { onOpen() }
        binding.open.setOnClickListener { onOpen() }
        binding.imageSkyView.setOnClickListener { onSkyView() }
        binding.skyView.setOnClickListener { onSkyView() }
        binding.imageFinderView.setOnClickListener { onFinderView() }
        binding.finder.setOnClickListener { onFinderView() }

        return binding.root
    }

    private fun onOpen() {
        when (element) {
            is Sun -> parentFragment.onSun()
            is Moon -> parentFragment.onMoon()
            is Star -> parentFragment.onStar(element)
            is Constellation -> parentFragment.onConstellation(element)
            is AbstractPlanet -> parentFragment.onPlanet(element)
        }
        dismiss()
    }

    private fun onSkyView() {
        parentFragment.onSkyView(element)
        dismiss()
    }

    private fun onFinderView() {
        parentFragment.onFinderView(element)
        dismiss()
    }
}

