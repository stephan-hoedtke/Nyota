package com.stho.nyota

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.AbstractFragment.IAbstractViewModel
import com.stho.nyota.settings.Settings
import com.stho.nyota.sky.universe.Universe

abstract class AbstractViewModel(application: Application, val repository: Repository) : AndroidViewModel(application), IAbstractViewModel {

    private val showDetailsLiveData: MutableLiveData<Boolean> = MutableLiveData()

    init {
        showDetailsLiveData.value = false
    }

    override val interval: Interval
        get() = repository.settings.interval

    override val intervalLD: LiveData<Interval>
        get() = repository.settings.intervalLD

    fun setInterval(interval: Interval) {
        repository.settings.interval = interval
    }

    override val universeLD: LiveData<Universe>
        get() = repository.universeLD

    override val moment: Moment
        get() = repository.universe.moment

    override val settings: Settings
        get() = repository.settings

    override fun onNext() {
        repository.settings.updateTimeAutomatically = false
        repository.next(interval)
    }

    override fun onPrevious() {
        repository.settings.updateTimeAutomatically = false
        repository.previous(interval)
    }

    override fun onReset() {
        repository.settings.updateTimeAutomatically = true
        repository.reset()
    }

    override val showDetailsLD: LiveData<Boolean>
        get() = showDetailsLiveData

    override var showDetails: Boolean
        get() = showDetailsLiveData.value ?: false
        set(value) {
            if (showDetailsLiveData.value != value) {
                showDetailsLiveData.postValue(value)
            }
        }

    override fun onToggleShowDetails() {
        showDetails = !showDetails
    }
}