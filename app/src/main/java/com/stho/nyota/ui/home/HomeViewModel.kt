package com.stho.nyota.ui.home

import android.app.Application
import android.opengl.Visibility
import androidx.lifecycle.*
import com.stho.nyota.RepositoryViewModelNoArgs
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.*
import com.stho.nyota.sky.utilities.Moment

class HomeViewModel(application: Application, repository: Repository) : RepositoryViewModelNoArgs(application, repository) {

    class Options {
        var showStars: Boolean = true
        var showPlanets: Boolean = true
        var showSatellites: Boolean = false
        var showTargets: Boolean = false
        var showInvisibleElements: Boolean = false
    }

    private val optionsLiveData = MutableLiveData<Options>().apply { value = Options() }

    val universe: Universe
        get() = repository.universe

    val moon: Moon
        get() = repository.solarSystem.moon

    val sun: Sun
        get() = repository.solarSystem.sun

    val iss: Satellite
        get() = repository.getSatelliteOrDefault("ISS")

    val targets: List<IElement>
        get() = repository.universe.targets.values.toList()

    val optionsLD: LiveData<Options>
        get() = optionsLiveData

    private var elements: List<IElement>? = null

    val visibleElements: List<IElement>
        get() = with (elements ?: createElementList(options)) {
            return if (options.showInvisibleElements)
                this
            else
                this.filter { element -> element.isVisible }
        }

    val options: Options
        get() = optionsLiveData.value ?: Options()

    fun updateOptions(showStars: Boolean, showPlanets: Boolean, showSatellites: Boolean, showTargets: Boolean, showInvisibleElements: Boolean) {
        updateOptions(options.also {
            it.showStars = showStars
            it.showPlanets = showPlanets
            it.showSatellites = showSatellites
            it.showTargets = showTargets
            it.showInvisibleElements = showInvisibleElements
        })
    }

    private fun updateOptions(options: Options) {
        optionsLiveData.postValue(options)
        elements = createElementList(options)
        repository.touchMoment()
    }

    private fun createElementList(options: Options): List<IElement> {
        val list: ArrayList<IElement> = ArrayList()

        if (options.showStars)
            list.addAll(universe.vip)

        if (options.showPlanets)
            list.addAll(universe.solarSystem.planets)

        if (options.showSatellites)
            list.addAll(universe.satellites.values)

        if (options.showTargets)
            list.addAll(universe.targets.values)

        return list
    }
}
