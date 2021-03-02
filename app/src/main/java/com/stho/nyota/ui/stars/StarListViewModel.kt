package com.stho.nyota.ui.stars

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stho.nyota.ISelectable
import com.stho.nyota.RepositoryViewModelNoArgs
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.Star

class StarListViewModel(application: Application, repository: Repository) : RepositoryViewModelNoArgs(application, repository), ISelectable<Star> {

    private val selectedItemLiveData: MutableLiveData<Star?> = MutableLiveData()

    val selectedItemLD: LiveData<Star?>
        get() = selectedItemLiveData

    override fun select(item: Star) =
        selectedItemLiveData.postValue(item)

    override fun unselect() =
        selectedItemLiveData.postValue(null)
}
