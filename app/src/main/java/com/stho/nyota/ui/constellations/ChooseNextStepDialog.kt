package com.stho.nyota.ui.constellations

import android.R
import android.opengl.Visibility
import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.stho.nyota.createViewModel
import com.stho.nyota.databinding.DialogChooseNextStepBinding
import com.stho.nyota.databinding.FragmentHomeDialogOptionsBinding
import com.stho.nyota.sky.universe.*


class ChooseNextStepDialog(private val element: IElement): DialogFragment() {

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
            is Star -> onStar(element.key)
            is Constellation -> onConstellation(element.key)
            is AbstractPlanet -> onPlanet(element.key)
            is Moon -> onMoon()
            is Sun -> onSun()
        }
        dismiss()
    }

    private fun onSkyView() {
        onSkyView(element.key)
        dismiss()
    }

    private fun onSkyView(key: String) =
        findNavController(this).navigate(com.stho.nyota.R.id.action_global_nav_sky, bundleOf("ELEMENT" to key))

    private fun onFinderView() {
        onFinderView(element.key)
        dismiss()
    }

    private fun onFinderView(key: String) =
        findNavController().navigate(com.stho.nyota.R.id.action_global_nav_finder, bundleOf("ELEMENT" to key))

    private fun onStar(key: String) =
        findNavController().navigate(com.stho.nyota.R.id.action_global_nav_star, bundleOf("STAR" to key))

    private fun onConstellation(key: String) =
        findNavController().navigate(com.stho.nyota.R.id.action_global_nav_constellation, bundleOf("CONSTELLATION" to key))

    private fun onPlanet(key: String) =
        findNavController().navigate(com.stho.nyota.R.id.action_global_nav_planet, bundleOf("PLANET" to key))

    private fun onMoon() =
        findNavController().navigate(com.stho.nyota.R.id.action_global_nav_moon)

    private fun onSun() =
        findNavController().navigate(com.stho.nyota.R.id.action_global_nav_sun)

}

