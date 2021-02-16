package com.stho.nyota.ui.constellations

import android.app.Application
import android.graphics.Path
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stho.nyota.RepositoryViewModelNoArgs
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.Constellation
import com.stho.nyota.sky.universe.Constellations
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.universe.Universe
import com.stho.nyota.ui.home.HomeViewModel

class ConstellationListViewModel(application: Application, repository: Repository) : RepositoryViewModelNoArgs(application, repository) {

    class Options {
        var filter: Constellations.Filter = Constellations.Filter.Ptolemaeus
    }

    private val optionsLiveData: MutableLiveData<Options> = MutableLiveData<Options>().apply { value = Options() }

    val optionsLD: LiveData<Options>
        get() = optionsLiveData

    val constellationsLD: LiveData<List<Constellation>>
        get() = Transformations.map(optionsLiveData) { options -> createConstellationList(options) }

    val options: Options
        get() = optionsLiveData.value ?: Options()

    fun updateOptions(filter: Constellations.Filter) {
        updateOptions(options.also {
            it.filter = filter
        })
    }

    private fun updateOptions(options: Options) {
        optionsLiveData.postValue(options)
        constellations = createConstellationList(options)
        repository.touchMoment()
    }

    private var constellations: List<Constellation>? = null

    private fun createConstellationList(options: Options): List<Constellation> =
        when (options.filter) {
            Constellations.Filter.IAU -> repository.universe.constellations.values.toList()
            Constellations.Filter.Zodiac -> repository.universe.constellations.values.filter { c -> c.isZodiac }.toList()
            Constellations.Filter.Ptolemaeus -> repository.universe.constellations.values.filter { c -> c.isPtolemaeus }.toList()
        }
}
