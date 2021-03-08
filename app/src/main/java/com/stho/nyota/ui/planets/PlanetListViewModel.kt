package com.stho.nyota.ui.planets

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stho.nyota.ISelectable
import com.stho.nyota.RepositoryViewModelNoArgs
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.universe.Moon
import com.stho.nyota.sky.universe.Sun
import com.stho.nyota.sky.universe.Universe

class PlanetListViewModel(application: Application, repository: Repository) : RepositoryViewModelNoArgs(application, repository), ISelectable<IElement> {

    private val selectedItemLiveData: MutableLiveData<IElement?> = MutableLiveData()

    val moon: Moon
        get() = repository.solarSystem.moon

    val sun: Sun
        get() = repository.solarSystem.sun

    val selectedItemLD: LiveData<IElement?>
        get() = selectedItemLiveData

    override fun select(item: IElement) =
        selectedItemLiveData.postValue(item)

    override fun unselect() =
        selectedItemLiveData.postValue(null)

}
