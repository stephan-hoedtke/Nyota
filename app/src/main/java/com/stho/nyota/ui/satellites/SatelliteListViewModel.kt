package com.stho.nyota.ui.satellites

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stho.nyota.ISelectable
import com.stho.nyota.RepositoryViewModelNoArgs
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.Satellite
import com.stho.nyota.sky.universe.Universe

class SatelliteListViewModel(application: Application, repository: Repository) : RepositoryViewModelNoArgs(application, repository), ISelectable<Satellite> {

    private val selectedItemLiveData: MutableLiveData<Satellite?> = MutableLiveData()

    val selectedItemLD: LiveData<Satellite?>
        get() = selectedItemLiveData

    override fun select(item: Satellite) =
        selectedItemLiveData.postValue(item)

    override fun unselect() =
        selectedItemLiveData.postValue(null)
}

