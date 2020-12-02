package com.stho.nyota.ui.satellites

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stho.nyota.RepositoryViewModelArgs
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.Satellite
import com.stho.nyota.sky.universe.TLE
import java.lang.Exception

class SatelliteDownloadElementsViewModel(application: Application, repository: Repository, val satellite: Satellite) : RepositoryViewModelArgs(application, repository) {

    private val errorMessageLiveData = MutableLiveData<String>()
    private val processingStatusLiveData = MutableLiveData<Boolean>()

    init {
        errorMessageLiveData.value = null
        processingStatusLiveData.value = false
    }

    val errorMessageLD: LiveData<String>
        get() = errorMessageLiveData

    val processingStatusLD: LiveData<Boolean>
        get() = processingStatusLiveData

    fun updateElements(elements: String) {
        try {
            satellite.also {
                it.updateElements(elements)
                repository.saveSatellite(getApplication(), it)
            }
        }
        catch (ex: Exception) {
            errorMessageLiveData.postValue(ex.localizedMessage)
        }
    }

    fun setErrorMessage(errorMessage: String) {
        errorMessageLiveData.postValue(errorMessage)
    }

    fun setProcessingStatus(processing: Boolean) {
        processingStatusLiveData.postValue(processing)
    }
}

